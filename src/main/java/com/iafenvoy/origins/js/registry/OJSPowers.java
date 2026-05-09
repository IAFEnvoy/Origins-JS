package com.iafenvoy.origins.js.registry;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.data.power.JSPower;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class OJSPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> POWER_TYPE_REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, OriginsJS.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<JSPower>> JS_POWER_TYPE = POWER_TYPE_REGISTRY.register("js_power", () -> JSPower.CODEC);
}
