package com.sydh.common.utils.bean;


import java.util.Set;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

public class BeanValidators {
    public BeanValidators() {
    }

    public static void validateWithException(Validator validator, Object object, Class<?>... groups) throws ConstraintViolationException {
        Set var3 = validator.validate(object, groups);
        if (!var3.isEmpty()) {
            throw new ConstraintViolationException(var3);
        }
    }
}