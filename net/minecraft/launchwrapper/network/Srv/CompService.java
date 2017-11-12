/*
 * Decompiled with CFR_Moded_ho3DeBug.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 */
package net.minecraft.launchwrapper.network.Srv;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.launchwrapper.AESHelper;
import net.minecraft.launchwrapper.network.Srv.KeyService;
import net.minecraft.launchwrapper.network.Srv.Service;
import net.minecraft.launchwrapper.network.common.Common;
import net.minecraft.launchwrapper.network.protocol.ComponentEntity;
import net.minecraft.launchwrapper.network.socket.NetworkSocket;

public class CompService
extends Service {
    public CompService(Integer sid) {
        super(sid);
    }

    @Override
    public void Handle(byte[] buff) {
        byte[] encryptData = buff;
        if (KeyService.streamKey == null) {
            encryptData = buff;
        } else {
            if (KeyService.streamKey.isEmpty() || KeyService.streamKey.length() != 32) {
                return;
            }
            try {
                encryptData = AESHelper.Decrypt(buff, KeyService.streamKey.substring(0, 16).getBytes(), KeyService.streamKey.substring(16, 32).getBytes());
            }
            catch (Exception ex) {
                Common.LogWrapper(ex.toString());
            }
        }
        try {
            byte[] data = Arrays.copyOfRange(encryptData, 2, encryptData.length);
            String json = new String(data, "ISO-8859-1");
            Common.debug("json:" + json);
            Gson parser = new Gson();
            Type listType = new TypeToken<ArrayList<ComponentEntity>>(){}.getType();
            List entities = (List)parser.fromJson(json, listType);
            NetworkSocket.componentKeys.clear();
            Iterator var8 = entities.iterator();

            while(var8.hasNext()) {
               ComponentEntity entity = (ComponentEntity)var8.next();
               NetworkSocket.componentKeys.add(entity);
               Common.debug("[Network] " + entity.toString());
            }


        }
        catch (Exception data) {
            // empty catch block
        }
    }

}

