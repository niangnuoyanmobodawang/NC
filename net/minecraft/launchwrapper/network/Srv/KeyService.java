/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.Srv;

import java.util.Arrays;
import net.minecraft.launchwrapper.network.Srv.Service;
import net.minecraft.launchwrapper.network.common.Common;

public class KeyService
extends Service {
    public static String streamKey;

    public KeyService(Integer id) {
        super(id);
    }

    @Override
    public void Handle(byte[] buff) {
        try {
            byte[] data = Arrays.copyOfRange(buff, 2, buff.length);
            streamKey = new String(data, "UTF-8");
            Common.version = 1;
            Common.debug("key:" + streamKey);
        }
        catch (Exception ex) {
            Common.LogWrapper(ex.toString());
        }
    }
}

