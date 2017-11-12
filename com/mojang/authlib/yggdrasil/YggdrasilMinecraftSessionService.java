/*
 * Decompiled with CFR_Moded_ho3DeBug.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Iterables
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonParseException
 *  org.apache.commons.codec.Charsets
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.authlib.yggdrasil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.AuthenticationCpp;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.main.Main;
import net.minecraft.launchwrapper.network.common.Common;
import net.minecraft.launchwrapper.network.socket.NetworkSocket;
import nivia.utils.Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilMinecraftSessionService
extends HttpMinecraftSessionService {
    private static final String[] WHITELISTED_DOMAINS = new String[]{".163.com", ".netease.com", ".minecraft.net", ".mojang.com"};
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean forClient = true;
    private static final boolean disabled = false;
    private static final boolean configurable = true;
    public static final AuthenticationCpp auth = new AuthenticationCpp();
    private static final AuthlibUrl authlibUrl = YggdrasilMinecraftSessionService.FindAuthlibUrl();
    private static final URL JOIN_URL_ = HttpAuthenticationService.constantURL(YggdrasilMinecraftSessionService.authlibUrl.AuthServerJoinUrl);
    private static final URL CHECK_URL_ = HttpAuthenticationService.constantURL(YggdrasilMinecraftSessionService.authlibUrl.AuthServerCheckUrl);
    private PublicKey publicKey;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, (Object)new UUIDTypeAdapter()).create();
    private final LoadingCache<GameProfile, GameProfile> insecureProfiles;

    protected YggdrasilMinecraftSessionService(YggdrasilAuthenticationService authenticationService) throws Exception {
        super(authenticationService);
        this.insecureProfiles = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build((CacheLoader)new CacheLoader<GameProfile, GameProfile>(){

            public GameProfile load(GameProfile key) throws Exception {
                return YggdrasilMinecraftSessionService.this.fillGameProfile(key, false);
            }
        });
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray((InputStream)YggdrasilMinecraftSessionService.class.getResourceAsStream("/Cracked.der")));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
        } 
        catch (Exception ignored) {
        	X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray((InputStream)new FileInputStream(new File("Cracked.der"))));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
            throw new Error("Missing/invalid yggdrasil public key!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String ReadFileString(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            String string = sb.toString();
            return string;
        }
        finally {
            br.close();
        }
    }

    private static String GetAuthlibJsonPath() {
        String authlibJsonPath = "authlib.json";
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        System.out.println("OS current temporary directory is " + tempDir);
        Path filePath = Paths.get(tempDir, authlibJsonPath);
        authlibJsonPath = filePath.toString();
        File authlibJson = new File(authlibJsonPath);
        if (authlibJson.exists()) {
            return authlibJsonPath;
        }
        authlibJsonPath = "authlib.json";
        authlibJson = new File(authlibJsonPath);
        return authlibJson.exists() ? authlibJsonPath : "";
    }

    private static AuthlibUrl FindAuthlibUrl() {
        try {
            String authlibJsonPath = YggdrasilMinecraftSessionService.GetAuthlibJsonPath();
            if (authlibJsonPath.isEmpty()) {
                LOGGER.info("Authlib Cracked by ho3_Missing url!");
                return new AuthlibUrl();
            }
            Gson gson = new Gson();
            String urlString = YggdrasilMinecraftSessionService.ReadFileString(authlibJsonPath);
            AuthlibUrl authlibUrl = (AuthlibUrl)gson.fromJson(urlString, AuthlibUrl.class);
            File authlibJson = new File(authlibJsonPath);
            authlibJson.delete();
            return authlibUrl;
        }
        catch (Exception e) {
            LOGGER.error("FindAuthlibUrl Exception! Use the default value");
            return new AuthlibUrl();
        }
    }

    @Override
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        try {
            String portString = Helper.portx;
            if(Integer.parseInt(portString)>10){
            //	auth.LoadLibrary();
            	System.out.println("Hypixel|"+portString);
            if (portString.isEmpty()) {
                throw new AuthenticationException("Unavailable port");
            }
            auth.Authentication(Integer.parseInt(portString), serverId);
            }
        }
        catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        String name = user.getName();
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
        GameProfile result = new GameProfile(uuid, name);
        return result;
    }


    public Map getTextures(GameProfile profile, boolean requireSecure) {
       Property textureProperty = (Property)Iterables.getFirst(profile.getProperties().get("textures"), (Object)null);
       if(textureProperty == null) {
          return new HashMap();
       } else {
          if(requireSecure) {
             if(!textureProperty.hasSignature()) {
                LOGGER.error("Signature is missing from textures payload");
                throw new InsecureTextureException("Signature is missing from textures payload");
             }

             if(!textureProperty.isSignatureValid(this.publicKey)) {
                LOGGER.error("Textures payload has been tampered with (signature invalid)");
                throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
             }
          }

          MinecraftTexturesPayload result;
          try {
             String e = new String(Base64.decodeBase64(textureProperty.getValue()), Charsets.UTF_8);
             result = (MinecraftTexturesPayload)this.gson.fromJson(e, MinecraftTexturesPayload.class);
          } catch (JsonParseException var7) {
             LOGGER.error("Could not decode textures payload", var7);
             return new HashMap();
          }

          if(result.getTextures() == null) {
             return new HashMap();
          } else {
             Iterator e1 = result.getTextures().entrySet().iterator();

             Entry entry;
             do {
                if(!e1.hasNext()) {
                   return result.getTextures();
                }

                entry = (Entry)e1.next();
             } while(isWhitelistedDomain(((MinecraftProfileTexture)entry.getValue()).getUrl()));

             LOGGER.error("Textures payload has been tampered with (non-whitelisted domain)");
             return new HashMap();
          }
       }
    }


    @Override
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
        if (profile.getId() == null) {
            return profile;
        }
        if (!requireSecure) {
            return (GameProfile)this.insecureProfiles.getUnchecked((GameProfile)profile);
        }
        return this.fillGameProfile(profile, true);
    }

    protected GameProfile fillGameProfile(GameProfile profile, boolean requireSecure) {
        return profile;
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService)super.getAuthenticationService();
    }

    private static boolean isWhitelistedDomain(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        }
        catch (URISyntaxException ignored) {
            throw new IllegalArgumentException("Invalid URL '" + url + "'");
        }
        String domain = uri.getHost();
        for (int i = 0; i < WHITELISTED_DOMAINS.length; ++i) {
            if (!domain.endsWith(WHITELISTED_DOMAINS[i])) continue;
            return true;
        }
        return false;
    }

    private static class AuthlibUrl {
        public String AuthServerJoinUrl = "https://x19authserver.nie.netease.com/mark";
        public String AuthServerCheckUrl = "http://x19authserver.nie.netease.com/check";
    }

}

