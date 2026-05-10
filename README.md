# Origins JS

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
- **Parameters Support**: Pass custom parameters from JSON to your JS callbacks for dynamic behavior and high
  reusability.

## Dependencies

- [KubeJS](https://modrinth.com/mod/kubejs)
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

// Resource / Cooldown / Entity Set
holder.setResource("origins:climbing", 20);
holder.startCooldown("origins:fireball");
holder.addToEntitySet("mypack:friends", event.player.getStringUuid());
```

### 3. Register Custom Actions

```javascript
// Entity Action — callback: (entity, params)
OriginsJS.registerEntityAction("mypack:greet", (entity, params) => {
    let msg = params.message || "Hello!";
    entity.tell(msg);
});

// Block Action — callback: (level, pos, direction, params)
OriginsJS.registerBlockAction("mypack:break_notify", (level, pos, dir, params) => {
    console.log(`Block at ${pos}, check: ${params.check}`);
});

// Item Action — callback: (level, entity, slotAccess, params)
OriginsJS.registerItemAction("mypack:modify", (level, entity, slot, params) => {
    let count = params.count || 1;
    let stack = slot.get();
    if (!stack.isEmpty()) stack.setCount(stack.getCount() + count);
});

// BiEntity Action — callback: (actor, target, params)
OriginsJS.registerBiEntityAction("mypack:splash", (actor, target, params) => {
    let duration = params.duration || 100;
    target.addEffect("minecraft:slowness", duration, 1);
});

// JSON usage:
// { "type": "origins_js:js_entity_action", "id": "mypack:greet", "params": { "message": "Hi!" } }
```

### 4. Register Custom Conditions

```javascript
// Entity Condition — callback: (entity, params) => bool
OriginsJS.registerEntityCondition("mypack:low_health", (entity, params) => {
    let threshold = params.threshold || 10;
    return entity.health < threshold;
});

// Block Condition — callback: (level, pos, params) => bool
OriginsJS.registerBlockCondition("mypack:is_block", (level, pos, params) => {
    let blockId = params.block || "minecraft:air";
    return level.getBlockState(pos).is(blockId);
});

// JSON usage:
// { "type": "origins_js:js_entity_condition", "id": "mypack:low_health", "params": { "threshold": 5 } }
```

### 5. Register Custom Powers

```javascript
// All callbacks receive (holder, params) where params comes from JSON
OriginsJS.powerBuilder("mypack:custom_power")
    .grant((holder, params) => holder.getEntity().tell(params.msg || "Granted!"))
    .revoke((holder, params) => holder.getEntity().tell("Revoked!"))
    .tick((holder, params) => holder.getEntity().heal(params.amount || 0.5))
    .register();
```

Usage in datapack JSON:

```json
{
  "type": "origins_js:js_power",
  "id": "mypack:custom_power",
  "name": "My Custom Power",
  "description": "A power defined in KubeJS scripts",
  "params": {
    "msg": "Hello custom power!",
    "amount": 1.0
  }
}
```

## Available JS Action/Condition Types

All custom types use the `origins_js:` namespace prefix:

| JSON `type`                        | Registration Method                        | Callback Args                              |
|------------------------------------|--------------------------------------------|--------------------------------------------|
| `origins_js:js_entity_action`      | `registerEntityAction`                     | `(entity, params)`                         |
| `origins_js:js_block_action`       | `registerBlockAction`                      | `(level, pos, direction, params)`          |
| `origins_js:js_item_action`        | `registerItemAction`                       | `(level, entity, slotAccess, params)`      |
| `origins_js:js_bientity_action`    | `registerBiEntityAction`                   | `(actor, target, params)`                  |
| `origins_js:js_entity_condition`   | `registerEntityCondition`                  | `(entity, params) => bool`                 |
| `origins_js:js_block_condition`    | `registerBlockCondition`                   | `(level, pos, params) => bool`             |
| `origins_js:js_item_condition`     | `registerItemCondition`                    | `(level, itemStack, params) => bool`       |
| `origins_js:js_bientity_condition` | `registerBiEntityCondition`                | `(actor, target, params) => bool`          |
| `origins_js:js_biome_condition`    | `registerBiomeCondition`                   | `(biomeHolder, pos, params) => bool`       |
| `origins_js:js_damage_condition`   | `registerDamageCondition`                  | `(damageSource, amount, params) => bool`   |
| `origins_js:js_fluid_condition`    | `registerFluidCondition`                   | `(fluidState, params) => bool`             |
| `origins_js:js_power`              | `powerBuilder(id).grant/.../...register()` | Builder: `.grant((holder, params) => ...)` |

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

### Resource (Integer resource bar)

| Method                          | Description                        |
|---------------------------------|------------------------------------|
| `getResource(powerId)`          | Get the current resource value     |
| `setResource(powerId, value)`   | Set the resource to a specific int |
| `addResource(powerId, delta)`   | Add/subtract to the resource       |

### Cooldown (Usage cooldown timer)

| Method                     | Description                          |
|----------------------------|--------------------------------------|
| `getCooldown(powerId)`     | Get remaining cooldown ticks         |
| `startCooldown(powerId)`   | Start the cooldown                   |
| `canUseCooldown(powerId)`  | Check whether the cooldown is ready  |

### Entity Set (Per-power entity tracking set)

| Method                                     | Description                               |
|--------------------------------------------|-------------------------------------------|
| `addToEntitySet(powerId, uuid)`            | Add an entity (by UUID string)            |
| `removeFromEntitySet(powerId, uuid)`       | Remove an entity                          |
| `isInEntitySet(powerId, uuid)`             | Check if an entity is in the set          |
| `getEntitySetSize(powerId)`                | Get the number of entities in the set     |
| `getEntitySetMembers(powerId)`             | Get all UUID strings in the set           |

### Static Shortcut Methods

```javascript
OriginsJS.getPlayerHolder(player)
OriginsJS.grantPower(entity, source, powerId)
OriginsJS.revokePower(entity, source, powerId)
OriginsJS.hasPower(entity, powerId)
OriginsJS.setOrigin(entity, layerId, originId)
OriginsJS.hasOrigin(entity, originId)
OriginsJS.powerBuilder("mypack:power").grant(...).tick(...).register()

// Resource
OriginsJS.getResource(entity, powerId)
OriginsJS.setResource(entity, powerId, value)
OriginsJS.addResource(entity, powerId, delta)

// Cooldown
OriginsJS.getCooldown(entity, powerId)
OriginsJS.startCooldown(entity, powerId)
OriginsJS.canUseCooldown(entity, powerId)

// Entity Set
OriginsJS.addToEntitySet(entity, powerId, uuid)
OriginsJS.removeFromEntitySet(entity, powerId, uuid)
OriginsJS.isInEntitySet(entity, powerId, uuid)
OriginsJS.getEntitySetSize(entity, powerId)
```
