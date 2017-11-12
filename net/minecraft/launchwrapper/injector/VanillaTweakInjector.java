/*
 * Decompiled with CFR_Moded_ho3DeBug.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.Display
 *  org.objectweb.asm.ClassReader
 *  org.objectweb.asm.ClassVisitor
 *  org.objectweb.asm.ClassWriter
 *  org.objectweb.asm.Label
 *  org.objectweb.asm.Type
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FieldNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.MethodNode
 */
package net.minecraft.launchwrapper.injector;

import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.lwjgl.opengl.Display;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class VanillaTweakInjector
implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
      
        return bytes;
    }

    public static File inject() {
        System.out.println("Turning off ImageIO disk-caching");
        ImageIO.setUseCache(false);
        VanillaTweakInjector.loadIconsOnFrames();
        System.out.println("Setting gameDir to: " + Launch.minecraftHome);
        return Launch.minecraftHome;
    }

    public static void loadIconsOnFrames() {
        try {
            File smallIcon = new File(Launch.assetsDir, "icons/icon_16x16.png");
            File bigIcon = new File(Launch.assetsDir, "icons/icon_32x32.png");
            System.out.println("Loading current icons for window from: " + smallIcon + " and " + bigIcon);
            Display.setIcon((ByteBuffer[])new ByteBuffer[]{VanillaTweakInjector.loadIcon(smallIcon), VanillaTweakInjector.loadIcon(bigIcon)});
            Frame[] frames = Frame.getFrames();
            if (frames != null) {
                List<Image> icons = Arrays.asList(ImageIO.read(smallIcon), ImageIO.read(bigIcon));
                for (Frame frame : frames) {
                    try {
                        frame.setIconImages(icons);
                        continue;
                    }
                    catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ByteBuffer loadIcon(File iconFile) throws IOException {
        BufferedImage icon = ImageIO.read(iconFile);
        int[] rgb = icon.getRGB(0, 0, icon.getWidth(), icon.getHeight(), null, 0, icon.getWidth());
        ByteBuffer buffer = ByteBuffer.allocate(4 * rgb.length);
        for (int color : rgb) {
            buffer.putInt(color << 8 | color >> 24 & 255);
        }
        buffer.flip();
        return buffer;
    }
}

