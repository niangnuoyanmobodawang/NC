/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.protocol;

public class ComponentEntity {
    public long iid;
    public int version;
    public String key;
    public String name;

    public String toString() {
        return "iid: '" + this.iid + "', version: '" + this.version + "', key: '" + this.key + "'" + "', name: '" + this.name + "'";
    }
}

