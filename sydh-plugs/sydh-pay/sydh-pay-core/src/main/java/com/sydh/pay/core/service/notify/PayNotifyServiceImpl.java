package com.sydh.pay.core.service.notify;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sydh.common.core.domain.CommonResult;
import com.sydh.common.core.domain.PageResult;
import com.sydh.common.mybatis.LambdaQueryWrapperX;
import com.sydh.common.utils.date.DateUtils;
import com.sydh.common.utils.json.JsonUtils;
import com.sydh.pay.api.api.notify.dto.PayOrderNotifyReqDTO;
import com.sydh.pay.api.api.notify.dto.PayRefundNotifyReqDTO;
import com.sydh.pay.api.enums.notify.PayNotifyStatusEnum;
import com.sydh.pay.api.enums.notify.PayNotifyTypeEnum;
import com.sydh.pay.core.controller.admin.notify.vo.PayNotifyTaskPageReqVO;
import com.sydh.pay.core.domain.dataobject.notify.PayNotifyLog;
import com.sydh.pay.core.domain.dataobject.notify.PayNotifyTask;
import com.sydh.pay.core.domain.dataobject.order.PayOrder;
import com.sydh.pay.core.domain.dataobject.refund.PayRefund;
import com.sydh.pay.core.domain.mapper.notify.PayNotifyLogMapper;
import com.sydh.pay.core.domain.mapper.notify.PayNotifyTaskMapper;
import com.sydh.pay.core.service.order.PayOrderService;
import com.sydh.pay.core.service.refund.PayRefundService;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.sydh.common.utils.date.LocalDateTimeUtils.addTime;
import static com.sydh.pay.core.framework.job.config.PayJobConfiguration.NOTIFY_THREAD_POOL_TASK_EXECUTOR;


/**
 * 支付通知 Core Service 实现类
 *
 * @author sydh
 */
@Service
@Valid
@Slf4j
public class PayNotifyServiceImpl implements PayNotifyService {

    /**
     * 通知超时时间，单位：秒
     */
    public static final int NOTIFY_TIMEOUT = 120;
    /**
     * {@link #NOTIFY_TIMEOUT} 的毫秒
     */
    public static final long NOTIFY_TIMEOUT_MILLIS = 120 * DateUtils.SECOND_MILLIS;

    @Resource
    @Lazy // 循环依赖，避免报错
    private PayOrderService payOrderService;
    @Resource
    @Lazy // 循环依赖，避免报错
    private PayRefundService payRefundService;

    @Resource
    private PayNotifyTaskMapper payNotifyTaskMapper;
    @Resource
    private PayNotifyLogMapper payNotifyLogMapper;

    @Resource(name = NOTIFY_THREAD_POOL_TASK_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

//    @Resource
//    private PayNotifyLockRedisDAO notifyLockCoreRedisDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPayNotifyTask(Integer type, Long dataId) {
        PayNotifyTask task = new PayNotifyTask().setType(type).setDataId(dataId);
        task.setStatus(PayNotifyStatusEnum.WAITING.getStatus()).setNextNotifyTime(LocalDateTime.now())
                .setNotifyTimes(0).setMaxNotifyTimes(PayNotifyTask.NOTIFY_FREQUENCY.length + 1);
        // 补充 appId + notifyUrl 字段
        if (Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())) {
            PayOrder order = payOrderService.getOrder(task.getDataId()); // 不进行非空判断，有问题直接异常
            task.setAppId(order.getAppId()).
                    setMerchantOrderId(order.getMerchantOrderId()).setNotifyUrl(order.getNotifyUrl());
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())) {
            PayRefund refundDO = payRefundService.getRefund(task.getDataId());
            task.setAppId(refundDO.getAppId())
                    .setMerchantOrderId(refundDO.getMerchantOrderId()).setNotifyUrl(refundDO.getNotifyUrl());
        }

        // 执行插入
        payNotifyTaskMapper.insert(task);

        // 必须在事务提交后，在发起任务，否则 PayNotifyTask 还没入库，就提前回调接入的业务
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
//                executeNotify(task);
            }
        });
    }

    @Override
    public int executeNotify() throws InterruptedException {
        // 获得需要通知的任务
        List<PayNotifyTask> tasks = payNotifyTaskMapper.selectList(new LambdaQueryWrapper<PayNotifyTask>()
                .in(PayNotifyTask::getStatus, PayNotifyStatusEnum.WAITING.getStatus(),
                        PayNotifyStatusEnum.REQUEST_SUCCESS.getStatus(), PayNotifyStatusEnum.REQUEST_FAILURE.getStatus())
                .le(PayNotifyTask::getNextNotifyTime, LocalDateTime.now()));
        if (CollUtil.isEmpty(tasks)) {
            return 0;
        }

        // 遍历，逐个通知
        CountDownLatch latch = new CountDownLatch(tasks.size());
        tasks.forEach(task -> threadPoolTaskExecutor.execute(() -> {
            try {
//                executeNotify(task);
            } finally {
                latch.countDown();
            }
        }));
        // 等待完成
        awaitExecuteNotify(latch);
        // 返回执行完成的任务数（成功 + 失败)
        return tasks.size();
    }

    /**
     * 等待全部支付通知的完成
     * 每 1 秒会打印一次剩余任务数量
     *
     * @param latch Latch
     * @throws InterruptedException 如果被打断
     */
    private void awaitExecuteNotify(CountDownLatch latch) throws InterruptedException {
        long size = latch.getCount();
        for (int i = 0; i < NOTIFY_TIMEOUT; i++) {
            if (latch.await(1L, TimeUnit.SECONDS)) {
                return;
            }
            log.info("[awaitExecuteNotify][任务处理中， 总任务数({}) 剩余任务数({})]", size, latch.getCount());
        }
        log.error("[awaitExecuteNotify][任务未处理完，总任务数({}) 剩余任务数({})]", size, latch.getCount());
    }

    /**
     * 同步执行单个支付通知
     *
     * @param task 通知任务
     */
//    public void executeNotify(PayNotifyTask task) {
//        // 分布式锁，避免并发问题
//        notifyLockCoreRedisDAO.lock(task.getId(), NOTIFY_TIMEOUT_MILLIS, () -> {
//            // 校验，当前任务是否已经被通知过
//            // 虽然已经通过分布式加锁，但是可能同时满足通知的条件，然后都去获得锁。此时，第一个执行完后，第二个还是能拿到锁，然后会再执行一次。
//            // 因此，此处我们通过第 notifyTimes 通知次数是否匹配来判断
//            PayNotifyTask dbTask = payNotifyTaskMapper.selectById(task.getId());
//            if (ObjectUtil.notEqual(task.getNotifyTimes(), dbTask.getNotifyTimes())) {
//                log.warn("[executeNotifySync][task({}) 任务被忽略，原因是它的通知不是第 ({}) 次，可能是因为并发执行了]",
//                        JsonUtils.toJsonString(task), dbTask.getNotifyTimes());
//                return;
//            }
//
//            // 执行通知
//            getSelf().executeNotify0(dbTask);
//        });
//    }

    @Transactional(rollbackFor = Exception.class)
    public void executeNotify0(PayNotifyTask task) {
        // 发起回调
        CommonResult<?> invokeResult = null;
        Throwable invokeException = null;
        try {
            invokeResult = executeNotifyInvoke(task);
        } catch (Throwable e) {
            invokeException = e;
        }

        // 处理结果
        Integer newStatus = processNotifyResult(task, invokeResult, invokeException);

        // 记录 PayNotifyLog 日志
        String response = invokeException != null ? ExceptionUtil.getRootCauseMessage(invokeException) :
                JsonUtils.toJsonString(invokeResult);
        payNotifyLogMapper.insert(PayNotifyLog.builder().taskId(task.getId())
                .notifyTimes(task.getNotifyTimes() + 1).status(newStatus).response(response).build());
    }

    /**
     * 执行单个支付任务的 HTTP 调用
     *
     * @param task 通知任务
     * @return HTTP 响应
     */
    private CommonResult<?> executeNotifyInvoke(PayNotifyTask task) {
        // 拼接 body 参数
        Object request;
        if (Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())) {
            request = PayOrderNotifyReqDTO.builder().merchantOrderId(task.getMerchantOrderId())
                    .payOrderId(task.getDataId()).build();
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())) {
            request = PayRefundNotifyReqDTO.builder().merchantOrderId(task.getMerchantOrderId())
                    .payRefundId(task.getDataId()).build();
        } else {
            throw new RuntimeException("未知的通知任务类型：" + JsonUtils.toJsonString(task));
        }
        // 拼接 header 参数
        Map<String, String> headers = new HashMap<>();
//        TenantUtils.addTenantHeader(headers, task.getTenantId());

        // 发起请求
        try (HttpResponse response = HttpUtil.createPost(task.getNotifyUrl())
                .body(JsonUtils.toJsonString(request)).addHeaders(headers)
                .timeout((int) NOTIFY_TIMEOUT_MILLIS).execute()) {
            // 解析结果
            return JsonUtils.parseObject(response.body(), CommonResult.class);
        }
    }

    /**
     * 处理并更新通知结果
     *
     * @param task 通知任务
     * @param invokeResult 通知结果
     * @param invokeException 通知异常
     * @return 最终任务的状态
     */
    @VisibleForTesting
    Integer processNotifyResult(PayNotifyTask task, CommonResult<?> invokeResult, Throwable invokeException) {
        // 设置通用的更新 PayNotifyTask 的字段
        PayNotifyTask updateTask = new PayNotifyTask()
                .setId(task.getId())
                .setLastExecuteTime(LocalDateTime.now())
                .setNotifyTimes(task.getNotifyTimes() + 1);

        // 情况一：调用成功
        if (invokeResult != null && invokeResult.isSuccess()) {
            updateTask.setStatus(PayNotifyStatusEnum.SUCCESS.getStatus());
            payNotifyTaskMapper.updateById(updateTask);
            return updateTask.getStatus();
        }

        // 情况二：调用失败、调用异常
        // 2.1 超过最大回调次数
        if (updateTask.getNotifyTimes() >= PayNotifyTask.NOTIFY_FREQUENCY.length) {
            updateTask.setStatus(PayNotifyStatusEnum.FAILURE.getStatus());
            payNotifyTaskMapper.updateById(updateTask);
            return updateTask.getStatus();
        }
        // 2.2 未超过最大回调次数
        updateTask.setNextNotifyTime(addTime(Duration.ofSeconds(PayNotifyTask.NOTIFY_FREQUENCY[updateTask.getNotifyTimes()])));
        updateTask.setStatus(invokeException != null ? PayNotifyStatusEnum.REQUEST_FAILURE.getStatus()
                : PayNotifyStatusEnum.REQUEST_SUCCESS.getStatus());
        payNotifyTaskMapper.updateById(updateTask);
        return updateTask.getStatus();
    }

    @Override
    public PayNotifyTask getNotifyTask(Long id) {
        return payNotifyTaskMapper.selectById(id);
    }

    @Override
    public PageResult<PayNotifyTask> getNotifyTaskPage(PayNotifyTaskPageReqVO pageReqVO) {
        return payNotifyTaskMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<PayNotifyTask>()
                .eqIfPresent(PayNotifyTask::getAppId, pageReqVO.getAppId())
                .eqIfPresent(PayNotifyTask::getType, pageReqVO.getType())
                .eqIfPresent(PayNotifyTask::getDataId, pageReqVO.getDataId())
                .eqIfPresent(PayNotifyTask::getStatus, pageReqVO.getStatus())
                .eqIfPresent(PayNotifyTask::getMerchantOrderId, pageReqVO.getMerchantOrderId())
                .betweenIfPresent(PayNotifyTask::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(PayNotifyTask::getId));
    }

    @Override
    public List<PayNotifyLog> getNotifyLogList(Long taskId) {
        return payNotifyLogMapper.selectList(PayNotifyLog::getTaskId, taskId);
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PayNotifyServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
