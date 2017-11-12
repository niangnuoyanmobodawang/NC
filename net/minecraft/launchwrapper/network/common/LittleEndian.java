/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.common;

public class LittleEndian {
    public static int littleShort(int number) {
        return (number & 255) << 8 | number >> 8 & 255;
    }

    public static int littleInt(int number) {
        return (number & 255) << 24 | (number & 65280) << 8 | number >> 8 & 65280 | number >> 24 & 255;
    }

    public static long littleLong(long number) {
        return (number & 255) << 56 | (number & 65280) << 40 | (number & 0xFF0000) << 24 | (number & 0xFF000000L) << 8 | number >> 8 & 0xFF000000L | number >> 24 & 0xFF0000 | number >> 40 & 65280 | number >> 56 & 255;
    }
}

