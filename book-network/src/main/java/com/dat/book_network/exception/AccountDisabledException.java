package com.dat.book_network.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountDisabledException extends AuthenticationException {
    private final String email;
    private final boolean isLocked;
    private final boolean isDisabled;

    public AccountDisabledException(String message) {
        super(message);
        this.email = null;
        this.isLocked = false;
        this.isDisabled = false;
    }

    public AccountDisabledException(String message, String email, boolean isLocked, boolean isDisabled) {
        super(message);
        this.email = email;
        this.isLocked = isLocked;
        this.isDisabled = isDisabled;
    }

    public AccountDisabledException(String message, Throwable cause) {
        super(message, cause);
        this.email = null;
        this.isLocked = false;
        this.isDisabled = false;
    }

    public AccountDisabledException(String message, String email, boolean isLocked, boolean isDisabled, Throwable cause) {
        super(message, cause);
        this.email = email;
        this.isLocked = isLocked;
        this.isDisabled = isDisabled;
    }

    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder(getMessage());
        if (email != null) {
            sb.append(" for email: ").append(email);
        }
        if (isLocked) {
            sb.append(" (Account is locked)");
        }
        if (isDisabled) {
            sb.append(" (Account is disabled)");
        }
        return sb.toString();
    }
}
