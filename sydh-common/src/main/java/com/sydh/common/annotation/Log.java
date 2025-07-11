package com.sydh.common.annotation;

import com.sydh.common.enums.BusinessType;
import com.sydh.common.enums.OperatorType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
  String title() default "";
  
  BusinessType businessType() default BusinessType.OTHER;
  
  OperatorType operatorType() default OperatorType.MANAGE;
  
  boolean isSaveRequestData() default true;
  
  boolean isSaveResponseData() default true;
}
