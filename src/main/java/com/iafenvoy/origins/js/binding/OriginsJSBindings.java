package com.iafenvoy.origins.js.binding;

import com.google.gson.JsonObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.js.OriginsJS;
import com.iafenvoy.origins.js.data.action.JSBiEntityAction;
import com.iafenvoy.origins.js.data.action.JSBlockAction;
import com.iafenvoy.origins.js.data.action.JSEntityAction;
import com.iafenvoy.origins.js.data.action.JSItemAction;
import com.iafenvoy.origins.js.data.condition.*;
import com.iafenvoy.origins.js.data.power.JSPower;
import com.iafenvoy.origins.js.util.function.QuadConsumer;
import com.iafenvoy.origins.js.util.function.TriPredicate;
import com.iafenvoy.origins.util.TriConsumer;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@SuppressWarnings("unused")
public class OriginsJSBindings {
    @Info("Get the OriginDataHolder wrapper for an entity.")
    public static HolderWrapper getHolder(Entity entity) {
        if (entity == null) return null;
        try {
            return new HolderWrapper(OriginDataHolder.get(entity));
        } catch (Exception e) {
            OriginsJS.LOGGER.debug("[OriginsJS] Failed to get OriginDataHolder for: {}", entity, e);
        }
        return null;
    }

    @Info("Get the OriginDataHolder wrapper for a player.")
    public static HolderWrapper getPlayerHolder(Player player) {
        return getHolder(player);
    }

    @Info("Grant a power to an entity.")
    public static void grantPower(Entity entity, String source, String powerId) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.grantPower(source, powerId);
    }

    @Info("Revoke a power from an entity.")
    public static void revokePower(Entity entity, String source, String powerId) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.revokePower(source, powerId);
    }

    @Info("Check if an entity has a power.")
    public static boolean hasPower(Entity entity, String powerId) {
        HolderWrapper w = getHolder(entity);
        return w != null && w.hasPower(powerId);
    }

    @Info("Set an origin for an entity.")
    public static void setOrigin(Entity entity, String layerId, String originId) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.setOrigin(layerId, originId);
    }

    @Info("Check if an entity has an origin.")
    public static boolean hasOrigin(Entity entity, String originId) {
        HolderWrapper w = getHolder(entity);
        return w != null && w.hasOrigin(originId);
    }

    // ========== Actions ==========

    @Info("Register entity action. Callback: (entity, params) => void. JSON: origins_js:js_entity_action")
    public static void registerEntityAction(String id, BiConsumer<Entity, JsonObject> action) {
        JSEntityAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered entity action: {}", id);
    }

    @Info("Register block action. Callback: (level, pos, direction, params) => void. JSON: origins_js:js_block_action")
    public static void registerBlockAction(String id, QuadConsumer<Level, BlockPos, Optional<Direction>, JsonObject> action) {
        JSBlockAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered block action: {}", id);
    }

    @Info("Register item action. Callback: (level, entity, slotAccess, params) => void. JSON: origins_js:js_item_action")
    public static void registerItemAction(String id, QuadConsumer<Level, Entity, SlotAccess, JsonObject> action) {
        JSItemAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered item action: {}", id);
    }

    @Info("Register bi-entity action. Callback: (actor, target, params) => void. JSON: origins_js:js_bientity_action")
    public static void registerBiEntityAction(String id, TriConsumer<Entity, Entity, JsonObject> action) {
        JSBiEntityAction.register(id, action);
        OriginsJS.LOGGER.info("[OriginsJS] Registered bi-entity action: {}", id);
    }

    // ========== Conditions ==========

    @Info("Register entity condition. Callback: (entity, params) => bool. JSON: origins_js:js_entity_condition")
    public static void registerEntityCondition(String id, BiPredicate<Entity, JsonObject> predicate) {
        JSEntityCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered entity condition: {}", id);
    }

    @Info("Register block condition. Callback: (level, pos, params) => bool. JSON: origins_js:js_block_condition")
    public static void registerBlockCondition(String id, TriPredicate<Level, BlockPos, JsonObject> predicate) {
        JSBlockCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered block condition: {}", id);
    }

    @Info("Register item condition. Callback: (level, itemStack, params) => bool. JSON: origins_js:js_item_condition")
    public static void registerItemCondition(String id, TriPredicate<Level, ItemStack, JsonObject> predicate) {
        JSItemCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered item condition: {}", id);
    }

    @Info("Register bi-entity condition. Callback: (actor, target, params) => bool. JSON: origins_js:js_bientity_condition")
    public static void registerBiEntityCondition(String id, TriPredicate<Entity, Entity, JsonObject> predicate) {
        JSBiEntityCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered bi-entity condition: {}", id);
    }

    @Info("Register biome condition. Callback: (biomeHolder, pos, params) => bool. JSON: origins_js:js_biome_condition")
    public static void registerBiomeCondition(String id, TriPredicate<Holder<Biome>, BlockPos, JsonObject> predicate) {
        JSBiomeCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered biome condition: {}", id);
    }

    @Info("Register damage condition. Callback: (damageSource, amount, params) => bool. JSON: origins_js:js_damage_condition")
    public static void registerDamageCondition(String id, TriPredicate<DamageSource, Float, JsonObject> predicate) {
        JSDamageCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered damage condition: {}", id);
    }

    @Info("Register fluid condition. Callback: (fluidState, params) => bool. JSON: origins_js:js_fluid_condition")
    public static void registerFluidCondition(String id, BiPredicate<FluidState, JsonObject> predicate) {
        JSFluidCondition.register(id, predicate);
        OriginsJS.LOGGER.info("[OriginsJS] Registered fluid condition: {}", id);
    }

    // ========== Powers ==========

    @Info("Return a PowerBuilder for the given id. Call .grant/.revoke/.tick/.active/.inactive/.isActive() then .register().")
    public static JSPower.PowerBuilder powerBuilder(String id) {
        return JSPower.forId(id);
    }

    // ========== Resource ==========

    @Info("Get resource value for a power. e.g. OriginsJS.getResource(entity, 'origins:climbing')")
    public static int getResource(Entity entity, String powerId) {
        HolderWrapper w = getHolder(entity);
        return w != null ? w.getResource(powerId) : 0;
    }

    @Info("Set resource value for a power.")
    public static void setResource(Entity entity, String powerId, int value) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.setResource(powerId, value);
    }

    @Info("Add to resource value (negative to subtract).")
    public static void addResource(Entity entity, String powerId, int delta) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.addResource(powerId, delta);
    }

    // ========== Cooldown ==========

    @Info("Get remaining cooldown ticks for a power.")
    public static int getCooldown(Entity entity, String powerId) {
        HolderWrapper w = getHolder(entity);
        return w != null ? w.getCooldown(powerId) : 0;
    }

    @Info("Start the cooldown for a power.")
    public static void startCooldown(Entity entity, String powerId) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.startCooldown(powerId);
    }

    @Info("Check if a cooldown is ready to use.")
    public static boolean canUseCooldown(Entity entity, String powerId) {
        HolderWrapper w = getHolder(entity);
        return w != null && w.canUseCooldown(powerId);
    }

    // ========== Entity Set ==========

    @Info("Add an entity to a power's entity set. Use entity.getStringUuid() for uuid.")
    public static void addToEntitySet(Entity entity, String powerId, String uuid) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.addToEntitySet(powerId, uuid);
    }

    @Info("Remove an entity from a power's entity set.")
    public static void removeFromEntitySet(Entity entity, String powerId, String uuid) {
        HolderWrapper w = getHolder(entity);
        if (w != null) w.removeFromEntitySet(powerId, uuid);
    }

    @Info("Check if an entity is in a power's entity set.")
    public static boolean isInEntitySet(Entity entity, String powerId, String uuid) {
        HolderWrapper w = getHolder(entity);
        return w != null && w.isInEntitySet(powerId, uuid);
    }

    @Info("Get the size of a power's entity set.")
    public static int getEntitySetSize(Entity entity, String powerId) {
        HolderWrapper w = getHolder(entity);
        return w != null ? w.getEntitySetSize(powerId) : 0;
    }

    // ========== Utility ==========

    @Info("Clear all JS callbacks (called on /kubejs reload).")
    public static void clearAll() {
        JSEntityAction.clear();
        JSBlockAction.clear();
        JSItemAction.clear();
        JSBiEntityAction.clear();
        JSEntityCondition.clear();
        JSBlockCondition.clear();
        JSItemCondition.clear();
        JSBiEntityCondition.clear();
        JSBiomeCondition.clear();
        JSDamageCondition.clear();
        JSFluidCondition.clear();
        JSPower.clear();
        OriginsJS.LOGGER.info("[OriginsJS] Cleared all JS callbacks");
    }
}
