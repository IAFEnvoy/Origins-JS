package com.iafenvoy.origins.js.registry;

import com.iafenvoy.origins.data.action.*;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.data.action.JSBiEntityAction;
import com.iafenvoy.origins.js.data.action.JSBlockAction;
import com.iafenvoy.origins.js.data.action.JSEntityAction;
import com.iafenvoy.origins.js.data.action.JSItemAction;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class OJSActions {
    public static final DeferredRegister<MapCodec<? extends EntityAction>> ENTITY_ACTION_REGISTRY = DeferredRegister.create(ActionRegistries.ENTITY_ACTION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BlockAction>> BLOCK_ACTION_REGISTRY = DeferredRegister.create(ActionRegistries.BLOCK_ACTION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends ItemAction>> ITEM_ACTION_REGISTRY = DeferredRegister.create(ActionRegistries.ITEM_ACTION, OriginsJS.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BiEntityAction>> BIENTITY_ACTION_REGISTRY = DeferredRegister.create(ActionRegistries.BI_ENTITY_ACTION, OriginsJS.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<JSEntityAction>> JS_ENTITY_ACTION = ENTITY_ACTION_REGISTRY.register("js_entity_action", () -> JSEntityAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BlockAction>, MapCodec<JSBlockAction>> JS_BLOCK_ACTION = BLOCK_ACTION_REGISTRY.register("js_block_action", () -> JSBlockAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<JSItemAction>> JS_ITEM_ACTION = ITEM_ACTION_REGISTRY.register("js_item_action", () -> JSItemAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<JSBiEntityAction>> JS_BIENTITY_ACTION = BIENTITY_ACTION_REGISTRY.register("js_bientity_action", () -> JSBiEntityAction.CODEC);
}
