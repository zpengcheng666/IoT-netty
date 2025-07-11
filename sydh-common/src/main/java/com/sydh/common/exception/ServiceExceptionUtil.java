package com.sydh.common.exception;

import com.sydh.common.enums.GlobalErrorCodeConstants;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link ServiceException} 工具类
 *
 * 目的在于，格式化异常信息提示。
 * 考虑到 String.format 在参数不正确时会报错，因此使用 {} 作为占位符，并使用 {@link #doFormat(int, String, Object...)} 方法来格式化
 *
 * 因为 {@link #MESSAGES} 里面默认是没有异常信息提示的模板的，所以需要使用方自己初始化进去。
 */
public class ServiceExceptionUtil {

    private static final Logger log = LoggerFactory.getLogger(ServiceExceptionUtil.class);

    /**
     * 错误码与错误信息的映射
     *
     * 这里需要声明 volatile 修饰的原因是，每次启动时，都会调用 {@link #putAll(Map)} 方法，更新其引用。
     * 为了避免可见性的问题，这里需要声明 volatile 修饰。
     */
    private static final ConcurrentMap<Integer, String> MESSAGES = new ConcurrentHashMap<>();

    public static void putAll(Map<Integer, String> messages) {
        MESSAGES.putAll(messages);
    }

    public static void put(Integer code, String message) {
        MESSAGES.put(code, message);
    }

    public static void delete(Integer code, String message) {
        MESSAGES.remove(code, message);
    }

    // ========== 和 ServiceException 的集成 ==========

    public static ServiceException exception(ErrorCode errorCode) {
        String message = MESSAGES.getOrDefault(errorCode.getCode(), errorCode.getMsg());
        return exception0(errorCode.getCode(), message);
    }

    public static ServiceException exception(ErrorCode errorCode, Object... params) {
        String message = MESSAGES.getOrDefault(errorCode.getCode(), errorCode.getMsg());
        return exception0(errorCode.getCode(), message, params);
    }

    /**
     * 创建指定编号的 ServiceException 的异常
     *
     * @param code 编号
     * @return 异常
     */
    public static ServiceException exception(Integer code) {
        return exception0(code, MESSAGES.get(code));
    }

    /**
     * 创建指定编号的 ServiceException 的异常
     *
     * @param code   编号
     * @param params 消息参数
     * @return 异常
     */
    public static ServiceException exception(Integer code, Object... params) {
        return exception0(code, MESSAGES.get(code), params);
    }

    public static ServiceException exception0(Integer code, String messagePattern, Object... params) {
        String message = doFormat(code, messagePattern, params);
        return new ServiceException(code, message);
    }

    public static ServiceException invalidParamException(String messagePattern, Object... params) {
        return exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), messagePattern, params);
    }

    // ========== 格式化方法 ==========

    /**
     * 将错误编号对应的消息使用 params 参数进行格式化。
     *
     * @param code           错误编号
     * @param messagePattern 消息模版
     * @param params         参数
     * @return 格式化后的提示
     */
    @VisibleForTesting
    public static String doFormat(int code, String messagePattern, Object... params) {
        if (messagePattern == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(messagePattern.length() + 50);
        int i = 0;

        // 逐个参数进行替换
        for (int paramIndex = 0; paramIndex < params.length; paramIndex++) {
            int j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][参数过多：错误码({})|错误内容({})|参数({})", 
                         code, messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                }
                sb.append(messagePattern.substring(i));
                return sb.toString();
            }
            
            sb.append(messagePattern, i, j);
            sb.append(params[paramIndex]);
            i = j + 2;
        }
        
        // 检查是否还有未替换的占位符
        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][参数过少：错误码({})|错误内容({})|参数({})", 
                     code, messagePattern, params);
        }
        sb.append(messagePattern.substring(i));
        return sb.toString();
    }
}


