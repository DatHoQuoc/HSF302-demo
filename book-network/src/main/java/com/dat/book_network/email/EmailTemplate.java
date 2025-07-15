package com.dat.book_network.email;

import lombok.Getter;

@Getter
public enum EmailTemplate {

    ACTIVATION_ACCOUNT("activation_account")

    ;
    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }
}
