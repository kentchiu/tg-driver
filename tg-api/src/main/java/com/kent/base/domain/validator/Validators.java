package com.kent.base.domain.validator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.UUID;

public class Validators {

    public static final String NOT_NULL = "NotNull";
    public static final String NOT_BLANK = "NotBlank";
    public static final String MIN = "Min";
    public static final String NOT_IN = "NotIn";
    public static final String EMAIL = "Email";
    public static final String DATE_TIME_FORMAT = "DateTimeFormat";
    public static final String DUPLICATED = "Duplicated";
    public static final String ILLEGAL_API_USAGE = "IllegalApiUsage";
    public static final String RESOURCE_NOT_FOUND = "ResourceNotFound";
    public static final String UNKNOWN = "Unknown";
    public static final String UUID2 = "UUID";
    public static final String MAC = "MAC";


    private static final String NOT_BLANK_MESSAGE = "may not be empty";
    private static final String MIN_MESSAGE = "must be greater than or equal to %d";
    private static final String NOT_IN_MESSAGE = "must one of %s";
    private static final String EMAIL_MESSAGE = "not a well-formed email address";
    private static final String ASSERT_FALSE_MESSAGE = "must be false";
    private static final String ASSERT_TRUE_MESSAGE = "must be true";
    private static final String DECIMAL_MAX_MESSAGE = "must be less than ${inclusive == true ? 'or equal to ' : ''}{value}";
    private static final String DECIMAL_MIN_MESSAGE = "must be greater than ${inclusive == true ? 'or equal to ' : ''}{value}";
    private static final String DIGITS_MESSAGE = "numeric value out of bounds (<{integer} digits>.<{fraction} digits> expected)";
    private static final String FUTURE_MESSAGE = "must be in the future";
    private static final String MAX_MESSAGE = "must be less than or equal to {value}";
    private static final String NOT_NULL_MESSAGE = "may not be null";
    private static final String NULL_MESSAGE = "must be null";
    private static final String PAST_MESSAGE = "must be in the past";
    private static final String PATTERN_MESSAGE = "must match '%s'";
    private static final String DUPLICATED_MESSAGE = "the value of [%s] field is duplicated";
    private static final String SIZE_MESSAGE = "size must be between %d and %d";
    private static final String CREDIT_CARD_NUMBER_MESSAGE = "invalid credit card number";
    private static final String LENGTH_MESSAGE = "length must be between {min} and {max}";
    private static final String NOT_EMPTY_MESSAGE = "may not be empty";
    private static final String RANGE_MESSAGE = "must be between {min} and {max}";
    private static final String SAFE_HTML_MESSAGE = "may have unsafe html content";
    private static final String URL_MESSAGE = "must be a valid URL";
    private static final String UUID2_MESSAGE = "UUID format : 8-4-4-4-12";
    private static final String MAC_MESSAGE = "MAC format : 00:00:00:00:00:00";

    private static ValidatorFactory validatorFactory;

    private Validators() {
    }


    public static void validateMin(Errors errors, String field, int min) {
        int value = 0;
        try {
            Object value1 = errors.getFieldValue(field);
            value = Integer.parseInt(value1.toString());
        } catch (Exception e) {
            value = 0;
        }
        if (value < min) {
            errors.rejectValue(field, MIN, new Object[]{min}, String.format(MIN_MESSAGE, min));
        }
    }

    public static void validateMin(Errors errors, String field, long min) {
        long value = 0;
        try {
            Object value1 = errors.getFieldValue(field);
            value = Long.parseLong(value1.toString());
        } catch (Exception e) {
            value = 0;
        }
        if (value < min) {
            errors.rejectValue(field, MIN, new Object[]{min}, String.format(MIN_MESSAGE, min));
        }
    }

    public static void validateMin(Errors errors, String field, float min) {
        float value = 0;
        try {
            Object value1 = errors.getFieldValue(field);
            value = Float.parseFloat(value1.toString());
        } catch (Exception e) {
            value = 0;
        }
        if (value < min) {
            errors.rejectValue(field, MIN, new Object[]{min}, String.format(MIN_MESSAGE, min));
        }
    }

    public static void validateMin(Errors errors, String field, double min) {
        double value = 0;
        try {
            Object value1 = errors.getFieldValue(field);
            value = Double.parseDouble(value1.toString());
        } catch (Exception e) {
            value = 0;
        }
        if (value < min) {
            errors.rejectValue(field, MIN, new Object[]{min}, String.format(MIN_MESSAGE, min));
        }
    }

    public static void validateEmail(Errors errors, String field) {
        Object value = errors.getFieldValue(field);
        EmailValidator emailValidator = new EmailValidator();
        if (value == null || !emailValidator.validate(value.toString())) {
            errors.rejectValue(field, EMAIL, new Object[]{}, String.format(EMAIL_MESSAGE));
        }
    }

    public static void validateMac(Errors errors, String field) {
        Object value = errors.getFieldValue(field);
        if (value == null || StringUtils.isBlank(value.toString())) {
            return;
        }
        MacValidator validator = new MacValidator();
        if (!validator.validate(value.toString())) {
            errors.rejectValue(field, MAC, new Object[]{}, String.format(MAC_MESSAGE));
        }
    }

    public static void validateBean(Errors errors, Object target) {
        if (validatorFactory == null) {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        SpringValidatorAdapter validator = new SpringValidatorAdapter(validatorFactory.getValidator());
        validator.validate(target, errors);
    }

    public static void validateBoolean(Errors errors, String field) {
        validateNotIn(errors, field, Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }

    public static void validateNotIn(Errors errors, String field, String... values) {
        Object value = errors.getFieldValue(field);
        if (value == null || !ArrayUtils.contains(values, value.toString())) {
            errors.rejectValue(field, NOT_IN, values, String.format(NOT_IN_MESSAGE, ArrayUtils.toString(values)));
        }
    }

    public static void validateNotBlank(Errors errors, String field) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, NOT_BLANK, NOT_BLANK_MESSAGE);

    }


    public static void validateNestedBean(Errors errors, String nestedPath, Object nestedTarget) {
        try {
            errors.pushNestedPath(nestedPath);
            Validators.validateBean(errors, nestedTarget);
        } finally {
            errors.popNestedPath();
        }
    }

    public static void rejectDuplicated(Errors errors, String field) {
        Object value = errors.getFieldValue(field);
        errors.rejectValue(field, DUPLICATED, new Object[]{value}, String.format(DUPLICATED_MESSAGE, field));

    }

    public static void validateKey(Errors errors, String key, String... keys) {
        if (!ArrayUtils.contains(keys, key)) {
            errors.rejectValue(key, Validators.NOT_IN, keys, String.format(NOT_IN_MESSAGE, ArrayUtils.toString(keys)));
        }
    }

    public static void validateUuid(Errors errors, String field) {
        Object value = errors.getFieldValue(field);
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            UUID uuid = null;
            try {
                UUID.fromString(value.toString());
            } catch (Exception e) {
                errors.rejectValue(field, UUID2, UUID2_MESSAGE);
            }
        }
    }

}

