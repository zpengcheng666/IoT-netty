package com.sydh.common.xss;

import com.sydh.common.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class XssValidator
        implements ConstraintValidator<Xss, String> {
    private static final String di = "<(\\S*?)[^>]*>.*?|<.*? />";

    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        return !containsHtml(value);
    }


    public static boolean containsHtml(String value) {
        Pattern pattern = Pattern.compile("<(\\S*?)[^>]*>.*?|<.*? />");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
