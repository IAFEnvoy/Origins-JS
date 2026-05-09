package com.iafenvoy.origins.js.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

public final class JSUtil {
    public static final Codec<JsonObject> JSON_CODEC = Codec.PASSTHROUGH.xmap(
            d -> d.convert(JsonOps.INSTANCE).getValue().getAsJsonObject(),
            o -> new Dynamic<>(JsonOps.INSTANCE, o)
    );
}
