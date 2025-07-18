package com.sydh.server.handler;

import com.sydh.base.codec.MessageEncoder;
import com.sydh.base.session.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础消息编码
 * @author bill
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageEncoderWrapper extends ChannelOutboundHandlerAdapter {
    private final MessageEncoder encoder;

    public MessageEncoderWrapper(MessageEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        Packet packet = (Packet) msg;
        ByteBuf output = packet.take();
        try {
            if (output == null) {
                output =  encoder.encode(packet.message, packet.session.getClientId());
            }
            //是否可写，发送消息到客户端
            if (output.isReadable()) {
                ctx.write(packet.wrap(output), promise);
            } else {
                //不可写,释放byteBuf
                output.release();
                ctx.write(packet.wrap(Unpooled.EMPTY_BUFFER), promise);
            }
            output = null;
        } catch (EncoderException e) {
            log.error("消息编码异常" + packet.message, e);
            throw e;
        } catch (Throwable e) {
            log.error("消息编码异常" + packet.message, e);
            throw new EncoderException(e);
        } finally {
            if (output != null) {
                output.release();
            }
        }
    }
}
