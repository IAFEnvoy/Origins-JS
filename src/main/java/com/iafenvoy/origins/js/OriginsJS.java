package com.iafenvoy.origins.js;

import com.iafenvoy.origins.js.registry.OJSActions;
import com.iafenvoy.origins.js.registry.OJSConditions;
import com.iafenvoy.origins.js.registry.OJSPowers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(OriginsJS.MOD_ID)
public class OriginsJS {
    public static final String MOD_ID = "origins_js";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OriginsJS(IEventBus bus) {
        OJSActions.ENTITY_ACTION_REGISTRY.register(bus);
        OJSActions.BLOCK_ACTION_REGISTRY.register(bus);
        OJSActions.ITEM_ACTION_REGISTRY.register(bus);
        OJSActions.BIENTITY_ACTION_REGISTRY.register(bus);
        OJSConditions.ENTITY_CONDITION_REGISTRY.register(bus);
        OJSConditions.BLOCK_CONDITION_REGISTRY.register(bus);
        OJSConditions.ITEM_CONDITION_REGISTRY.register(bus);
        OJSConditions.BIENTITY_CONDITION_REGISTRY.register(bus);
        OJSConditions.BIOME_CONDITION_REGISTRY.register(bus);
        OJSConditions.DAMAGE_CONDITION_REGISTRY.register(bus);
        OJSConditions.FLUID_CONDITION_REGISTRY.register(bus);
        OJSPowers.POWER_TYPE_REGISTRY.register(bus);
    }
}
