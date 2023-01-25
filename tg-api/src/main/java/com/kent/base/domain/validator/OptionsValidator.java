package com.kent.base.domain.validator;


import com.kent.base.domain.Options;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class OptionsValidator implements ConstraintValidator<Options, String[]> {

    private String[] options;

    @Override
    public void initialize(Options constraintAnnotation) {
        this.options = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String[] value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        for (String v : value) {
            boolean present = Arrays.stream(options).filter(o -> o.equals(v)).findAny().isPresent();
            if (!present) {
                return false;
            }
        }


        return true;
    }

}