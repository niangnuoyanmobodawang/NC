/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package com.mojang.authlib;

enum ErrorCode {
    SUCCESS(0, "(ho3)successfully authentication"),
    UNkNOWN_ERROR(-1, "(ho3)unknown error in authentication"),
    SOCKET_FAILED(1, "(ho3)Create socket failed"),
    CONNECT_FAILED(2, "(ho3)Connect failed"),
    SEND_DATA_ERROR(3, "(ho3)Send data Error!≥¢ ‘÷ÿΩ¯!"),
    AUTHENTICATION_FAILED(4, "(ho3)Authentication response failed"),
    URL_ERROR(5, "(ho3)URL format failed"),
    CREATE_SSL_ERROR(6, "(ho3)InitializeSslContext failed"),
    CONNECT_SSL_ERROR(7, "(ho3)SslConnect failed");
    
    private final int code;
    private final String description;

    private ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCode() {
        return this.code;
    }

    public static ErrorCode GetErrorCode(int code) {
        for (ErrorCode error : ErrorCode.values()) {
            if (error.getCode() != code) continue;
            return error;
        }
        return UNkNOWN_ERROR;
    }
}

