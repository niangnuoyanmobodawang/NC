/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.Srv;

import java.util.HashMap;
import net.minecraft.launchwrapper.network.Srv.CompService;
import net.minecraft.launchwrapper.network.Srv.KeyService;
import net.minecraft.launchwrapper.network.Srv.Service;
import net.minecraft.launchwrapper.network.common.Common;

public class NetDispatcher {
    HashMap<Integer, Service> srvDict = new HashMap();

    public NetDispatcher() {
        this.Register(new KeyService(1284));
        this.Register(new CompService(1028));
    }

    private void Register(Service srv) {
        int id = new Integer(srv.serviceId);
        this.srvDict.put(id, srv);
    }

    public void Dispatch(Integer sid, byte[] data) {
        if (this.srvDict.containsKey(sid)) {
            this.srvDict.get(sid).Handle(data);
        } else {
            Common.debug("sid not exist");
        }
    }
}

