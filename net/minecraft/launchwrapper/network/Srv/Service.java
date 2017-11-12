/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.Srv;

import net.minecraft.launchwrapper.network.Srv.IService;

public class Service
implements IService {
    public Integer serviceId;

    public Service(Integer sid) {
        this.serviceId = sid;
    }

    @Override
    public void Handle(byte[] buff) {
    }
}

