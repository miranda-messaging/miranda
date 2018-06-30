package com.ltsllc.miranda.user;

import com.google.gson.*;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import sun.security.rsa.RSAPublicKeyImpl;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

/**
 * Created by Clark on 6/7/2017.
 */
public class JSPublicKeySerializer implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {
    public PublicKey deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        try {
            if (jsonObject.has("n"))
                return sunPublicKey(jsonObject);
            else
                return null;
        } catch (GeneralSecurityException e) {
            Panic panic = new Panic("Exception deserializing public key", e, Panic.Reasons.ExceptionDeserializing);
            Miranda.panicMiranda(panic);
        }

        return null;
    }

    public PublicKey sunPublicKey(JsonObject jsonObject) throws GeneralSecurityException {
        BigInteger n = jsonObject.get("n").getAsBigInteger();
        BigInteger e = jsonObject.get("e").getAsBigInteger();
        return new RSAPublicKeyImpl(n, e);
    }

    public JsonElement serialize(PublicKey publicKey, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        if (publicKey instanceof BCRSAPublicKey) {
            BCRSAPublicKey bcrsaPublicKey = (BCRSAPublicKey) publicKey;
            jsonObject = serializeBounceyCastlePublicKey(bcrsaPublicKey);
        } else if (publicKey instanceof RSAPublicKeyImpl) {
            RSAPublicKeyImpl rsaPublicKey = (RSAPublicKeyImpl) publicKey;
            jsonObject = serializeSunPublicKey(rsaPublicKey);
        } else {
            Panic panic = new Panic("Unrecognized public key type: " + publicKey.getClass(),
                    Panic.Reasons.UnrecognizedPublicKeyClass);
            Miranda.panicMiranda(panic);
        }

        return jsonObject;
    }

    public JsonObject serializeBounceyCastlePublicKey(BCRSAPublicKey publicKey) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("e", new JsonPrimitive(publicKey.getPublicExponent()));
        jsonObject.add("n", new JsonPrimitive(publicKey.getModulus()));

        return jsonObject;
    }

    public JsonObject serializeSunPublicKey(RSAPublicKeyImpl publicKey) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("e", new JsonPrimitive(publicKey.getPublicExponent()));
        jsonObject.add("n", new JsonPrimitive(publicKey.getModulus()));

        return jsonObject;
    }
}
