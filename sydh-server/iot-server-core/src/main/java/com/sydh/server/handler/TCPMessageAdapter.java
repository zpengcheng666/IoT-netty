package com.sydh.server.handler;

import com.sydh.base.session.Packet;
import com.sydh.base.session.Session;
import com.sydh.base.session.SessionManager;
import com.sydh.base.util.AttributeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * TCP消息适配器,消息进栈-处理步骤2
 *
 * @author bill
 */
@ChannelHandler.Sharable
@Slf4j
public class TCPMessageAdapter extends ChannelInboundHandlerAdapter {

    private final SessionManager sessionManager;

    public TCPMessageAdapter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    /**
     * TCP -1 报文入口
     * 设备端消息响应 ，只处理设备session，报文传递到下一个处理器
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        /*session存储*/
        Session session = getSession(ctx);
        session.access();
        /*将设备端消息传递给下一个处理器*/
        ctx.fireChannelRead(Packet.of(session, buf));
    }

    /**
     * 初次连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("=>设备端连接{}", ctx.channel().remoteAddress());
        /*session存储*/
        Session session = getSession(ctx);
        /*生成上线时间*/
        session.access();
    }

    /**
     * 断开连接，只处理设备状态，报文传递到下一个处理器
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        //连接IP地址
        String host= socketAddress.getAddress().getHostAddress();
        Session session = AttributeUtils.getSession(ctx.channel());
        String clientId = session.getClientId();
        if (session != null) {
            session.setIp(host);
            session.invalidate();
        }
        ctx.channel().close();
        log.info("=>channelInactive,设备[{}]断开连接", clientId);
    }

    /**
     * 处理异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.error("=>设备端断开连接session=[{}],error=[{}]", client(ctx), cause.getMessage());
        } else {
            log.error("=>消息处理异常session=[{}],error=[{}]", client(ctx), cause.getMessage());
        }
    }

    /**
     * TCP客户端心跳处理
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        //连接IP地址
        String host= socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            if (state == IdleState.READER_IDLE || state == IdleState.WRITER_IDLE || state == IdleState.ALL_IDLE) {
                log.error("=>设备心跳超时state=[{}],clientId=[{}]", state, client(ctx));
                ctx.close();
                Session session = AttributeUtils.getSession(ctx.channel());
                if (session != null) {
                    //移除客户端
                    session.setIp(host);
                    // session.invalidate();
                }
            }
        }
    }

    private Session getSession(ChannelHandlerContext context) {
        Channel channel = context.channel();
        Session session = AttributeUtils.getSession(channel);
        if (session == null) {
            session = sessionManager.newInstance(channel);
            AttributeUtils.setSession(channel, session);
        }else {
            //防止设备掉线不同步
            boolean registered = session.isRegistered();
            if (registered){
                //已经注册查询session是否存在
                if (!sessionManager.containKey(session.getClientId())){
                  sessionManager.add(session);
                }
            }
        }
        return session;
    }

    private static Object client(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Session session = AttributeUtils.getSession(channel);
        if (session != null) {
            return session.getClientId();
        }
        return channel.remoteAddress();
    }
}
