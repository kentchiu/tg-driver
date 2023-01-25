package com.kent.base.domain.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MacValidator {
    private static final String MAC_PATTERN = "([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])";
    private final Pattern pattern;
    private Matcher matcher;

    public MacValidator() {
        pattern = Pattern.compile(MAC_PATTERN);
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validate(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();

    }
}
