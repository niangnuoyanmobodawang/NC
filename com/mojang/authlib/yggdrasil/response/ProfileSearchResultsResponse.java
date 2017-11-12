/*
 * Decompiled with CFR_Moded_ho3DeBug.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 */
package com.mojang.authlib.yggdrasil.response;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.response.Response;
import java.lang.reflect.Type;

public class ProfileSearchResultsResponse
extends Response {
    private GameProfile[] profiles;

    public GameProfile[] getProfiles() {
        return this.profiles;
    }

    public static class Serializer
    implements JsonDeserializer<ProfileSearchResultsResponse> {
        public ProfileSearchResultsResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ProfileSearchResultsResponse result = new ProfileSearchResultsResponse();
            if (json instanceof JsonObject) {
                JsonObject object = (JsonObject)json;
                if (object.has("error")) {
                    result.setError(object.getAsJsonPrimitive("error").getAsString());
                }
                if (object.has("errorMessage")) {
                    result.setError(object.getAsJsonPrimitive("errorMessage").getAsString());
                }
                if (object.has("cause")) {
                    result.setError(object.getAsJsonPrimitive("cause").getAsString());
                }
            } else {
                result.profiles = (GameProfile[])context.deserialize(json, GameProfile[].class);
            }
            return result;
        }
    }

}

