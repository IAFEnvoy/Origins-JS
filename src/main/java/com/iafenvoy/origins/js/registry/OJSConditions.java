package com.iafenvoy.origins.js.registry;

import com.iafenvoy.origins.data.condition.*;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.data.condition.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class OJSConditions {
    public static final DeferredRegister<MapCodec<? extends EntityCondition>> ENTITY_CONDITION_REGISTRY = DeferredRegister.create(ConditionRegistries.ENTITY_CONDITION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BlockCondition>> BLOCK_CONDITION_REGISTRY = DeferredRegister.create(ConditionRegistries.BLOCK_CONDITION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends ItemCondition>> ITEM_CONDITION_REGISTRY = DeferredRegister.create(ConditionRegistries.ITEM_CONDITION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BiEntityCondition>> BIENTITY_CONDITION_REGISTRY = DeferredRegister.create(ConditionRegistries.BI_ENTITY_CONDITION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BiomeCondition>> BIOME_CONDITION_REGISTRY = DeferredRegister.create(ConditionRegistries.BIOME_CONDITION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends DamageCondition>> DAMAGE_CONDITION_REGISTRY = DeferredRegister.create(ConditionRegistries.DAMAGE_CONDITION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends FluidCondition>> FLUID_CONDITION_REGISTRY = DeferredRegister.create(ConditionRegistries.FLUID_CONDITION, OriginsJS.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<JSEntityCondition>> JS_ENTITY_CONDITION = ENTITY_CONDITION_REGISTRY.register("js_entity_condition", () -> JSEntityCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockCondition>, MapCodec<JSBlockCondition>> JS_BLOCK_CONDITION = BLOCK_CONDITION_REGISTRY.register("js_block_condition", () -> JSBlockCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<JSItemCondition>> JS_ITEM_CONDITION = ITEM_CONDITION_REGISTRY.register("js_item_condition", () -> JSItemCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityCondition>, MapCodec<JSBiEntityCondition>> JS_BIENTITY_CONDITION = BIENTITY_CONDITION_REGISTRY.register("js_bientity_condition", () -> JSBiEntityCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiomeCondition>, MapCodec<JSBiomeCondition>> JS_BIOME_CONDITION = BIOME_CONDITION_REGISTRY.register("js_biome_condition", () -> JSBiomeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<JSDamageCondition>> JS_DAMAGE_CONDITION = DAMAGE_CONDITION_REGISTRY.register("js_damage_condition", () -> JSDamageCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends FluidCondition>, MapCodec<JSFluidCondition>> JS_FLUID_CONDITION = FLUID_CONDITION_REGISTRY.register("js_fluid_condition", () -> JSFluidCondition.CODEC);
}
