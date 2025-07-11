package com.sydh.iot.data.listen;


import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeDelayTask;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.data.service.IOtaUpgradeService;
import com.sydh.mq.queue.DelayUpgradeQueue;
import com.sydh.system.domain.SysMenu;
import com.sydh.system.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * OTA升级监听
 * @author bill
 */
@Slf4j
@Component
public class UpGradeListener {

    @Autowired
    private IOtaUpgradeService otaUpgradeService;
    @Resource
    private ISysMenuService menuService;
    /**
     * true: 使用netty搭建的mqttBroker  false: 使用emq
     */
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    
    /**
     * 监听器运行状态标识
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 应用启动完成后启动OTA监听
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("✅ =>应用启动完成，准备启动OTA监听队列");
        // updateMenu();
        startOtaListener();
    }

    /**
     * 异步启动OTA监听任务
     */
    @Async(SYDHConstant.TASK.DELAY_UPGRADE_TASK)
    public void startOtaListener() {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("✅ =>OTA监听任务已经在运行中，跳过重复启动");
            return;
        }
        
        log.info("✅ =>OTA监听队列启动成功");
        listen();
    }

    /**
     * OTA升级监听主循环
     */
    private void listen() {
        while (isRunning.get()) {
            try {
                OtaUpgradeDelayTask task = DelayUpgradeQueue.task();
                if (StringUtils.isNotNull(task)) {
                    Date startTime = task.getStartTime();
                    otaUpgradeService.upgrade(task);
                    log.info("=>开始OTA升级时间{}", startTime);
                } else {
                    // 当获取到null任务时，短暂休眠避免CPU高占用
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("=>OTA升级监听线程被中断，停止监听");
                break;
            } catch (Exception e) {
                log.error("=>OTA升级监听异常", e);
                try {
                    // 异常时短暂休眠，避免连续异常导致CPU高占用
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("=>OTA升级监听线程被中断");
                    break;
                }
            }
        }
        log.info("=>OTA升级监听任务已停止");
    }

    /**
     * 应用关闭前停止监听
     */
    @PreDestroy
    public void shutdown() {
        log.info("=>准备关闭OTA监听队列");
        isRunning.set(false);
        // 给一些时间让监听循环优雅退出
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("=>OTA监听队列已关闭");
    }

    /**
     * 切换菜单中Emqx控制台和netty管理
     */
    public void updateMenu(){
        SysMenu sysMenu = new SysMenu();
        if (enabled){
            sysMenu.setMenuId(2104L);
            sysMenu.setVisible("1");
            menuService.updateMenu(sysMenu);
            sysMenu.setMenuId(3031L);
        }else {
            sysMenu.setMenuId(3031L);
            sysMenu.setVisible("1");
            menuService.updateMenu(sysMenu);
            sysMenu.setMenuId(2104L);
        }
        sysMenu.setVisible("0");
        menuService.updateMenu(sysMenu);
    }
}
