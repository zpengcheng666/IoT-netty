package com.sydh.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;

import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.util.StringUtils;


public class ValidationUtils {
    private static final Pattern aL = Pattern.compile("^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[0,1,4-9])|(?:5[0-3,5-9])|(?:6[2,5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[0-3,5-9]))\\d{8}$");

    private static final Pattern aM = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static final Pattern aN = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    private static final Pattern aO = Pattern.compile("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$");

    public static boolean isMobile(String mobile) {
        return (StringUtils.hasText(mobile) && aL
                .matcher(mobile).matches());
    }

    public static boolean isURL(String url) {
        return (StringUtils.hasText(url) && aM
                .matcher(url).matches());
    }

    public static boolean isXmlNCName(String str) {
        return (StringUtils.hasText(str) && aN
                .matcher(str).matches());
    }

    public static boolean isEmail(String email) {
        return (StringUtils.hasText(email) && aO
                .matcher(email).matches());
    }

    public static void validate(Object object, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Assert.notNull(validator);
        validate(validator, object, groups);
    }

    public static void validate(Validator validator, Object object, Class<?>... groups) {
        Set set = validator.validate(object, groups);
        if (CollUtil.isNotEmpty(set)) {
            throw new ConstraintViolationException(set);
        }
    }
}
