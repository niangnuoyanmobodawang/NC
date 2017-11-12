/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package com.mojang.authlib.exceptions;

public class AuthenticationException
extends Exception {
    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}

