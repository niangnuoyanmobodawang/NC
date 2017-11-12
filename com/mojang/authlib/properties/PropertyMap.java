/*
 * Decompiled with CFR_Moded_ho3DeBug.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingMultimap
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package com.mojang.authlib.properties;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PropertyMap
extends ForwardingMultimap<String, Property> {
    private final Multimap<String, Property> properties = LinkedHashMultimap.create();

    protected Multimap<String, Property> delegate() {
        return this.properties;
    }

    public static class Serializer
    implements JsonSerializer<PropertyMap>,
    JsonDeserializer<PropertyMap> {
        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
    	 public PropertyMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    	      PropertyMap result = new PropertyMap();
    	      if(json instanceof JsonObject) {
    	         JsonObject object = (JsonObject)json;
    	         Iterator element = object.entrySet().iterator();

    	         while(true) {
    	            Entry object1;
    	            do {
    	               if(!element.hasNext()) {
    	                  return result;
    	               }

    	               object1 = (Entry)element.next();
    	            } while(!(object1.getValue() instanceof JsonArray));

    	            Iterator name = ((JsonArray)object1.getValue()).iterator();

    	            while(name.hasNext()) {
    	               JsonElement value = (JsonElement)name.next();
    	               result.put((String) object1.getKey(), new Property((String)object1.getKey(), value.getAsString()));
    	            }
    	         }
    	      } else if(json instanceof JsonArray) {
    	         Iterator object2 = ((JsonArray)json).iterator();

    	         while(object2.hasNext()) {
    	            JsonElement element1 = (JsonElement)object2.next();
    	            if(element1 instanceof JsonObject) {
    	               JsonObject object3 = (JsonObject)element1;
    	               String name1 = object3.getAsJsonPrimitive("name").getAsString();
    	               String value1 = object3.getAsJsonPrimitive("value").getAsString();
    	               if(object3.has("signature")) {
    	                  result.put(name1, new Property(name1, value1, object3.getAsJsonPrimitive("signature").getAsString()));
    	               } else {
    	                  result.put(name1, new Property(name1, value1));
    	               }
    	            }
    	         }
    	      }

    	      return result;
    	   }


        public JsonElement serialize(PropertyMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray result = new JsonArray();
            for (Property property : src.values()) {
                JsonObject object = new JsonObject();
                object.addProperty("name", property.getName());
                object.addProperty("value", property.getValue());
                if (property.hasSignature()) {
                    object.addProperty("signature", property.getSignature());
                }
                result.add((JsonElement)object);
            }
            return result;
        }
    }

}

