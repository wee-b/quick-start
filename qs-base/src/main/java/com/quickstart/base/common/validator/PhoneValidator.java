package com.quickstart.base.common.validator;


import com.quickstart.base.common.annotation.CheckPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class PhoneValidator implements ConstraintValidator<CheckPhone, String> {

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return value.matches(PHONE_REGEX);
    }
}
