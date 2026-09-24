I’ll map the current machine, item, and rendering code first, then propose a structure for fixed-step simulation, optional smooth visuals, and adding new machine content. I’ll focus on changes that support large factories and keep this read-only.

The main issue is separation: `StorageBlock` currently blocks the next item until its intake animation clears `item`, so visual behavior can change factory throughput. Also, conveyors now transfer directly during their own ticks, which reintroduces update-order dependence.

The proposed structure will separate simulation activity, visual animation, and visibility so those concerns can be optimized independently.For the initial architecture and benchmarks, what scale should we design around? These are test targets, not guaranteed performance.
- 100,000 placed machines, with many idle
- 10,000 mostly active machines
- Eventually a million-plus belt tiles

When smooth item visuals are disabled, what should the player see?
- Exact fixed-tick positions, with no extra visual updates
- Constant-speed visual movement; disable only easing

request_user_input_async

Compact conversation

Your `Block` → runtime `Machine` split is worth keeping. The next layer should separate machine logic, shared transfer resolution, and item presentation. I’ll also separate “needs a tick” from “needs drawing,” so blocked belts can sleep without their items disappearing.


The first migration should be storage intake: commit the inventory change during the fixed tick, then animate a separate cosmetic record. That gives you a testable rule—FPS, smoothing, and camera visibility must never change item counts or transfer timing.

I’ll keep the proposed structure incremental, with stable interfaces that let you optimize conveyor internals later.


I recommend keeping your **`Block` definition → runtime `Machine`** foundation, but separating three responsibilities:

**Machines decide what they want to do. A shared simulation resolves transfers. Presentation decides how items look.**

That gives you room for more content without filling `FactorySystem` with machine-specific branches. I checked the current files; nothing was edited.

## 1. What needs changing first

Your current code has several boundaries mixed together:

- [StorageBlock.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/machines/cores/StorageBlock.java:124>) rejects input while `this.item` exists, but its visual update clears that field. **Animation therefore controls storage throughput.**
- [Conveyor.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/machines/cores/Conveyor.java:75>) transfers directly while advancing its item. Results can depend on which conveyor updates first.
- [MachineGroup.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/MachineGroup.java:28>) uses the same active collection for simulation, visual updates, and drawing. Those need different membership rules.
- [Machine.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/Machine.java:124>) contains visual methods that also modify logical progress. That must not be part of the base contract.

These are correctness issues to resolve before optimizing larger factories.

## 2. Separate the data by ownership

### Shared content definitions

Your `Block` should describe a **type of machine**, shared by all instances:

- Stable content ID.
- Footprint and ports.
- Capacity, conveyor speed, production interval.
- Runtime factory.
- Rendering configuration identifiers.

Position, rotation, inventory, and current production progress belong to the placed `Machine`, not the shared definition.

Normal and fast conveyors can share the same runtime implementation with different configuration. You do not need another behavior class for every content tier.

### Runtime machine state

Keep `Machine` relatively small:

- Machine ID and definition reference.
- Tile position and direction.
- Lifecycle and scheduling information.
- Only the state/modules that machine actually needs.

Use composition for reusable capabilities:

- `Inventory`: counted items.
- `TransportState`: items moving along a conveyor.
- `RecipeState`: processing progress, later.
- `PowerState`: energy consumption, later.

**A common item-transfer API does not require every machine to use the same storage representation.**

A chest needs item counts. A conveyor needs ordered moving loads. A producer needs an output buffer. All can expose compatible inputs and outputs.

### Separate stored items, transported items, and visuals

Use three distinct concepts:

| Concept | Contains | Authoritative? |
|---|---|---|
| Inventory | Item type → quantity | Yes |
| Transported load | Item type, owner/path, progress | Yes |
| Item visual | Display position, animation/path-following state | No |

Do not create one moving `Item` object for every item inside a chest.

A visual record is also **not a ground item**. It has no inventory ownership, collision, or pickup behavior.

## 3. Suggested project structure

This is a target structure to migrate toward gradually—not a request to create every class immediately.

```text
core/
├─ blocks/
│  └─ Blocks.java                     # Content registration
│
├─ machine/
│  ├─ definition/
│  │  └─ Block.java                    # Shared machine configuration
│  │
│  ├─ runtime/
│  │  ├─ Machine.java                  # Placed machine identity/lifecycle
│  │  ├─ Inventory.java                # Counts and capacity
│  │  ├─ TransportState.java           # Conveyor occupancy/progress
│  │  └─ ItemLoad.java                 # An individually transported item
│  │
│  ├─ behavior/
│  │  ├─ ConveyorMachine.java
│  │  ├─ StorageMachine.java
│  │  └─ ItemSourceMachine.java
│  │
│  ├─ transport/
│  │  ├─ ItemInput.java                # Acceptance/commit contract
│  │  ├─ ItemOutput.java               # Available-output contract
│  │  └─ TransferBatch.java            # Reusable transfer requests
│  │
│  ├─ simulation/
│  │  ├─ FactorySimulation.java        # Fixed-tick phases
│  │  ├─ MachineScheduler.java         # Active/sleeping/timed machines
│  │  └─ TransferResolver.java         # Conflicts, reservations, commits
│  │
│  ├─ spatial/
│  │  ├─ MachineRegistry.java          # Runtime ownership and lookup
│  │  └─ PortConnections.java          # Cached machine connections
│  │
│  └─ presentation/
│     ├─ ItemVisualState.java
│     ├─ ItemVisualSystem.java         # Optional frame animation
│     └─ ItemRenderer.java             # Drawing only
│
└─ system/systems/
   └─ FactorySystem.java               # Adapter to your game loop
```

Important boundaries:

- Simulation must work without `SpriteBatch`, textures, or `Artist`.
- Presentation reads simulation results; it cannot change inventory or acceptance.
- `FactorySystem` coordinates these components; it does not implement conveyor, chest, or furnace behavior.
- Use one authoritative machine registry. Your existing `MachineRegistry` should eventually replace the separate registry inside `FactorySystem`, rather than maintaining two owners.

Keeping runtime classes nested inside definitions initially is fine. Establishing ownership matters more than moving files.

## 4. Fixed-tick simulation: one shared transfer pipeline

Keep movement and production at your existing 60 UPS.

Each tick should have explicit phases:

```text
Apply placement/configuration commands
                 ↓
Advance active machines locally
                 ↓
Collect output offers
                 ↓
Resolve conflicts and reserve capacity
                 ↓
Commit accepted transfers
                 ↓
Publish changes and schedule wake/sleep
```

### Local machine work

Each machine advances only its own state:

- Conveyor advances progress.
- Producer advances production.
- Crafter advances its recipe.
- Storage usually has no periodic work.

A conveyor reaching its output **offers a transfer**. It does not immediately mutate its neighbor.

### Shared transfer resolution

The resolver handles:

- Multiple outputs competing for one input.
- Capacity reservations.
- Input-side restrictions and filters.
- Deterministic priority or round-robin fairness.
- Transfers into slots being vacated during the same tick.

The last point matters for packed conveyor chains and loops. Collecting requests alone is insufficient if every occupied destination is rejected.

For mutually dependent accepted moves, capture the outgoing loads, remove them from their sources, then insert them into their destinations. Do not sequentially overwrite occupied slots.

Start with explicit semantics:

- A transported item crosses at most one machine boundary per tick.
- Newly received items begin moving on the next tick.
- Rejected transfers retain their source ownership and output position.
- Acceptance queries are pure; only the commit phase mutates inventories.

That one-boundary rule also places a speed limit on one-tile transport. If future belts need to exceed it, deliberately add substeps or segment-based transport.

### Generic contracts

Conceptually, the contracts are:

```java
collectOutputs(...);   // Report available output.
canAccept(...);        // Query rules; do not mutate.
commitExtract(...);    // Remove accepted output.
commitInsert(...);     // Insert accepted input.
```

These are architectural signatures, not drop-in code.

A creative source, furnace, chest, or conveyor participates through those contracts. The resolver should not need `instanceof CreativeSource` or a machine-type switch.

## 5. Item presentation: independent and optional

The central rule should be:

> Turning visuals off must not change the simulation result at any tick.

### Storage intake

For your current `StorageBlock`, the intended sequence is:

1. The resolver accepts an incoming item.
2. The source loses it and storage count increases during the same commit.
3. Presentation optionally creates an intake animation.
4. Finishing that animation removes only the visual record.

If the chest receives five items before the first animation ends, those are five accepted items—not one occupied animation slot blocking four transfers.

If you want a real input-rate limit, implement a **fixed-tick cooldown or input quota**. Keep it separate from animation speed.

### Conveyor motion

Simulation owns logical path progress. Presentation follows the committed path.

I would replace the ambiguous smoothing boolean with clearly defined modes:

- `TICK_POSITION`: draw the latest logical position; no per-frame movement.
- `CONSTANT_SPEED`: move the display along the committed path at a defined speed.
- `SMOOTH_FOLLOW`: ease the display toward the committed position.

Your current “smoothing disabled” behavior is actually `CONSTANT_SPEED`, not “visual updates disabled.”

For exponential following:

```java
float alpha = 1f - (float) Math.exp(-followRate * gameDelta);
```

Use that alpha—not `20f` directly—as the interpolation amount. Your storage smoothing branch currently passes the raw constant.

For turns, follow distance along a path rather than independently easing X and Y; otherwise items can cut diagonally across corners. Preserve visual continuity across transfers, with bounded path history and a catch-up policy.

Other rules:

- Pause-aware game time drives animation.
- Drawing never advances state.
- Newly visible items initialize from current simulation state.
- Offscreen intake effects may be skipped.
- Finishing or dropping a cosmetic effect cannot delete a logical item.
- A stationary blocked item remains drawable even when no animation update is needed.

Also distinguish units: production interval in seconds, transport speed in tiles/second, and smoothing rate are different settings. Your source currently returns its production interval from `getOutputSpeed()`.

## 6. Scheduling and optimization

The main scaling improvement is separating these populations:

### Simulation-active machines

Moving conveyors and processing machines need ticks.

A conveyor blocked at its output can sleep. An empty conveyor can sleep. A passive chest normally needs no tick.

Wake them when relevant state changes:

- Input arrives.
- Destination capacity becomes available.
- A neighbor is placed, removed, or rotated.
- Configuration, power, or recipe availability changes.
- A scheduled timer expires.

Queue scheduler membership changes at defined phase boundaries. Avoid modifying the collection being iterated.

### Visually updating items

Only visible items with unfinished display movement need per-frame animation work.

A conveyor can sleep logically while its display finishes catching up.

### Render-visible items

Use camera/chunk bounds to select drawable items, independently of simulation activity.

A blocked belt can be logically asleep and visually stationary while still being drawn.

### Further optimizations, in order

1. **Avoid unnecessary work:** sleep idle/blocked machines and cull presentation.
2. **Cache connections:** rebuild affected port connections on topology changes instead of repeated neighbor discovery.
3. **Reuse buffers:** transfer requests, reservations, and change notifications should not create garbage for every item every tick.
4. **Allocate capabilities selectively:** do not give every machine an inventory array covering every possible item.
5. **Profile specialized transport:** later, long conveyor runs can become segments while keeping the same endpoint contracts.

Camera visibility must never determine whether an offscreen factory operates. Render-chunk residency and simulation residency are separate policies.

Start with a single simulation thread. A clean phase boundary makes later parallel work possible; threads are not the first optimization.

## 7. Game-loop integration and future content

Your current [GameLoop.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/app/GameLoop.java:43>) runs frame updates before fixed ticks. Introduce a dedicated presentation phase after simulation:

```text
Input/UI → fixed simulation ticks → item presentation → drawing
```

Do not blindly move all existing frame updates; input, camera, and UI can have different timing requirements.

Also define an overload policy. The current five-tick-per-frame cap retains excess backlog, and game speed is effectively unbounded. Add a supported speed range and report simulation lag rather than allowing an indefinitely growing backlog.

For future content:

- Port definitions include footprint location, direction, and acceptance policy.
- Whitelists, recipes, and filters belong behind the input contract.
- New recipes usually become content data.
- New behavior families get runtime implementations.
- Save authoritative machine state and stable content IDs—not visual positions or enum ordinals.
- Reconstruct presentation after loading.

This supports future systems without making `Machine` a class containing every possible feature.

## 8. Incremental implementation plan

Each stage should leave the game runnable.

1. **Separate storage logic from intake visuals.**  
   Prove smoothing cannot affect accepted item counts or timing.

2. **Introduce the shared transfer pipeline.**  
   Test straight chains, competing inputs, full storage, and packed loops.

3. **Separate simulation scheduling, visual updates, and rendering.**  
   Prove sleeping conveyor items remain visible and wake correctly.

4. **Consolidate registry and connection ownership.**  
   Cover placement, removal, rotation, and chunk boundaries.

5. **Move content configuration out of runtime state.**  
   Add another conveyor tier and a basic processing machine through the same contracts.

6. **Benchmark before specializing.**  
   Measure mostly idle factories, fully moving belts, jams, and heavily loaded storage separately.

Track tick time, transfer-resolution time, active counts, visible-item counts, allocation rate, and frame-time spikes. A placed-machine count alone does not describe performance.

Your strongest regression test is: **run the same tick-stamped commands at different FPS, with different visual modes and camera positions, and compare authoritative state after the same number of ticks. It should match.**

**The next concrete step is stage 1: make `StorageMachine` own inventory only, and move intake animation ownership into separate visual state.** That establishes the boundary everything else depends on.

I recommend keeping your **`Block` definition → runtime `Machine`** foundation, but separating three responsibilities:

**Machines decide what they want to do. A shared simulation resolves transfers. Presentation decides how items look.**

That gives you room for more content without filling `FactorySystem` with machine-specific branches. I checked the current files; nothing was edited.

## 1. What needs changing first

Your current code has several boundaries mixed together:

- [StorageBlock.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/machines/cores/StorageBlock.java:124>) rejects input while `this.item` exists, but its visual update clears that field. **Animation therefore controls storage throughput.**
- [Conveyor.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/machines/cores/Conveyor.java:75>) transfers directly while advancing its item. Results can depend on which conveyor updates first.
- [MachineGroup.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/MachineGroup.java:28>) uses the same active collection for simulation, visual updates, and drawing. Those need different membership rules.
- [Machine.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/machine/Machine.java:124>) contains visual methods that also modify logical progress. That must not be part of the base contract.

These are correctness issues to resolve before optimizing larger factories.

## 2. Separate the data by ownership

### Shared content definitions

Your `Block` should describe a **type of machine**, shared by all instances:

- Stable content ID.
- Footprint and ports.
- Capacity, conveyor speed, production interval.
- Runtime factory.
- Rendering configuration identifiers.

Position, rotation, inventory, and current production progress belong to the placed `Machine`, not the shared definition.

Normal and fast conveyors can share the same runtime implementation with different configuration. You do not need another behavior class for every content tier.

### Runtime machine state

Keep `Machine` relatively small:

- Machine ID and definition reference.
- Tile position and direction.
- Lifecycle and scheduling information.
- Only the state/modules that machine actually needs.

Use composition for reusable capabilities:

- `Inventory`: counted items.
- `TransportState`: items moving along a conveyor.
- `RecipeState`: processing progress, later.
- `PowerState`: energy consumption, later.

**A common item-transfer API does not require every machine to use the same storage representation.**

A chest needs item counts. A conveyor needs ordered moving loads. A producer needs an output buffer. All can expose compatible inputs and outputs.

### Separate stored items, transported items, and visuals

Use three distinct concepts:

| Concept | Contains | Authoritative? |
|---|---|---|
| Inventory | Item type → quantity | Yes |
| Transported load | Item type, owner/path, progress | Yes |
| Item visual | Display position, animation/path-following state | No |

Do not create one moving `Item` object for every item inside a chest.

A visual record is also **not a ground item**. It has no inventory ownership, collision, or pickup behavior.

## 3. Suggested project structure

This is a target structure to migrate toward gradually—not a request to create every class immediately.

```text
core/
├─ blocks/
│  └─ Blocks.java                     # Content registration
│
├─ machine/
│  ├─ definition/
│  │  └─ Block.java                    # Shared machine configuration
│  │
│  ├─ runtime/
│  │  ├─ Machine.java                  # Placed machine identity/lifecycle
│  │  ├─ Inventory.java                # Counts and capacity
│  │  ├─ TransportState.java           # Conveyor occupancy/progress
│  │  └─ ItemLoad.java                 # An individually transported item
│  │
│  ├─ behavior/
│  │  ├─ ConveyorMachine.java
│  │  ├─ StorageMachine.java
│  │  └─ ItemSourceMachine.java
│  │
│  ├─ transport/
│  │  ├─ ItemInput.java                # Acceptance/commit contract
│  │  ├─ ItemOutput.java               # Available-output contract
│  │  └─ TransferBatch.java            # Reusable transfer requests
│  │
│  ├─ simulation/
│  │  ├─ FactorySimulation.java        # Fixed-tick phases
│  │  ├─ MachineScheduler.java         # Active/sleeping/timed machines
│  │  └─ TransferResolver.java         # Conflicts, reservations, commits
│  │
│  ├─ spatial/
│  │  ├─ MachineRegistry.java          # Runtime ownership and lookup
│  │  └─ PortConnections.java          # Cached machine connections
│  │
│  └─ presentation/
│     ├─ ItemVisualState.java
│     ├─ ItemVisualSystem.java         # Optional frame animation
│     └─ ItemRenderer.java             # Drawing only
│
└─ system/systems/
   └─ FactorySystem.java               # Adapter to your game loop
```

Important boundaries:

- Simulation must work without `SpriteBatch`, textures, or `Artist`.
- Presentation reads simulation results; it cannot change inventory or acceptance.
- `FactorySystem` coordinates these components; it does not implement conveyor, chest, or furnace behavior.
- Use one authoritative machine registry. Your existing `MachineRegistry` should eventually replace the separate registry inside `FactorySystem`, rather than maintaining two owners.

Keeping runtime classes nested inside definitions initially is fine. Establishing ownership matters more than moving files.

## 4. Fixed-tick simulation: one shared transfer pipeline

Keep movement and production at your existing 60 UPS.

Each tick should have explicit phases:

```text
Apply placement/configuration commands
                 ↓
Advance active machines locally
                 ↓
Collect output offers
                 ↓
Resolve conflicts and reserve capacity
                 ↓
Commit accepted transfers
                 ↓
Publish changes and schedule wake/sleep
```

### Local machine work

Each machine advances only its own state:

- Conveyor advances progress.
- Producer advances production.
- Crafter advances its recipe.
- Storage usually has no periodic work.

A conveyor reaching its output **offers a transfer**. It does not immediately mutate its neighbor.

### Shared transfer resolution

The resolver handles:

- Multiple outputs competing for one input.
- Capacity reservations.
- Input-side restrictions and filters.
- Deterministic priority or round-robin fairness.
- Transfers into slots being vacated during the same tick.

The last point matters for packed conveyor chains and loops. Collecting requests alone is insufficient if every occupied destination is rejected.

For mutually dependent accepted moves, capture the outgoing loads, remove them from their sources, then insert them into their destinations. Do not sequentially overwrite occupied slots.

Start with explicit semantics:

- A transported item crosses at most one machine boundary per tick.
- Newly received items begin moving on the next tick.
- Rejected transfers retain their source ownership and output position.
- Acceptance queries are pure; only the commit phase mutates inventories.

That one-boundary rule also places a speed limit on one-tile transport. If future belts need to exceed it, deliberately add substeps or segment-based transport.

### Generic contracts

Conceptually, the contracts are:

```java
collectOutputs(...);   // Report available output.
canAccept(...);        // Query rules; do not mutate.
commitExtract(...);    // Remove accepted output.
commitInsert(...);     // Insert accepted input.
```

These are architectural signatures, not drop-in code.

A creative source, furnace, chest, or conveyor participates through those contracts. The resolver should not need `instanceof CreativeSource` or a machine-type switch.

## 5. Item presentation: independent and optional

The central rule should be:

> Turning visuals off must not change the simulation result at any tick.

### Storage intake

For your current `StorageBlock`, the intended sequence is:

1. The resolver accepts an incoming item.
2. The source loses it and storage count increases during the same commit.
3. Presentation optionally creates an intake animation.
4. Finishing that animation removes only the visual record.

If the chest receives five items before the first animation ends, those are five accepted items—not one occupied animation slot blocking four transfers.

If you want a real input-rate limit, implement a **fixed-tick cooldown or input quota**. Keep it separate from animation speed.

### Conveyor motion

Simulation owns logical path progress. Presentation follows the committed path.

I would replace the ambiguous smoothing boolean with clearly defined modes:

- `TICK_POSITION`: draw the latest logical position; no per-frame movement.
- `CONSTANT_SPEED`: move the display along the committed path at a defined speed.
- `SMOOTH_FOLLOW`: ease the display toward the committed position.

Your current “smoothing disabled” behavior is actually `CONSTANT_SPEED`, not “visual updates disabled.”

For exponential following:

```java
float alpha = 1f - (float) Math.exp(-followRate * gameDelta);
```

Use that alpha—not `20f` directly—as the interpolation amount. Your storage smoothing branch currently passes the raw constant.

For turns, follow distance along a path rather than independently easing X and Y; otherwise items can cut diagonally across corners. Preserve visual continuity across transfers, with bounded path history and a catch-up policy.

Other rules:

- Pause-aware game time drives animation.
- Drawing never advances state.
- Newly visible items initialize from current simulation state.
- Offscreen intake effects may be skipped.
- Finishing or dropping a cosmetic effect cannot delete a logical item.
- A stationary blocked item remains drawable even when no animation update is needed.

Also distinguish units: production interval in seconds, transport speed in tiles/second, and smoothing rate are different settings. Your source currently returns its production interval from `getOutputSpeed()`.

## 6. Scheduling and optimization

The main scaling improvement is separating these populations:

### Simulation-active machines

Moving conveyors and processing machines need ticks.

A conveyor blocked at its output can sleep. An empty conveyor can sleep. A passive chest normally needs no tick.

Wake them when relevant state changes:

- Input arrives.
- Destination capacity becomes available.
- A neighbor is placed, removed, or rotated.
- Configuration, power, or recipe availability changes.
- A scheduled timer expires.

Queue scheduler membership changes at defined phase boundaries. Avoid modifying the collection being iterated.

### Visually updating items

Only visible items with unfinished display movement need per-frame animation work.

A conveyor can sleep logically while its display finishes catching up.

### Render-visible items

Use camera/chunk bounds to select drawable items, independently of simulation activity.

A blocked belt can be logically asleep and visually stationary while still being drawn.

### Further optimizations, in order

1. **Avoid unnecessary work:** sleep idle/blocked machines and cull presentation.
2. **Cache connections:** rebuild affected port connections on topology changes instead of repeated neighbor discovery.
3. **Reuse buffers:** transfer requests, reservations, and change notifications should not create garbage for every item every tick.
4. **Allocate capabilities selectively:** do not give every machine an inventory array covering every possible item.
5. **Profile specialized transport:** later, long conveyor runs can become segments while keeping the same endpoint contracts.

Camera visibility must never determine whether an offscreen factory operates. Render-chunk residency and simulation residency are separate policies.

Start with a single simulation thread. A clean phase boundary makes later parallel work possible; threads are not the first optimization.

## 7. Game-loop integration and future content

Your current [GameLoop.java](<C:/Users/khang/IdeaProjects/2d factory game exterLibGDX/core/src/main/core/app/GameLoop.java:43>) runs frame updates before fixed ticks. Introduce a dedicated presentation phase after simulation:

```text
Input/UI → fixed simulation ticks → item presentation → drawing
```

Do not blindly move all existing frame updates; input, camera, and UI can have different timing requirements.

Also define an overload policy. The current five-tick-per-frame cap retains excess backlog, and game speed is effectively unbounded. Add a supported speed range and report simulation lag rather than allowing an indefinitely growing backlog.

For future content:

- Port definitions include footprint location, direction, and acceptance policy.
- Whitelists, recipes, and filters belong behind the input contract.
- New recipes usually become content data.
- New behavior families get runtime implementations.
- Save authoritative machine state and stable content IDs—not visual positions or enum ordinals.
- Reconstruct presentation after loading.

This supports future systems without making `Machine` a class containing every possible feature.

## 8. Incremental implementation plan

Each stage should leave the game runnable.

1. **Separate storage logic from intake visuals.**  
   Prove smoothing cannot affect accepted item counts or timing.

2. **Introduce the shared transfer pipeline.**  
   Test straight chains, competing inputs, full storage, and packed loops.

3. **Separate simulation scheduling, visual updates, and rendering.**  
   Prove sleeping conveyor items remain visible and wake correctly.

4. **Consolidate registry and connection ownership.**  
   Cover placement, removal, rotation, and chunk boundaries.

5. **Move content configuration out of runtime state.**  
   Add another conveyor tier and a basic processing machine through the same contracts.

6. **Benchmark before specializing.**  
   Measure mostly idle factories, fully moving belts, jams, and heavily loaded storage separately.

Track tick time, transfer-resolution time, active counts, visible-item counts, allocation rate, and frame-time spikes. A placed-machine count alone does not describe performance.

Your strongest regression test is: **run the same tick-stamped commands at different FPS, with different visual modes and camera positions, and compare authoritative state after the same number of ticks. It should match.**

**The next concrete step is stage 1: make `StorageMachine` own inventory only, and move intake animation ownership into separate visual state.** That establishes the boundary everything else depends on.
