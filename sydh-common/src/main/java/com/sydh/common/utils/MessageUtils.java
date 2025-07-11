package com.sydh.common.utils;

import com.sydh.common.utils.spring.SpringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;


public class MessageUtils {
    public static String message(String code, Object... args) {
        MessageSource messageSource = (MessageSource) SpringUtils.getBean(MessageSource.class);
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
