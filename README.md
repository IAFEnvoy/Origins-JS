# Origins JS (KubeJS Addon)

This is a KubeJS integration for [Origins (NeoForge)](https://github.com/IAFEnvoy/Origins-NeoForge), allowing you to
manage
origins and powers via KubeJS.

Note: This is my first time to make a KubeJS addon, most code written by DeepSeek and reviewed by my self. So if you
think some APIs are weird or not well-designed, please let me know.

## Features

- **Origin & Power Management**: Grant, revoke, and check origins/powers from JS scripts.
- **Custom Actions**: Define `EntityAction`, `BlockAction`, `ItemAction`, `BiEntityAction` in JavaScript.
- **Custom Conditions**: Define `EntityCondition`, `BlockCondition`, `ItemCondition`, `BiEntityCondition`,
  `BiomeCondition`, `DamageCondition`, `FluidCondition` in JavaScript.
- **Custom Powers**: Create fully scripted Power types with JS callbacks (`grant`, `revoke`, `tick`, `active`,
  `inactive`).
- **Data-Driven**: All custom types can be referenced in JSON datapacks using the `origins_js:` namespace.

## Dependencies

- [KubeJS](https://modrinth.com/mod/kubejs)
- [Jupiter](https://modrinth.com/mod/jupiter)
- [Origins (NeoForge)](https://modrinth.com/mod/origins-neoforge)

## Quick Start

### 1. Install all dependencies, then place your `.js` scripts in `kubejs/server_scripts/`.

### 2. Manage Origins & Powers

```javascript
// Get the holder for a player or entity
let holder = OriginsJS.getHolder(event.player);

// Grant / revoke powers
holder.grantPower("origins:origin", "origins:fire_immunity");
holder.revokePower("origins:origin", "origins:climbing");

// Check powers
if (holder.hasPower("origins:fire_immunity")) event.player.tell("You are immune to fire!");

// Set / check origins
holder.setOrigin("origins:origin", "origins:human");
if (holder.hasOrigin("origins:human")) event.player.tell("You are a human.");

// Get origin in a layer
let originId = holder.getOriginId("origins:origin");

// Revoke all powers from a source
holder.revokeAllPowers("origins_js:script");

// Random origin for a layer
holder.randomOrigin("origins:origin");

// List all origins
holder.getAllOrigins().forEach(([layer, origin]) => console.log(`Layer ${layer}: ${origin}`));
```

### 3. Register Custom Actions

```javascript
// Entity Action
OriginsJS.registerEntityAction("mypack:greet", (entity) => entity.tell("Hello from KubeJS!"));

// Block Action — receives (level, pos, direction)
OriginsJS.registerBlockAction("mypack:break_notify", (level, pos, dir) => {
    let block = level.getBlockState(pos);
    console.log(`Block ${block} at ${pos}`);
});

// Item Action — receives (level, entity, slotAccess)
OriginsJS.registerItemAction("mypack:modify", (level, entity, slot) => {
    let stack = slot.get();
    if (!stack.isEmpty()) stack.setCount(stack.getCount() + 1);
});

// BiEntity Action — receives (actor, target)
OriginsJS.registerBiEntityAction("mypack:splash", (actor, target) => target.addEffect("minecraft:slowness", 100, 1));

// JSON usage: { "type": "origins_js:js_entity_action", "id": "mypack:greet" }
```

### 4. Register Custom Conditions

```javascript
// Entity Condition
OriginsJS.registerEntityCondition("mypack:low_health", (entity) => entity.health < 10);

// Block Condition — receives (level, pos)
OriginsJS.registerBlockCondition("mypack:is_air", (level, pos) => level.isEmptyBlock(pos));

// Item Condition — receives (level, itemStack)
OriginsJS.registerItemCondition("mypack:is_damaged", (level, stack) => stack.isDamaged());

// BiEntity Condition — receives (actor, target)
OriginsJS.registerBiEntityCondition("mypack:can_see", (actor, target) => actor.canSee(target));

// Biome Condition — receives (biomeHolder, pos)
OriginsJS.registerBiomeCondition("mypack:is_plains", (biome, pos) => biome.is("minecraft:plains"));

// Damage Condition — receives (damageSource, amount)
OriginsJS.registerDamageCondition("mypack:fire_damage", (source, amount) => source.isFire());

// Fluid Condition — receives (fluidState)
OriginsJS.registerFluidCondition("mypack:is_water", (state) => state.is("minecraft:water"));

// JSON usage: { "type": "origins_js:js_entity_condition", "id": "mypack:low_health" }
```

### 5. Register Custom Powers

```javascript
// Builder pattern — chain callbacks then call .register()
// All of them are optional, but you must provide at least a grant/revoke or tick/active/inactive callback, otherwise the power won't do anything
OriginsJS.powerBuilder("mypack:custom_power")
    .grant((holder) => holder.getEntity().tell("Power granted!"))
    .revoke((holder) => holder.getEntity().tell("Power revoked!"))
    .tick((holder) => holder.getEntity().heal(0.5))
    .active((holder) => holder.getEntity().addEffect("minecraft:glowing", 40, 0))
    .inactive((holder) => holder.getEntity().removeEffect("minecraft:glowing"))
    .isActive((holder) => holder.getEntity().isSprinting()) //This is not recommended, use condition field in JSON instead
    .register();
```

Usage in datapack JSON:

```json
{
  "type": "origins_js:js_power",
  "id": "mypack:custom_power",
  "name": "My Custom Power",
  "description": "A power defined in KubeJS scripts"
}
```

## Available JS Action/Condition Types

All custom types use the `origins_js:` namespace prefix:

| JSON `type`                        | Registration Method                        | Args                             |
|------------------------------------|--------------------------------------------|----------------------------------|
| `origins_js:js_entity_action`      | `registerEntityAction`                     | `(entity)`                       |
| `origins_js:js_block_action`       | `registerBlockAction`                      | `(level, pos, direction)`        |
| `origins_js:js_item_action`        | `registerItemAction`                       | `(level, entity, slotAccess)`    |
| `origins_js:js_bientity_action`    | `registerBiEntityAction`                   | `(actor, target)`                |
| `origins_js:js_entity_condition`   | `registerEntityCondition`                  | `(entity) => bool`               |
| `origins_js:js_block_condition`    | `registerBlockCondition`                   | `(level, pos) => bool`           |
| `origins_js:js_item_condition`     | `registerItemCondition`                    | `(level, itemStack) => bool`     |
| `origins_js:js_bientity_condition` | `registerBiEntityCondition`                | `(actor, target) => bool`        |
| `origins_js:js_biome_condition`    | `registerBiomeCondition`                   | `(biomeHolder, pos) => bool`     |
| `origins_js:js_damage_condition`   | `registerDamageCondition`                  | `(damageSource, amount) => bool` |
| `origins_js:js_fluid_condition`    | `registerFluidCondition`                   | `(fluidState) => bool`           |
| `origins_js:js_power`              | `powerBuilder(id).grant/.../...register()` | Builder pattern                  |

## HolderWrapper API Reference

Available methods on the wrapper returned by `OriginsJS.getHolder(entity)`:

### Origin Management

| Method                                | Description                                         |
|---------------------------------------|-----------------------------------------------------|
| `setOrigin(layerId, originId)`        | Assign an origin to a layer                         |
| `clearOrigin(layerId)`                | Remove the origin from a layer                      |
| `hasOrigin(originId)`                 | Check if entity has the given origin (in any layer) |
| `hasOriginInLayer(layerId, originId)` | Check in a specific layer                           |
| `hasLayer(layerId)`                   | Check if the layer has any origin                   |
| `getOriginId(layerId)`                | Get the origin ID string for a layer, or `null`     |
| `getAllOrigins()`                     | Returns `[[layerId, originId], ...]`                |
| `hasAllOrigins()`                     | Whether all layers are assigned                     |
| `fillAutoChoosing()`                  | Fill auto-choosing layers                           |
| `randomOrigin(layerId)`               | Randomly assign an origin                           |

### Power Management

| Method                                | Description                          |
|---------------------------------------|--------------------------------------|
| `grantPower(source, powerId)`         | Grant a power from a source          |
| `revokePower(source, powerId)`        | Revoke a power from a source         |
| `revokeAllPowers(source)`             | Remove all powers from a source      |
| `hasPower(powerId)`                   | Check whether the entity has a power |
| `hasPowerFromSource(source, powerId)` | Check from a specific source         |
| `getAllPowerIds()`                    | List all power ID strings            |

### Static Shortcut Methods

```javascript
OriginsJS.getPlayerHolder(player)   // same as getHolder for players
OriginsJS.grantPower(entity, source, powerId)
OriginsJS.revokePower(entity, source, powerId)
OriginsJS.hasPower(entity, powerId)
OriginsJS.setOrigin(entity, layerId, originId)
OriginsJS.hasOrigin(entity, originId)
OriginsJS.powerBuilder("mypack:power").grant(...).tick(...).register()
```
