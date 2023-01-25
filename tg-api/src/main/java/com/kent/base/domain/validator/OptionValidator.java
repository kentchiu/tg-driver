package com.kent.base.domain.validator;


import com.kent.base.domain.Option;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class OptionValidator implements ConstraintValidator<Option, String> {

    private String[] options;

    @Override
    public void initialize(Option constraintAnnotation) {
        this.options = constraintAnnotation.value();
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
        if (value == null || "".equals(value)) {
            return true;
        }
        return Arrays.stream(options).filter(o -> o.equals(value)).findAny().isPresent();
    }
}