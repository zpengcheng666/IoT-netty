package com.sydh.base.session;

import com.sydh.base.service.ISessionStore;
import com.sydh.common.enums.ServerType;
import com.sydh.common.utils.spring.SpringUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Function;

/**
 * session管理
 * @author gsb
 * @date 2022/11/7 8:55
 */
@Slf4j
public class SessionManager {

    private final Class<? extends Enum> sessionKeys;

    private final SessionListener sessionListener;

    /*Session会话存储*/
    private static ISessionStore sessionStore = SpringUtils.getBean(ISessionStore.class);

    public SessionManager(){
        this(null,null);
    }

    public SessionManager(SessionListener sessionListener){
        this(null,sessionListener);
    }

    public SessionManager(Class<? extends Enum> sessionKeys, SessionListener sessionListener){
        this.sessionKeys = sessionKeys;
        this.sessionListener = sessionListener;
    }

    /**
     * 获取Session
     */
    public Session getSession(String clientId) {
        return sessionStore.getSession(clientId);
    }

    public boolean containKey(String clientId){
        return sessionStore.containsKey(clientId);
    }

    /**
     * 获取所有session
     */
    public Collection<Session> all(){
        return sessionStore.getSessionMap().values();
    }

    /**
     * 新建session TCP
     * @return
     */
    public Session newInstance(Channel channel){
        InetSocketAddress sender = (InetSocketAddress) channel.remoteAddress();
        Session session = new Session(this, channel, sender, s -> {
            channel.close();
            return true;
        }, false, ServerType.TCP);
        if (sessionListener != null) {
            try {
                sessionListener.sessionCreated(session);
            } catch (Exception e) {
                log.error("sessionCreated", e);
            }
        }
        return session;
    }

    /**
     * 新建session UDP
     */
    public Session newInstance(Channel channel, InetSocketAddress sender, Function<Session, Boolean> remover) {
        Session session = new Session(this, channel, sender, remover, true,ServerType.UDP);
        if (sessionListener != null) {
            try {
                sessionListener.sessionCreated(session);
            } catch (Exception e) {
                log.error("sessionCreated", e);
            }
        }
        return session;
    }

    /**
     * 设备端离线
     */
    protected void remove(Session session){
        sessionStore.cleanSession(session.getClientId());
        if (null != sessionListener){
            try {
                //设备状态业务处理
                sessionListener.sessionDestroyed(session);
            }catch (Exception e){
                log.error("设备端离线异常",e);
            }
        }
    }

    /**
     * 设备端上线
     */
    public void add(Session session){
        sessionStore.storeSession(session.getClientId().toUpperCase(),session);
        if (null != sessionListener){
            try {
                sessionListener.sessionRegistered(session);
            }catch (Exception e){
                log.error("设备端注册",e);
            }
        }
    }

    public Class<? extends Enum> getSessionKeys(){
        return sessionKeys;
    }

}
