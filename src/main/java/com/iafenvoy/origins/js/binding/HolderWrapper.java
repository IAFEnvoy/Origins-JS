package com.iafenvoy.origins.js.binding;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.iafenvoy.origins.data.power.component.builtin.EntitySetComponent;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.iafenvoy.origins.util.RLHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Wrapper around {@link OriginDataHolder} exposed to JavaScript.
 * Provides a safe, user-friendly API for managing origins and powers.
 */
@SuppressWarnings("unused")
public class HolderWrapper {
    private final OriginDataHolder holder;
    private final RegistryAccess access;

    public HolderWrapper(OriginDataHolder holder) {
        this.holder = holder;
        this.access = holder.getAccess();
    }

    /**
     * Get the underlying OriginDataHolder (for internal use).
     */
    public OriginDataHolder getHolder() {
        return this.holder;
    }

    /**
     * Get the entity this holder belongs to.
     */
    public Entity getEntity() {
        return this.holder.getEntity();
    }

    // ========== Origin Management ==========

    /**
     * Set an origin for a specific layer.
     *
     * @param layerId  The layer ID (e.g., "origins:origin")
     * @param originId The origin ID (e.g., "origins:human")
     */
    public void setOrigin(String layerId, String originId) {
        Holder<Layer> layer = this.getLayer(layerId);
        Holder<Origin> origin = this.getOrigin(originId);
        if (layer != null && origin != null) {
            this.holder.setOrigin(layer, origin);
        }
    }

    /**
     * Clear the origin from a specific layer.
     */
    public void clearOrigin(String layerId) {
        Holder<Layer> layer = this.getLayer(layerId);
        if (layer != null) {
            this.holder.clearOrigin(layer);
        }
    }

    /**
     * Check if the entity has a specific origin (in any layer).
     */
    public boolean hasOrigin(String originId) {
        Holder<Origin> origin = this.getOrigin(originId);
        return origin != null && this.holder.hasOrigin(origin);
    }

    /**
     * Check if the entity has a specific origin in a specific layer.
     */
    public boolean hasOriginInLayer(String layerId, String originId) {
        Holder<Layer> layer = this.getLayer(layerId);
        Holder<Origin> origin = this.getOrigin(originId);
        return layer != null && this.holder.hasOrigin(layer, origin);
    }

    /**
     * Check if the entity has any origin in the given layer.
     */
    public boolean hasLayer(String layerId) {
        Holder<Layer> layer = this.getLayer(layerId);
        return layer != null && this.holder.hasOriginInLayer(layer);
    }

    /**
     * Get the origin ID for a layer, or null if none.
     */
    public String getOriginId(String layerId) {
        Holder<Layer> layer = this.getLayer(layerId);
        if (layer == null) return null;
        Holder<Origin> origin = this.holder.getOrigin(layer);
        return RLHelper.string(origin);
    }

    /**
     * Get all origin-layer pairs as a list of [layerId, originId] lists.
     */
    public List<List<String>> getAllOrigins() {
        List<List<String>> result = new ArrayList<>();
        for (Map.Entry<Holder<Layer>, Holder<Origin>> entry : this.holder.getOrigins().entrySet()) {
            String layerId = RLHelper.string(entry.getKey());
            String originId = RLHelper.string(entry.getValue());
            result.add(List.of(layerId, originId));
        }
        return result;
    }

    /**
     * Check if all layers have origins assigned.
     */
    public boolean hasAllOrigins() {
        return this.holder.hasAllOrigins();
    }

    /**
     * Fill auto-choosing layers with their default origins.
     */
    public boolean fillAutoChoosing() {
        return this.holder.fillAutoChoosing();
    }

    /**
     * Randomly assign an origin for a specific layer.
     */
    public boolean randomOrigin(String layerId) {
        Holder<Layer> layer = this.getLayer(layerId);
        return layer != null && this.holder.randomOrigin(layer);
    }

    // ========== Power Management ==========

    /**
     * Grant a power to the entity.
     *
     * @param source  The source identifier (e.g., "origins_js:script" or "origins:origin")
     * @param powerId The power ID (e.g., "origins:fire_immunity")
     */
    public void grantPower(String source, String powerId) {
        Holder<Power> power = this.getPower(powerId);
        if (power != null) {
            this.holder.grantPower(ResourceLocation.parse(source), power);
        }
    }

    /**
     * Revoke a power from the entity.
     */
    public void revokePower(String source, String powerId) {
        Holder<Power> power = this.getPower(powerId);
        if (power != null) {
            this.holder.revokePower(ResourceLocation.parse(source), power);
        }
    }

    /**
     * Revoke all powers from a specific source.
     */
    public void revokeAllPowers(String source) {
        this.holder.revokeAllPowers(ResourceLocation.parse(source));
    }

    /**
     * Check if the entity has a specific power.
     */
    public boolean hasPower(String powerId) {
        Holder<Power> power = this.getPower(powerId);
        return power != null && this.holder.hasPower(power);
    }

    /**
     * Check if the entity has a specific power from a specific source.
     */
    public boolean hasPowerFromSource(String source, String powerId) {
        Holder<Power> power = this.getPower(powerId);
        return power != null && this.holder.hasPower(ResourceLocation.parse(source), power);
    }

    /**
     * Get all power IDs held by this entity.
     */
    public List<String> getAllPowerIds() {
        return this.holder.getPowers().values().stream().map(RLHelper::string).distinct().toList();
    }

    // ========== Resource ==========

    /**
     * Get the current resource value for a power.
     * @param powerId The power's ResourceLocation (e.g., "origins:climbing")
     * @return The resource value, or 0 if not found
     */
    public int getResource(String powerId) {
        return this.holder.getComponent(ResourceLocation.parse(powerId), ResourceComponent.class)
                .map(ResourceComponent::getValue).orElse(0);
    }

    /**
     * Set the resource value for a power.
     */
    public void setResource(String powerId, int value) {
        this.holder.getComponent(ResourceLocation.parse(powerId), ResourceComponent.class)
                .ifPresent(c -> c.updateResource((prev) -> value));
    }

    /**
     * Add (or subtract, if negative) to the resource value.
     */
    public void addResource(String powerId, int delta) {
        this.holder.getComponent(ResourceLocation.parse(powerId), ResourceComponent.class)
                .ifPresent(c -> c.updateResource(prev -> prev + delta));
    }

    // ========== Cooldown ==========

    /**
     * Get the remaining cooldown ticks.
     * @return The cooldown ticks, or 0 if not found
     */
    public int getCooldown(String powerId) {
        return this.holder.getComponent(ResourceLocation.parse(powerId), CooldownComponent.class)
                .map(CooldownComponent::getValue).orElse(0);
    }

    /**
     * Start the cooldown for a power.
     */
    public void startCooldown(String powerId) {
        this.holder.getComponent(ResourceLocation.parse(powerId), CooldownComponent.class)
                .ifPresent(CooldownComponent::startCooldown);
    }

    /**
     * Check whether the cooldown is ready (can be used).
     */
    public boolean canUseCooldown(String powerId) {
        return this.holder.getComponent(ResourceLocation.parse(powerId), CooldownComponent.class)
                .map(CooldownComponent::canUse).orElse(false);
    }

    // ========== Entity Set ==========

    /**
     * Add an entity (by UUID string) to a power's entity set.
     */
    public void addToEntitySet(String powerId, String uuidStr) {
        this.holder.getComponent(ResourceLocation.parse(powerId), EntitySetComponent.class)
                .ifPresent(c -> c.getSet().put(UUID.fromString(uuidStr), 0));
    }

    /**
     * Remove an entity from a power's entity set.
     */
    public void removeFromEntitySet(String powerId, String uuidStr) {
        this.holder.getComponent(ResourceLocation.parse(powerId), EntitySetComponent.class)
                .ifPresent(c -> c.getSet().remove(UUID.fromString(uuidStr)));
    }

    /**
     * Check if an entity is in a power's entity set.
     */
    public boolean isInEntitySet(String powerId, String uuidStr) {
        return this.holder.getComponent(ResourceLocation.parse(powerId), EntitySetComponent.class)
                .map(c -> c.getSet().containsKey(UUID.fromString(uuidStr))).orElse(false);
    }

    /**
     * Get the number of entities in a power's entity set.
     */
    public int getEntitySetSize(String powerId) {
        return this.holder.getComponent(ResourceLocation.parse(powerId), EntitySetComponent.class)
                .map(c -> c.getSet().size()).orElse(0);
    }

    /**
     * Get all UUID strings in a power's entity set.
     */
    public List<String> getEntitySetMembers(String powerId) {
        return this.holder.getComponent(ResourceLocation.parse(powerId), EntitySetComponent.class)
                .map(c -> c.getSet().keySet().stream().map(UUID::toString).toList())
                .orElse(List.of());
    }

    // ========== Internal Helpers ==========

    private Holder<Origin> getOrigin(String originId) {
        ResourceLocation rl = ResourceLocation.parse(originId);
        Optional<Registry<Origin>> registry = this.access.registry(OriginRegistries.ORIGIN_KEY);
        return registry.flatMap(reg -> reg.getHolder(ResourceKey.create(OriginRegistries.ORIGIN_KEY, rl))).orElse(null);
    }

    private Holder<Layer> getLayer(String layerId) {
        ResourceLocation rl = ResourceLocation.parse(layerId);
        Optional<Registry<Layer>> registry = this.access.registry(LayerRegistries.LAYER_KEY);
        return registry.flatMap(reg -> reg.getHolder(ResourceKey.create(LayerRegistries.LAYER_KEY, rl))).orElse(null);
    }

    private Holder<Power> getPower(String powerId) {
        ResourceLocation rl = ResourceLocation.parse(powerId);
        Optional<Registry<Power>> registry = this.access.registry(PowerRegistries.POWER_KEY);
        return registry.flatMap(reg -> reg.getHolder(ResourceKey.create(PowerRegistries.POWER_KEY, rl))).orElse(null);
    }
}
