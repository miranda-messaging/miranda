package com.ltsllc.miranda.user;

import com.google.gson.*;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import sun.security.rsa.RSAPublicKeyImpl;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

/**
 * Created by Clark on 6/4/2017.
 */
public class JSPublicKeyCreator implements JsonDeserializer<PublicKey> {
    public PublicKey deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            BigInteger n = jsonObject.get("n").getAsBigInteger();
            BigInteger e = jsonObject.get("e").getAsBigInteger();
            return new RSAPublicKeyImpl(n, e);
        } catch (GeneralSecurityException e) {
            Panic panic = new Panic("Exception desierializing public key", e,
                    Panic.Reasons.ExceptionDuringInstantiation);

            Miranda.panicMiranda(panic);
        }

        return null;
    }
}
