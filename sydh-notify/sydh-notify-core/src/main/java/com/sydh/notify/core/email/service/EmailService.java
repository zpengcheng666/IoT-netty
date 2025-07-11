package com.sydh.notify.core.email.service;

import com.sydh.common.extend.core.domin.notify.NotifySendResponse;
import com.sydh.notify.vo.NotifyVO;

/**
 * @description: 邮箱发送业务类
 * @author fastb
 * @date 2023-12-29 16:20
 * @version 1.0
 */
public interface EmailService {

    /**
     * @description:  邮件简要内容发送
     * @param: notifyVO 发送VO类
     * @return: void
     */
    NotifySendResponse send(NotifyVO notifyVO);

}
