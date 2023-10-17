package com.spyro.messenger.emailverification.misc;

public abstract class ConfirmationUrls {
    public static final String CONFIRMATION_PARAMETER_NAME = "token";
    public static final String CONFIRMATION_PREFIX = "/confirm";
    public static final String CONFIRMATION_URL = "/api/v1" + CONFIRMATION_PREFIX;
    public static final String FULL_CONFIRMATION_URL_PATTERN = new StringBuilder("%s")
            .append(CONFIRMATION_URL)
            .append("%s?")
            .append(CONFIRMATION_PARAMETER_NAME)
            .append('=')
            .append("%s")
            .toString();
    public static final String CONFIRM_REGISTRATION_URL = "/activate-account";
    public static final String CONFIRM_EMAIL_CHANGE_URL = "/change-email";
}
