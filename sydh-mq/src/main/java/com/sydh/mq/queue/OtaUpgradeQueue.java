package com.sydh.mq.queue;

import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeBo;
import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * OTA升级列队 {@link OtaUpgradeBo}
 * @author gsb
 * @date 2022/10/10 10:30
 */
public class OtaUpgradeQueue {
    private static final LinkedBlockingQueue<OtaUpgradeBo> queue = new LinkedBlockingQueue<>();

    /*元素加入队列,最后*/
    public static void offer(OtaUpgradeBo dto){
        queue.offer(dto);
    }
    /*取出队列元素 先进先出*/
    @SneakyThrows
    public static OtaUpgradeBo take(){
        return queue.take();
    }
}
