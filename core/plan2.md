# Factory transport redesign: implementation plan

Status: design and implementation checklist; no implementation milestones completed.
Created: 2026-09-25.

## 1. Goal and working assumptions

Build an approachable factory simulation with a small machine API, understandable conveyor logic, and clear extension points for mods. Design for worlds containing 1 million to 10 million conveyor tiles and machines, and establish supported limits with measurements.

The main design is:

- Machines consume from input buffers and produce into output buffers.
- The transport engine moves available outputs through connected ports.
- Compatible conveyor tiles form a `BeltLine`, which owns an ordered queue of items.
- Machines run when awakened or when scheduled work becomes due.
- Rendering reads visible simulation state without deciding logical transfers.

Provisional performance target: 60 simulation ticks per second. Target CPU, RAM, acceptable memory usage, and the mixture of conveyors versus continuously running custom machines are not yet specified. Confirm these when establishing the benchmark budget; they do not block the small functional prototype.

Distinguish placed tiles, moving items, active conveyor lines, independent processing machines, and visible items. Ten million placed conveyor tiles and ten million arbitrary mod callbacks every tick are different workloads. No 1-million or 10-million performance result has been demonstrated for this proposal.

## 2. Instructions for agents using this plan

- Read current user instructions and `.aiassistant/rules/THIS_PROJECT_RULE.md` before modifying source files.
- The authorization that created this document covers `core/plan2.md`. This plan alone does not authorize application source edits. Honor any subsequent source-edit authorization from the user.
- Recheck the relevant source before implementing a task; the baseline below may change.
- Keep work within one milestone or an explicitly assigned task. Do not combine this migration with unrelated cleanup.
- Use existing definitions and registries where practical. Proposed component names describe responsibilities; they do not require renaming every existing class.
- Assign one writer to each shared file. Coordinate changes to contracts before consumers implement them.
- Preserve the old implementation as a temporary behavioral reference until the new path is verified.
- Mark a task complete only with the evidence required by its acceptance gate. Report blocked dependencies and unverified assumptions explicitly.
- End each task with the handoff template in section 12.

## 3. Current code and reference implementations

All project paths in this document are relative to the repository root.

| Current file | Relevant responsibility |
| --- | --- |
| `core/src/main/core/machine/MachineGroup.java` | Active-machine collection, frame updates, ticks, output collection, resolver calls, drawing, wake/deactivation. |
| `core/src/main/core/machine/transport/TransferBatch.java` | Pooled offers, presentation snapshots, selected state and cycle traversal state. |
| `core/src/main/core/machine/transport/TransferResolver.java` | Sorted immediate selection, vacancy propagation, cycle selection, extraction, acceptance and observer notification. |
| `core/src/main/core/machine/transport/InputAdmission.java` | Reservation eligibility contract. |
| `core/src/main/core/machine/transport/OutputEmitter.java` | Output collection contract. |
| `core/src/main/core/machine/Machine.java` | Runtime inventory, item ownership, acceptance, extraction and group attachment. |
| `core/src/main/core/machine/machines/cores/Conveyor.java` | One-item conveyor behavior and logical/visual progress. |
| `core/src/main/core/machine/machines/cores/ItemSource.java` | Source production and output. |
| `core/src/main/core/machine/machines/cores/StorageBlock.java` | Finite and unlimited storage acceptance. |
| `core/src/main/core/machine/machines/definition/Block.java` | Existing definition/runtime construction boundary. |
| `core/src/main/core/machine/state/ItemType.java` | Current item enum; relevant to future dynamic content registration. |
| `core/src/main/core/system/systems/FactorySystem.java` | Placement integration, machine lookup, ticking and presentation events. |
| `core/src/main/core/machine/render/RenderManager.java` | Transfer presentation. |
| `core/src/main/core/app/GameTest.java` | Existing manual test scene. |
| `core/src/main/core/app/GameLoop.java` | Simulation timing integration. |

There are similarly named block/render classes in the repository. Trace actual imports and callers before selecting an implementation target.

### Lessons verified from Mindustry

Local reference checkout: `C:\Users\khang\IdeaProjects\mindustry`, inspected HEAD `27ba9d7cd2`. These findings describe that checkout, not a performance guarantee or a claim about every Mindustry version.

- `core/src/mindustry/world/blocks/distribution/Conveyor.java`: `pass()` asks `acceptItem(source, item)` and then calls `handleItem(source, item)`. `onProximityUpdate()` caches the next building. Empty conveyors can call `sleep()`.
- `core/src/mindustry/entities/comp/BuildingComp.java`: `sleep()` and `noSleep()` remove and restore buildings to update groups; inventory admission and mutation are distinct operations.
- `core/src/mindustry/entities/EntityGroup.java`: `update()` still visits individual awake entities.
- `core/src/mindustry/world/blocks/distribution/Duct.java`: a useful smaller reference for one-item transport, but its immediate acceptance rules are not equivalent to this project's simultaneous full-loop transfers.

Borrow clear machine responsibilities, cached connections and sleep/wake behavior. Do not assume direct local handoffs preserve the existing transfer timing or merge winners.

Additional primary references:

- [Factorio's transport-line and item-gap optimization explanation](https://factorio.com/blog/post/fff-176): combine adjacent belts, store item gaps, and update movement/compression state with less work per item. Its historical measurements are not expected speedups for this project.
- [Mindustry modding introduction](https://mindustrygame.github.io/wiki/modding/1-modding/): data-defined content.
- [Mindustry scripting](https://mindustrygame.github.io/wiki/modding/3-scripting/): custom behavior and events.

## 4. Required behavior and ownership rules

These are compatibility requirements unless a later user instruction explicitly changes them.

- [ ] Simulation uses the fixed simulation clock. Frame rate, camera position and visual smoothing do not affect throughput.
- [ ] An item has exactly one logical owner: an inventory, an output buffer, or a conveyor line. Planning a move does not duplicate it.
- [ ] Full or invalid destinations leave the source item intact.
- [ ] Finite storage accepts only available capacity; unlimited storage preserves the existing unlimited-capacity behavior.
- [ ] Admission checks are pure. Logical mutation happens through engine-owned buffer operations and committed transfers.
- [ ] An item received during transfer commit cannot move through another output or begin machine processing again during that same tick.
- [ ] Ready packed chains can advance together when downstream space is available.
- [ ] Fully occupied compatible conveyor loops can rotate without requiring an artificial empty slot.
- [ ] Merges remain deterministic. Initially preserve the current source-coordinate priority; fair rotation is a separate gameplay change.
- [ ] Preserve input-direction restrictions, item filters, configured speeds and backpressure.
- [ ] Preserve smooth presentation across handoffs, including storage intake animation, without retaining an animated object for every offscreen item.
- [ ] Placement, removal, rotation and speed changes cannot leave stale connections, duplicate wakeups or lost items.

### Timing decisions to freeze before aggregation

The existing conveyors clamp local progress and reset it at tile handoff. A continuously moving line may have different arrival ticks, particularly at speeds that do not divide the tick rate evenly.

- [ ] Record baseline arrival ticks for equal-speed lines, non-divisible speeds, turns, speed transitions, side inputs, blocking and restart.
- [ ] Specify progress precision, boundary equality, handoff delay, overflow handling and time-efficiency behavior.
- [ ] Default to preserving documented behavior. Use an uncompressed implementation for unsupported cases until an equivalent optimization exists, or obtain an explicit gameplay decision to change timing.
- [ ] Do not hide known correctness defects in the baseline: document them and establish a separate expected-behavior test before fixing them.

## 5. Proposed architecture

```text
Machine output -> Conveyor line -> Machine input -> Recipe -> Machine output
                         |
                  ordered item queue
                  shared movement state
```

| Component | Owns | Public use |
| --- | --- | --- |
| Shared block/item definitions | Configuration, recipes, port layouts, filters, visuals and behavior factories | Register content once. |
| Machine runtime/context | Per-placement inventory and processing state | Consume inputs, produce outputs, schedule work. |
| MachineGroup / scheduler | Machine handles, wake queues, scheduled completion times | Run due work and handle registration/removal. |
| BeltLine | Route, ordered item IDs, movement/spacing state and line ends | Query state; engine-controlled insertion, removal and advancement. |
| TransportSystem | Cached connections, line construction, ready endpoints and boundary transfers | Apply topology changes and run transport for a tick. |
| Factory renderer | Visible presentation data and transient effects | Read state and draw visible content. |

Most conveyor tiles should eventually be compact world metadata identifying a line and a position on its route. Do not allocate a full machine, inventory, port collection and animation object for every ordinary belt tile.

Input and output ports exist at meaningful machine connections and line boundaries. A port describes a side, supported item rules and a buffer. Storage accounting and reservation bookkeeping belong to the engine. Avoid exposing `reservedIncoming`, cycle states or mutable neighbor inventory to mod code.

### Example machine API

This is an illustrative contract, not existing compilable project code. Freeze exact names in milestone P1.

```java
@Override
public void onWake(MachineContext machine) {
    if (!machine.output().tryAdd(selectedItem, 1)) {
        machine.waitForOutputSpace();
        return;
    }

    machine.wakeAfter(intervalTicks);
}
```

`tryAdd()` produces into this machine's output buffer; success does not mean delivery to a neighbor. A successful output mutation informs transport that the endpoint has work. When capacity later becomes available, the scheduler wakes a waiting producer once.

Standard crafting should be supplied by a reusable recipe behavior. Consume inputs and reserve result capacity when starting a job, schedule its completion, and account for changed power/speed before rescheduling. A custom callback that genuinely needs every tick can opt into continuous updates.

### Fixed tick phases

1. Apply queued placement/removal/configuration changes and repair affected connections.
2. Run due machine work in a defined deterministic order.
3. Advance relevant conveyor-line state to this tick.
4. Determine legal transfers between ready endpoints, including downstream-vacancy dependencies.
5. Commit transfers, notify observers and schedule machine reactions for subsequent ticks.

Private dependency processing may propagate available space within phase 4. Public callbacks must not reenter transfer resolution or forward freshly received items within the same tick.

For dependent transfers, validate the whole selected group, extract selected outputs, then apply selected inputs. Callback code runs after the logical commit. Specify callback exception handling so it cannot leave half a transfer committed.

### Conveyor-line rules

- Begin with a straight line of compatible equal-speed belts.
- Combine only belts whose movement, entry and filtering rules permit equivalent grouped behavior.
- Split at junctions, external side inputs/outputs, behavior changes and incompatible speeds. Curves can be included once route mapping is verified.
- Store an ordered item queue in reusable primitive storage. Keep individual item types even when their motion is grouped.
- Advance shared motion and spacing state when possible. Treat insertion/removal, compression boundaries and item exits as meaningful work.
- Continue updating a partially compressing blocked line. Sleep only when it is settled and has no scheduled motion to perform.
- Keep a moving full loop active, or compute its position from a valid scheduled/shared-motion representation.
- Support pure closed loops as circular lines where compatible. Junction-containing cycles still need simultaneous dependency resolution between lines.
- Cache source priority and connection relationships when topology changes. Avoid a global sort and rebuilt machine hash index every tick.
- Do not assume dependency graphs are always small: junction-heavy layouts are required stress cases.
- Bound expensive route/storage operations with pages or a measured line-length limit. Split/merge work can touch an entire affected run or component; do not promise constant-time edits to arbitrarily large networks.

### Scheduler rules

| Condition | Wake condition |
| --- | --- |
| Waiting for input | Relevant input changed. |
| Waiting for output capacity | Output capacity became available. |
| Processing a recipe | Completion time or a relevant power/speed/configuration change. |
| Empty line | Insertion or topology change. |
| Settled blocked line | Downstream capacity or topology change. |
| Moving line/loop | Its next relevant movement event, or an active-line update. |

- Deduplicate wakeups; removal cancels or invalidates scheduled work.
- Use stable handles with generation/version checks where IDs can be reused.
- Prefer reusable arrays and tick buckets for scheduled work once measurements justify them.
- Avoid checking every sleeping machine each tick merely to confirm that it is sleeping.
- Synchronized recipe completions can still produce large work spikes; include them in benchmarks.

## 6. Modding and save-data boundaries

- Data mods change speeds, recipes, capacities, port directions, filters and textures using built-in behavior.
- Event-based behavior mods react to input changes, recipe completion, output space and configuration changes through `MachineContext`.
- Fully custom movement or continuously running code is allowed through an explicit extension path. It can prevent line aggregation and has a measurable runtime cost.
- A standard belt variant that changes only compatible configuration can reuse the optimized line engine.
- Use stable content names such as `my_mod:copper_plate` in saved content mappings, with dense numeric IDs at runtime. Do not rely on enum ordinals as a persistent mod format.
- Shared definitions are separate from each placed machine's mutable state.
- Prefer inventories sized to actual recipe/buffer needs; avoid an array covering every registered item type in every machine.
- Unique item metadata may use an optional state handle. Ordinary items should not require a heavyweight object.
- Save authoritative logical item order, progress, inventories, routing policy state and scheduled-work state. Rebuild derived connections/caches after load.
- Define schema versions, missing-content behavior and content-ID remapping before integrating a persistent save format.
- Geometry changes and speed overrides must split or invalidate affected lines safely before new rules take effect.

## 7. Implementation milestones

Task IDs are intended for assignment and handoff. All boxes start unchecked because this document records a plan rather than completed implementation.

### P0 - Establish the behavioral reference

Dependencies: none. Suggested owner: validation/integration agent.

- [ ] P0.1 Trace the actual tick, placement, construction, transfer and render callers. Record any drift from section 3.
- [ ] P0.2 Establish a small deterministic simulation harness that can run without opening the graphical game. Choose minimal test wiring; do not assume an existing test framework.
- [ ] P0.3 Record baseline logical states and transfer events for the compatibility scenarios in section 8.
- [ ] P0.4 Freeze timing, ownership, merge priority and observer-order expectations from section 4.
- [ ] P0.5 Record CPU, RAM, OS, JDK, tick target and initial runtime measurements.

Acceptance gate: repeatable scenarios with explicit expected ownership, counts and arrival ticks. Known bugs and unresolved decisions are listed separately.

### P1 - Define buffers, ports and the machine-facing API

Dependencies: P0.3-P0.4. Suggested owner: machine/API agent.

- [ ] P1.1 Specify input/output buffer operations, capacity behavior, item-type checks and readiness notifications.
- [ ] P1.2 Specify `MachineContext`, deduplicated wake requests, scheduling, and callbacks after commit.
- [ ] P1.3 Adapt source and storage behavior to the contracts behind a compatibility path using the existing transfer implementation.
- [ ] P1.4 Specify definition factories so new machine behavior does not require concrete-type branches in `FactorySystem`.
- [ ] P1.5 Document the smallest source, chest and recipe examples using the API.

Acceptance gate: the API can express the current source/conveyor/storage loop without exposing resolver internals or changing logical results.

### P2 - Build the first understandable BeltLine prototype

Dependencies: P1, P0 reference scenarios. Suggested owner: conveyor agent.

- [ ] P2.1 Implement a simple, inspectable ordered queue for one straight compatible line. Optimize after behavior is established.
- [ ] P2.2 Implement insertion, logical advancement, endpoint readiness, blocking, compression and restart.
- [ ] P2.3 Connect source -> 100-tile line -> chest through the agreed port contracts.
- [ ] P2.4 Expose debug state: line ID, route, item order, head/tail progress, next wake time and reason for blocking.
- [ ] P2.5 Compare after each tick against the reference; also test short lines where each handoff can be inspected.

Acceptance gate: order, counts, arrival ticks and blocking/restart are correct. Increasing line length does not require adding an update callback for each tile.

### P3 - Add topology, boundary arbitration and loops

Dependencies: P2. Suggested owner: conveyor/topology agent; integration owner reviews arbitration.

- [ ] P3.1 Build and cache line routes and neighboring endpoints when layout changes.
- [ ] P3.2 Handle placement, rotation, removal, insertion into a route, splitting, merging and speed changes while preserving items.
- [ ] P3.3 Extend route mapping to turns and explicitly supported side inputs.
- [ ] P3.4 Resolve competing inputs using the baseline priority, with one selected destination per offered output item.
- [ ] P3.5 Preserve simultaneous packed-chain transfers and implement compatible circular lines.
- [ ] P3.6 Handle cycles spanning line boundaries/junctions without partial commits or infinite traversal.
- [ ] P3.7 Specify removal behavior for held items and ensure stale handles/events cannot access reused objects.

Acceptance gate: all topology and dependency cases pass; traversal/registration order changes do not alter the documented deterministic result.

### P4 - Reduce runtime work and allocations

Dependencies: P3 correctness gates. Suggested owners: conveyor and scheduler agents in separately owned files.

- [ ] P4.1 Replace repeated per-item movement with shared movement/gap state for compatible lines, preserving the P0 timing specification.
- [ ] P4.2 Implement and test compression tracking, including invalidation after insertion, removal and topology changes. Restrict constant/amortized-cost claims to the cases actually supported.
- [ ] P4.3 Move common tile/item data to compact reusable arrays; avoid a full `Machine` object per ordinary belt tile.
- [ ] P4.4 Schedule machine completions and state-change wakeups; replace full-list inactivity scans with explicit transitions where valid.
- [ ] P4.5 Cache and process ready endpoints; eliminate per-tick global offer sorting/reindexing on the new path.
- [ ] P4.6 Profile steady-state allocations and optimize only identified remaining costs.

Acceptance gate: correctness remains identical for supported compatibility cases; benchmarks demonstrate which costs were reduced and where fallbacks remain.

### P5 - Integrate visible rendering and inspection

Dependencies: stable read-only line/query API from P2; final validation after P3-P4. Suggested owner: rendering agent.

- [ ] P5.1 Draw only visible chunks and visible portions of lines; cache geometry as appropriate.
- [ ] P5.2 Compute smooth positions from authoritative line progress without a per-frame update for every offscreen item.
- [ ] P5.3 Preserve turn presentation and storage intake effects through committed-transfer events.
- [ ] P5.4 Add distant-zoom representations and a selected-line debug view.
- [ ] P5.5 Verify identical simulation results with different frame rates, camera locations, and rendering disabled.

Acceptance gate: smooth visible movement, bounded presentation work for offscreen content, and no visual influence on acceptance or throughput.

### P6 - Add content registration and persistence support

Dependencies: P1 stable contracts and P3 authoritative state. Suggested owner: content/modding agent.

- [ ] P6.1 Introduce stable named item/block registration and migrate enum dependencies through a documented compatibility layer.
- [ ] P6.2 Load a standard belt variant, recipe machine and storage definition without adding `FactorySystem` type branches.
- [ ] P6.3 Add an event-based custom machine example and define the boundary for continuous custom behavior.
- [ ] P6.4 Implement versioned logical-state serialization, content-ID remapping and derived-cache reconstruction when save support is integrated.
- [ ] P6.5 Verify waiting, processing, blocked, moving and cyclic state after save/load, including missing-content handling.

Acceptance gate: examples work through public contracts; mod code does not need to edit the transport engine; saved state preserves item ownership and deterministic continuation.

### P7 - Measure scale and complete migration

Dependencies: P4-P5; include P6 workloads when available. Suggested owner: benchmark/integration agent.

- [ ] P7.1 Run the workload matrix in section 9 at 10k, 100k, 1m and, where feasible, 10m tiles/instances.
- [ ] P7.2 Report raw measurements and supported configurations; distinguish tile count, machine count, line count and item count.
- [ ] P7.3 Resolve measured bottlenecks before considering parallel execution.
- [ ] P7.4 If parallel execution is justified, partition ownership and define deterministic cross-boundary transfer processing. Validate boundary cases against the single-threaded reference.
- [ ] P7.5 Switch the live factory integration after correctness and agreed performance gates pass.
- [ ] P7.6 Remove the old transport path only after call-site checks prove that direct transfers, duplicate updates and legacy item ownership cannot remain active.

Acceptance gate: supported workloads meet the agreed tick/memory targets with reproducible evidence; unsupported workloads and fallback behavior are documented.

## 8. Behavioral validation checklist

Use small targeted simulations and deterministic state comparisons. Graphics are optional for logical tests; visual handoff checks use a small real scene.

- [ ] Empty source/belt/storage and empty tick.
- [ ] One item through 1, 3 and 100 tiles at several speeds, including non-divisible tick speeds.
- [ ] Distinct item types maintain order through a line and after a save/load.
- [ ] Source -> packed chain -> available destination advances correctly.
- [ ] Blocked output with remaining gaps compresses correctly; settled blockage sleeps; freeing one slot wakes the required work.
- [ ] One-slot remaining storage with multiple ready sources accepts the correct count and winner.
- [ ] Unlimited storage and zero-capacity/no-input targets.
- [ ] Wrong input direction and rejected item filter.
- [ ] Pure full ring, partially filled ring, ring with an external competing input, and a cycle crossing line boundaries.
- [ ] An invalid edge in a cycle prevents an illegal partial rotation.
- [ ] New receipts cannot traverse another endpoint during the same tick.
- [ ] Changed registration/iteration order preserves defined merge winners and simulation results.
- [ ] Duplicate wake requests, sleeping input arrival, output-space notification, machine removal and handle reuse.
- [ ] Place/remove/rotate a belt or change speed while its line is occupied.
- [ ] Recipe input consumption, output reservation, completion, and mid-recipe power/speed changes.
- [ ] Frame-rate changes and offscreen simulation preserve logical results.
- [ ] Save/load preserves logical state and scheduled work when persistence is available.

Across all scenarios, assert conservation: initial items + production - consumption - explicitly defined destruction = items currently owned by the simulation. Count committed presentation notifications independently; visual effects are not logical items.

## 9. Performance validation

At 60 ticks/second, the complete tick budget is about 16.67 ms. Agree on the simulation share and memory budget for the target hardware before making a capacity claim.

Illustrative work counts, assuming 100 compatible tiles per line and one line visit per tick:

| Tiles | Per-tile visits/second | Line visits/second |
| --- | ---: | ---: |
| 1,000,000 | 60,000,000 | 600,000 |
| 10,000,000 | 600,000,000 | 6,000,000 |

These are arithmetic examples, not timing predictions. Transfers, items, junctions, topology edits, custom logic and rendering add costs.

Required workload matrix:

| Workload | What it measures |
| --- | --- |
| Long moving lines with mixed item IDs | Benefit and overhead of grouped motion. |
| Empty and fully settled blocked factories | Scheduling overhead and absence of full-world polling. |
| Partial jams repeatedly blocking/unblocking | Compression and wake propagation. |
| Many junctions and short lines | Boundary transfer cost and worst-case loss of grouping. |
| Alternating speeds/custom behavior | Compatibility splits and fallback cost. |
| Full loops and connected cycles | Simultaneous dependency handling. |
| Many recipe machines, including synchronized completions | Event rate, latency spikes and inventory cost. |
| Continuously running custom callbacks | Cost of behavior that cannot sleep or aggregate. |
| Placement/removal across a large network | Topology rebuilding and latency. |
| Near and distant camera views | Visible-item cost, geometry cost and zoom handling. |

For every result record hardware, OS/JDK, code revision, seed, warmup and measurement duration, tile/line/junction/machine/item counts, average and p95/p99 tick time, worst tick, throughput, allocations, heap and GC pauses. Publish simulation-only and combined rendering results separately. Make the sink consume or count items so the moving benchmark cannot quietly turn into a permanently blocked world.

The existing compile command is:

```powershell
.\gradlew.bat --gradle-user-home .gradle-local core:compileJava
```

Record the actual harness/test/benchmark commands after P0 establishes them. Do not claim that a proposed Gradle task already exists. Compilation alone does not validate timing, conservation or million-scale performance.

## 10. Work ownership and dependency order

This is a suggested division for later authorized collaboration, not an instruction to start parallel agents automatically.

| Workstream | Primary ownership | Coordination requirement |
| --- | --- | --- |
| Integration/validation | Tick contract, reference harness, baseline cases, final cutover | Own shared API decisions and integration files. |
| Machine/API/scheduler | Buffers, ports, MachineContext, wakeups, recipes | Freeze P1 contracts before conveyor/render consumers depend on them. |
| Conveyor/topology | BeltLine, connection construction, boundary resolution | Preserve reference timing and publish read-only query contracts. |
| Rendering | Visibility, route sampling, interpolation, debug display | Read logical state through agreed APIs. |
| Content/modding | Registries, definitions, extensions, persistence | Coordinate stable IDs and authoritative state with integration owner. |
| Benchmarking | Workload generators, timing/allocation/memory reports | Use functional sinks and publish reproducible configurations. |

Critical path: P0 -> P1 -> P2 -> P3 -> P4 -> P7. P5 can begin after the P2 query contract stabilizes. P6 starts after P1 contracts, with persistence finishing after P3 state is stable. Do not let parallel changes silently redefine item timing, port semantics or serialization.

## 11. First task to assign

Start with P0 and the P1 contracts required for the prototype. The first functional milestone is:

> One source feeds a 100-tile BeltLine into a chest. Filling the chest blocks and settles the line. Removing an item from the chest wakes transport. Item counts, order and arrival ticks match the agreed reference.

Use a three-tile variant for inspecting each handoff. Keep production source edits limited to the files authorized for that task. Do not begin with a wholesale rewrite of `MachineGroup`, new rendering infrastructure and dynamic mod registration at the same time.

## 12. Agent handoff template

```text
Task IDs:
Owner:
Source files authorized for this task:
Files changed:
Behavior implemented:
Public contract changes:
Validation commands and observed results:
Benchmark configuration and results, if applicable:
Known limitations / unresolved decisions:
Dependencies for the next task:
Next concrete step:
```

Progress log:

- 2026-09-25: Created this plan from source inspection and the design discussion. Implementation, runtime equivalence and performance measurements remain outstanding.
