/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package com.mojang.authlib;

import com.mojang.authlib.GameProfile;

public interface ProfileLookupCallback {
    public void onProfileLookupSucceeded(GameProfile var1);

    public void onProfileLookupFailed(GameProfile var1, Exception var2);
}

