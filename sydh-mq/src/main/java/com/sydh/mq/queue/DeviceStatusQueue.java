package com.sydh.mq.queue;

import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 设备消息缓存队列 添加{@link DeviceStatusBo} 消息
 * @author gsb
 * @date 2022/10/10 9:59
 */
public class DeviceStatusQueue {
    private static final LinkedBlockingQueue<DeviceStatusBo> queue = new LinkedBlockingQueue<>();

    /*元素加入队列,最后*/
    public static void offer(DeviceStatusBo dto){
        queue.offer(dto);
    }
    /*取出队列元素 先进先出*/
    @SneakyThrows
    public static DeviceStatusBo take(){
        return queue.take();
    }
}
