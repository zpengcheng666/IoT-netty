package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.iot.data.consumer.FunctionInvokeConsumer;
import com.sydh.mq.queue.FunctionInvokeQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 设备服务下发监听
 *
 * @author bill
 */
@Slf4j
@Component
public class FunctionInvokeListen {

    @Autowired
    private FunctionInvokeConsumer functionInvokeConsumer;

    @Async(SYDHConstant.TASK.MESSAGE_CONSUME_TASK)
    public void listen() {
        while (true) {
            try {
                MQSendMessageBo sendBo = FunctionInvokeQueue.take();
                functionInvokeConsumer.handler(sendBo);
            } catch (Exception e) {
                log.error("=>下发服务消费异常", e);
            }
        }
    }
}
