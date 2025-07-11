package com.sydh.common.annotation;

import com.sydh.common.enums.LimitType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
  String key() default "rate_limit:";
  
  int time() default 60;
  
  int count() default 100;
  
  LimitType limitType() default LimitType.DEFAULT;
}
