/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class ValidateRequest {
    private String clientToken;
    private String accessToken;

    public ValidateRequest(YggdrasilUserAuthentication authenticationService) {
        this.clientToken = authenticationService.getAuthenticationService().getClientToken();
        this.accessToken = authenticationService.getAuthenticatedToken();
    }
}

