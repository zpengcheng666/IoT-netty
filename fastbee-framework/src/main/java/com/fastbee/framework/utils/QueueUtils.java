/*     */ package com.fastbee.framework.utils;
/*     */ 
/*     */ import com.fastbee.common.utils.spring.SpringUtils;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.Consumer;
/*     */ import org.redisson.api.RBlockingQueue;
/*     */ import org.redisson.api.RBoundedBlockingQueue;
/*     */ import org.redisson.api.RDelayedQueue;
/*     */ import org.redisson.api.RPriorityBlockingQueue;
/*     */ import org.redisson.api.RQueue;
/*     */ import org.redisson.api.RedissonClient;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QueueUtils
/*     */ {
/*  22 */   private static final RedissonClient CLIENT = (RedissonClient)SpringUtils.getBean(RedissonClient.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RedissonClient getClient() {
/*  29 */     return CLIENT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean addQueueObject(String queueName, T data) {
/*  39 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/*  40 */     return queue.offer(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getQueueObject(String queueName) {
/*  49 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/*  50 */     return (T)queue.poll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean removeQueueObject(String queueName, T data) {
/*  57 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/*  58 */     return queue.remove(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean destroyQueue(String queueName) {
/*  65 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/*  66 */     return queue.delete();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> void addDelayedQueueObject(String queueName, T data, long time) {
/*  77 */     addDelayedQueueObject(queueName, data, time, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> void addDelayedQueueObject(String queueName, T data, long time, TimeUnit timeUnit) {
/*  89 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/*  90 */     RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue((RQueue)queue);
/*  91 */     delayedQueue.offer(data, time, timeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getDelayedQueueObject(String queueName) {
/* 100 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/* 101 */     RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue((RQueue)queue);
/* 102 */     return (T)delayedQueue.poll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean removeDelayedQueueObject(String queueName, T data) {
/* 109 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/* 110 */     RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue((RQueue)queue);
/* 111 */     return delayedQueue.remove(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> void destroyDelayedQueue(String queueName) {
/* 118 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/* 119 */     RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue((RQueue)queue);
/* 120 */     delayedQueue.destroy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean addPriorityQueueObject(String queueName, T data) {
/* 130 */     RPriorityBlockingQueue<T> priorityBlockingQueue = CLIENT.getPriorityBlockingQueue(queueName);
/* 131 */     return priorityBlockingQueue.offer(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getPriorityQueueObject(String queueName) {
/* 140 */     RPriorityBlockingQueue<T> queue = CLIENT.getPriorityBlockingQueue(queueName);
/* 141 */     return (T)queue.poll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean removePriorityQueueObject(String queueName, T data) {
/* 148 */     RPriorityBlockingQueue<T> queue = CLIENT.getPriorityBlockingQueue(queueName);
/* 149 */     return queue.remove(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean destroyPriorityQueue(String queueName) {
/* 156 */     RPriorityBlockingQueue<T> queue = CLIENT.getPriorityBlockingQueue(queueName);
/* 157 */     return queue.delete();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean trySetBoundedQueueCapacity(String queueName, int capacity) {
/* 167 */     RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
/* 168 */     return boundedBlockingQueue.trySetCapacity(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean trySetBoundedQueueCapacity(String queueName, int capacity, boolean destroy) {
/* 179 */     RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
/* 180 */     if (boundedBlockingQueue.isExists() && destroy) {
/* 181 */       destroyQueue(queueName);
/*     */     }
/* 183 */     return boundedBlockingQueue.trySetCapacity(capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean addBoundedQueueObject(String queueName, T data) {
/* 194 */     RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
/* 195 */     return boundedBlockingQueue.offer(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T getBoundedQueueObject(String queueName) {
/* 204 */     RBoundedBlockingQueue<T> queue = CLIENT.getBoundedBlockingQueue(queueName);
/* 205 */     return (T)queue.poll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean removeBoundedQueueObject(String queueName, T data) {
/* 212 */     RBoundedBlockingQueue<T> queue = CLIENT.getBoundedBlockingQueue(queueName);
/* 213 */     return queue.remove(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> boolean destroyBoundedQueue(String queueName) {
/* 220 */     RBoundedBlockingQueue<T> queue = CLIENT.getBoundedBlockingQueue(queueName);
/* 221 */     return queue.delete();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> void subscribeBlockingQueue(String queueName, Consumer<T> consumer, boolean isDelayed) {
/* 228 */     RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
/* 229 */     if (isDelayed)
/*     */     {
/* 231 */       CLIENT.getDelayedQueue((RQueue)queue);
/*     */     }
/* 233 */     queue.subscribeOnElements(consumer);
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framewor\\utils\QueueUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */