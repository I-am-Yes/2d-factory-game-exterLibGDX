# 2D Factory Game — Project Brief & Milestones

> **For:** AI agents and future collaborators  
> **Project:** 2d factory game exterLibGDX  
> **Stack:** Java + libGDX (LWJGL3 desktop backend)  
> **Updated:** 2026-09-14 (status sync — UI/settings foundation, pause-aware update domains, placement scaffolding, ghost previews, and plan systems)  
> **Reference game:** [Mindustry](https://github.com/Anuken/Mindustry) (learning objective)  
> **libGDX primer:** [A Simple Game](https://libgdx.com/wiki/start/a-simple-game)

---

## Goal

Build a **2D factory/automation game** in the spirit of **[Mindustry](https://github.com/Anuken/Mindustry)** — an automation tower-defense RTS written in **Java + libGDX**, the same stack as this project.

Mindustry is the **primary reference and learning objective**: study how it handles tiles, buildings, item flow, and simulation, then rebuild those ideas step by step in this repo. **Factorio** is secondary inspiration for automation concepts (belts, recipes, throughput), but the codebase to learn from is Mindustry.

The goal is to **understand and own the engine architecture** — not clone Mindustry wholesale on day one. libGDX is the platform layer. The game loop, world model, simulation, and factory logic are written here first in simple form, then grown toward Mindustry-like systems.

**Long-term topics to understand (visible in Mindustry):**

- Rendering, game loops, input, camera panning over a large map
- **Tiled maps** (`TiledMap`, `.tmx` / tilesets) then **seed-based world generation**
- Tile grid, block placement, tile entities (`Building` on a cell)
- Item stacks, inventories, conveyors / transport
- Fixed-tick simulation and performance at scale

**Map data strategy (this project — actual path taken):**

1. **Done:** Programmatic `TiledMap` in code (`World.generateWorld`) + `OrthogonalTiledMapRenderer` — same libGDX maps API as `.tmx`, built without Tiled editor at runtime.
2. **Done:** Seed-based fill via `MapGenerator` + `MapConfig` presets (jumped to M7b while learning M7).
3. **Optional later:** Load hand-authored `../assets/unpacked/maps` via `TmxMapLoader` for fixed layouts or templates.
4. **Art:** Tile textures under `../assets/unpacked/tiles`; swap PNGs without changing generation logic.

---

## Reference Game: Mindustry

| | |
|---|---|
| **Repo** | https://github.com/Anuken/Mindustry |
| **Genre** | Automation tower-defense RTS |
| **Stack** | Java, libGDX, Gradle multi-module (`core`, `desktop`, …) |
| **Why it fits** | Same language and framework as this project; open source; real factory-game systems |

### What to learn from Mindustry (by milestone)

| Our milestone | Mindustry concept | Where to look (when ready) |
|---------------|-------------------|----------------------------|
| M2–M5 | Camera, world coords, player movement | `core/src/mindustry/input/` |
| M6 | Sprites, textures, content assets | `core/assets/`, generated `Tex` / `Icon` |
| M7 | Tiled floor maps (`TiledMap`, `.tmx`) | `com.badlogic.gdx.maps.tiled`, [tile-maps wiki](https://libgdx.com/wiki/graphics/2d/tile-maps) |
| M7b | Seed-based map generation | Programmatic `TiledMap` / mutate layers — same renderer as M7 |
| M8 | Blocks on tiles, placement, selection | `core/src/mindustry/world/blocks/`, `BuildPlan` |
| M9 | Fixed logic tick (logic ≠ FPS) | `Logic` update loop, `Time.delta` / tick timing |
| M10 | Items, inventories, conveyors | `core/src/mindustry/type/`, conveyor blocks |
| M11 | Chunks, culling, large maps | `core/src/mindustry/world/` chunk systems |

Use Mindustry as a **read-only textbook**. Do not copy-paste its architecture into early milestones.

### What NOT to copy from Mindustry yet

Mindustry is a mature codebase with patterns that are **too advanced for M2–M8**:

| Mindustry feature | Why wait |
|-------------------|----------|
| `mindustry.gen` (generated entity/components) | Build-time code generation; learn plain structs first |
| Component ECS (`*Comp` classes merged into `Unit`, `Building`) | Powerful but hard to debug as a beginner |
| Arc framework (Anuken’s layer on libGDX) | Custom engine on top of libGDX; use vanilla libGDX first |
| Multiplayer / `@Remote` networking | Irrelevant until single-player factory loop works |
| Modding API, content loaders, full UI | Add after core placement + item flow works |

**Rule for agents:** Match Mindustry’s *gameplay ideas* (grid, blocks, items, tick), not its *final architecture*.

---

## Current State

**You are here:** **M1–M3 done** · **M5 largely done** · **M7/M7b largely done** · **M8 in progress** · **M9 next**

### What works today

| Item | Details |
|------|---------|
| Entry point | `core.app.Main` / `GameLoop` — update and `input()` / `logic()` / `draw()` structure, `ExtendViewport`, `resize()`, real-time vs game-time delta, and pause state |
| Launcher | `lwjgl3/.../Lwjgl3Launcher.java` |
| World + map render | `World.java` — programmatic `TiledMap` + `OrthogonalTiledMapRenderer` |
| Map generation | `MapGenerator.java` — seed-based `FloorType[][]`; modes `NOISE`, `ISLAND`, `PLAIN`; presets in `PresetMap` / `MapConfig` |
| Tile/assets catalog | `AssetsHandler.java` — packed `TextureAtlas`, `AssetType` catalog for floor/building tiles, nearest filtering, and `TiledMapTile` lookup |
| Player | `Player.java` + `Controller.java` — WASD, frame delta time, velocity lerp (accel/friction); **injected `PlayerController`** |
| Camera | `CameraController.java` — **lerped follow**, **animated zoom**, **`CURSOR_ZOOM` / `CENTER_ZOOM`**, **middle-mouse pan**, scroll blocked while panning, world clamp |
| Hover overlay | `OverlayRenderer.java` — Minecraft-style **corner brackets** on hovered tile; white/red by walkable; **slide animation**; padding modes `OUTSIDE` / `INSIDE` / `CENTER` |
| Tile queries | `World.isWalkable`, `getFloorAt`, `isInBounds`, `worldToTileX/Y` — used by overlay |
| Input | `InputHandler.java` — keyboard, scroll wheel, middle mouse, and Scene2D UI input routing |
| UI / settings | Scene2D `Stage`, hotbar, scrollable Settings panel, and a pause-on-open preference; UI and entry input continue updating while gameplay is paused |
| Debug | `Debug.java` + `DebugType` toggles — **PERFORMANCE** (FPS/heap), **RENDER** (grid), **CAMERA**, **INPUT**, **MAP_GENERATION**, and **EVENT** |
| Performance debug | `PerformanceDebugger.java` — register/enable metrics (`fps`, `ram` heap) with per-metric intervals; not enabled by default |
| Assets | Packed atlas generated from `../assets/unpacked/tiles`; floor and industrial building tile types are catalogued by `AssetsHandler` |
| Default run | `PresetMap.PLAIN`, map ~1024×512 tiles, camera debug enabled by default; seed wiring still needs cleanup |

### Partial / polish gaps

| Item | Status |
|------|--------|
| **M4** click-to-move | Not wired; left click currently belongs to block placement when a type is selected |
| **M4** world bounds clamp on player | Not in `PlayerController` — player can leave map |
| **M5** camera delta/focus polish | Camera refreshes delta during update, but delta ownership is not yet consistently centralized; `CENTER_ZOOM` can still drift when manually panned |
| **M6 / overlay** | `worldToTileX/Y` uses `(int)` cast; overlay uses `MathUtils.floor` — align for negative coords |
| **M7** load authored `.tmx` | Not wired at runtime (`TmxMapLoader` optional later) |
| **M7b** debug re-roll seed key | No runtime re-roll; `GameStart` assigns a seed on one config while generating the world from a separate preset config |
| **M7b** autotile edge variants | Middle tiles only; full neighbor autotiling not done |
| **Hygiene** | Window size set in both `Lwjgl3Launcher` and `Main.create()` |

### What does not exist yet

- Complete occupancy rules, placement confirmation/rejection feedback, and removal/selection behavior for buildings (M8 scaffolding exists; `BuildingType` selection is now exposed)
- Plan construction lifecycle is not fully wired (`PlanManager.update()` is empty and the active `Renderer` build-plan path is uninitialized)
- Fixed simulation tick / factory logic (M9+)
- Items, inventories, conveyors (M10)

### Prior work (separate C++ SDL2 prototype)

In another namePNG, milestones **1–4** were completed (window, WASD + delta time, camera, sprites). Milestone **5** (tilemap) was in progress. This libGDX project follows the **same factory-game roadmap**, but early steps should follow libGDX conventions from the official tutorial.

---

## How to Guide the Developer

- Complete beginner to engine building — **explain terms when first used**
- **One concept per step** — do not jump ahead
- Keep the game **runnable after every change**
- **data first** — decide what to store before adding patterns
- Always prepare for large scale codebase, such as optimising before growth code
- Keep code simple and readable
- Teach **one milestone at a time**; wait for confirmation before the next
- **Minimize scope** — smallest correct diff, match existing code style
- When stuck on libGDX basics, point to [A Simple Game](https://libgdx.com/wiki/start/a-simple-game) for the matching section
- When stuck on *what* to build next, point to [Mindustry](https://github.com/Anuken/Mindustry) for the matching system — but implement the **simplest version** from our milestone, not Mindustry’s full code

---

## libGDX Patterns (from official tutorial)

These are the conventions libGDX expects. Use them from **M2 onward**.

### Project layout

| Path | Role |
|------|------|
| `core/` | Shared game logic (`core/app/Main.java`, `core/world/World.java`, `core/Player.java`, …) |
| `lwjgl3/` | Desktop launcher (`core/lwjgl3/Lwjgl3Launcher.java`) |
| `assets/` | Textures, maps (`../assets/unpacked/maps`), audio |

Run: `gradlew lwjgl3:run` · Build: `gradlew build`

### Lifecycle (`ApplicationAdapter`)

| Method | When | Use for |
|--------|------|---------|
| `create()` | Once at startup | Load assets, create `SpriteBatch`, viewport, sprites |
| `render()` | Every frame | Call `input()` → `logic()` → `draw()` |
| `resize(w, h)` | Window resized | `viewport.update(width, height, true)` |
| `pause()` / `resume()` | App backgrounded | Optional; needed later on mobile |
| `dispose()` | App shutdown | `batch.dispose()`, `texture.dispose()`, etc. |

**Rule:** Never load `Texture` / `Sound` in a constructor. Load only inside `create()` after libGDX has started.

### Rendering pipeline (repeat every frame)

```java
private void draw() {
    ScreenUtils.clear(/* color */);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    // draw background, then world, then UI (back to front)
    batch.end();
}
```

- **`SpriteBatch`** — batches draw calls for the GPU; always pair `begin()` / `end()`
- **`ExtendViewport`** — maps world units to screen size while preserving the world view; camera is built in
- **World units** — use meters (or tiles), not pixels. Pick a scale (e.g. 64 px = 1 m) and stick to it
- **Draw order** — background first, entities on top; later draws cover earlier ones

### Movement and time

```java
float delta = Gdx.graphics.getDeltaTime(); // seconds since last frame
sprite.translateX(speed * delta);           // speed = units per second
```

Always multiply motion, timers, and spawn rates by `delta`. Never move a fixed amount per frame.

### Input

| Input | API |
|-------|-----|
| Keyboard (held) | `Gdx.input.isKeyPressed(Input.Keys.RIGHT)` |
| Mouse / touch | `Gdx.input.isTouched()`, `Gdx.input.getX()`, `Gdx.input.getY()` |
| Screen → world | `touchPos.set(x, y); viewport.unproject(touchPos);` |

Reuse one `Vector2 touchPos` field — do not `new Vector2()` every frame (avoids GC spikes).

### Useful classes (early game)

| Class | Purpose |
|-------|---------|
| `Sprite` | Position, size, draw state for a texture |
| `Texture` | Image in GPU memory; load from `assets/` |
| `Array<T>` | libGDX resizable list (prefer over `ArrayList` in hot paths) |
| `MathUtils.clamp` | Keep player inside world bounds |
| `Rectangle` | Simple overlap checks (later: selection, placement) |
| `Vector2` | 2D math; reuse instances |

### Coding conventions

- Use `float` literals with **`f`** suffix: `4f`, `0.15f` (OpenGL expects floats)
- Asset paths are **case-sensitive** — `"libgdx.png"` ≠ `"LibGDX.png"`
- Split `render()` into **`input()` / `logic()` / `draw()`** before the codebase grows
- **`dispose()`** all textures and the batch (tutorial skips this for one-screen demos; we should not)

### Learn later (not now)

- `AssetManager` — async asset loading (after basic `TmxMapLoader` works)
- `TexturePacker` / `TiledMapPacker` — atlas tilesets for fewer draw calls
- `Game` + `Screen` — multiple menus / game states
- `DelayedRemovalArray` — safe removal while iterating lists
- Perlin noise / infinite maps — after **M7b** basic seed generation works

---

## Core Architecture (Mindustry-Style Target)

Every frame:

1. Read input
2. Accumulate time; run **fixed simulation steps** (UPS)
3. Render world + UI (FPS)

### Two clocks

| Clock | Purpose |
|-------|---------|
| **Simulation tick** | Fixed step (target **60 UPS**). Belts, assemblers, recipes. Must **not** depend on frame rate. |
| **Rendering** | As fast as the monitor allows. Camera, sprites, UI. |

Until **M7b**, map layout is **authored or static** (`.tmx` or fixed rules). After **M9**, factory rules move into the fixed tick.

### World model (Mindustry vocabulary)

| Concept | Meaning |
|---------|---------|
| **Tiles** | Floor / terrain grid (what the map is made of) |
| **Blocks / buildings** | Things placed **on** tiles (drills, conveyors, turrets) — one per cell early on |
| **Items** | Stack counts in inventories and on conveyors — **not** thousands of physics objects |
| **Conveyors** | Lane-based transport; implement **last**, after inventories work |
| **Logic tick** | Fixed update step for machines and item movement (Mindustry separates this from rendering) |

### Scale principles (when needed, not day one)

- Grid index for O(1) “what is on this tile?”
- **Chunks** (e.g. 32×32) to simulate/draw only nearby areas
- Sprite batching (already handled by `SpriteBatch`)
- Avoid deep inheritance (`Entity → Building → Assembler`). Use enums, components, or plain data structs

### Target program shape (after M7)

```text
create()
  load tileset / TmxMapLoader OR generateMap(seed)  // M7 / M7b
  OrthogonalTiledMapRenderer(map, unitScale)
  init viewport + player

render()
  input()
  logic()
  draw()
    mapRenderer.setView(camera)
    mapRenderer.render()
    player.draw(batch)

dispose()
  mapRenderer.dispose()
  map.dispose()
  playerTexture.dispose()
  batch.dispose()
```

---

## Milestone Roadmap

> **Do these in order.** Each milestone maps to sections of [A Simple Game](https://libgdx.com/wiki/start/a-simple-game) where noted.

### M0 — Java basics

| | |
|---|---|
| **Goal** | Variables, functions, loops, classes, `ArrayList`, basic references |
| **Stop when** | You can store a list of `{x, y}` points and iterate them |
| **Status** | Assumed sufficient |

---

### M1 — Window + game loop

| | |
|---|---|
| **Goal** | App opens, clears screen, draws one image, closes cleanly |
| **libGDX** | `ApplicationAdapter`, `create` / `render` / `dispose`, `SpriteBatch`, `ScreenUtils.clear` |
| **Tutorial** | [Prerequisites](https://libgdx.com/wiki/start/a-simple-game#prerequisites), [Rendering intro](https://libgdx.com/wiki/start/a-simple-game#rendering) |
| **Status** | **Done** |

**Checklist**

- ✅ `SpriteBatch` created in `create()`, disposed in `dispose()`
- ✅ `render()` clears and draws each frame
- ✅ Window configured in `Lwjgl3Launcher.java`

---

### M2 — Viewport + render structure

| | |
|---|---|
| **Goal** | World measured in game units; `render()` split into three methods |
| **libGDX** | `ExtendViewport`, `viewport.apply()`, `batch.setProjectionMatrix(...)`, `resize()` |
| **Tutorial** | [Rendering — viewport](https://libgdx.com/wiki/start/a-simple-game#rendering) |
| **Status** | **Done** (keep using for Tiled maps in M7) |

**Deliverables**

- ✅ Add a world-unit viewport (current implementation uses `ExtendViewport` with map dimensions)
- ✅ Implement `resize(int width, int height)` → `viewport.update(...)`
- ✅ Split `render()` into `input()`, `logic()`, `draw()`
- ⬜ Draw test sprite at a world position with correct size (optional once tiles draw)
- ⬜ Remove or gate FPS `println` spam

**Why before movement:** Factory games need a stable coordinate system. Viewport + camera come before WASD.

---

### M3 — Player sprite + keyboard + delta time

| | |
|---|---|
| **Goal** | Move a sprite with arrow keys / WASD at the same speed on any PC |
| **libGDX** | `Sprite`, `Gdx.graphics.getDeltaTime()`, `Gdx.input.isKeyPressed`, `sprite.translateX/Y` |
| **Tutorial** | [Input Controls — keyboard](https://libgdx.com/wiki/start/a-simple-game#keyboard), [delta time](https://libgdx.com/wiki/start/a-simple-game#keyboard) |
| **Status** | **Done** (`Player.java`, `Controller.java`, `speed * delta`, velocity lerp) |

**Deliverables**

- ✅ Wrap texture in a `Sprite`; set size in world units
- ✅ Move with `speed * delta` (speed = world units **per second**)
- ✅ WASD / arrow keys

**Do not add yet:** camera follow (M5), Tiled maps (M7)

---

### M4 — Mouse/touch + world bounds

| | |
|---|---|
| **Goal** | Click/tap moves player; player cannot leave the viewport world |
| **libGDX** | `Vector2 touchPos`, `viewport.unproject`, `MathUtils.clamp` |
| **Tutorial** | [Mouse and Touch](https://libgdx.com/wiki/start/a-simple-game#mouse-and-touch-controls), [Game Logic — clamp](https://libgdx.com/wiki/start/a-simple-game#game-logic) |
| **Status** | **Partial** — `viewport.unproject` used for **tile hover overlay**; click-to-move and bounds clamp not yet |

**Deliverables**

- ✅ Reused `Vector3 tmp` + `viewport.unproject` in `Overlay` (mouse → tile)
- ⬜ Clamp sprite X/Y inside world bounds in `PlayerController`
- ⬜ Click-to-move or dedicated placement click handler

**Mindustry relevance:** Same `unproject` pattern Mindustry uses for **block placement** and **selection** on the grid.

---

### M5 — Camera + world larger than the screen

| | |
|---|---|
| **Goal** | World bigger than the window; camera follows player or pans |
| **libGDX** | `OrthographicCamera` or viewport camera offset; update camera before `batch.setProjectionMatrix` |
| **Tutorial** | Builds on [viewport/camera wiki](https://libgdx.com/wiki/graphics/2d/orthographic-camera) (beyond Drop demo scope) |
| **Status** | **Done** (follow, pan, zoom modes; optional polish on zoom anchor) |

**Deliverables**

- ✅ World size > viewport world size (e.g. 1024×512 tiles via `MapConfig`)
- ✅ Camera follows player with **lerp** (`FOLLOW_LERP_SPEED`)
- ✅ Re-follow player when moving after free cam (pan/zoom disables follow until player moves)
- ✅ All draws use world coordinates through the camera
- ✅ Mouse-wheel zoom with animated lerp (`ZOOM_LERP_SPEED`)
- ✅ **`ZoomMode.CURSOR_ZOOM`** — zoom toward world point under mouse at scroll time
- ✅ **`ZoomMode.CENTER_ZOOM`** — zoom toward player when following, view center when panned
- ✅ **Middle-mouse drag** to pan; scroll **blocked while middle held** (avoids pan+zoom fight)
- ✅ Camera clamped to world bounds
- ⬜ Edge-scroll pan (optional alternative to middle-mouse)

---

### M6 — Sprites + asset catalog

| | |
|---|---|
| **Goal** | Multiple PNGs from `assets/`; simple id → texture mapping |
| **libGDX** | Multiple `Texture`s, `Sprite` draw, dispose all in `dispose()` |
| **Tutorial** | [Loading Assets](https://libgdx.com/wiki/start/a-simple-game#loading-assets) |
| **Status** | **Partial** — multi-biome and building textures load from a packed atlas with nearest filtering; middle tiles only |

**Deliverables**

- ✅ Load floor and building tile regions through the packed atlas in `AssetsHandler`
- ✅ Use `MapConfig.tilePixel` (18) as the world tile size and atlas tile dimensions
- ✅ Nearest filter on tile textures
- ✅ Player texture from `../assets/unpacked/player`
- ✅ `AssetType` catalog + `FloorType` / `BuildingType` → `TiledMapTile` mapping
- ⬜ Full autotile edge/corner variants (top/left/right/bottom PNGs wired)
- ⬜ Swap placeholders for final tile art without changing map logic

**Note:** Runtime loading now uses the packed atlas. Autotile variants and final art remain separate polish work.

---

### M7 — Tiled map system (libGDX maps API)

| | |
|---|---|
| **Goal** | Floor map via **[Tiled](https://www.mapeditor.org/)** + libGDX [**tile maps wiki**](https://libgdx.com/wiki/graphics/2d/tile-maps) — not a hand-rolled `TileType[][]` draw loop |
| **libGDX** | `TmxMapLoader`, `TiledMap`, `TiledMapTileLayer`, `OrthogonalTiledMapRenderer`, `unitScale` |
| **Tutorial** | [Tile maps](https://libgdx.com/wiki/graphics/2d/tile-maps), [Orthographic camera](https://libgdx.com/wiki/graphics/2d/orthographic-camera) |
| **Mindustry** | Floor tiles — see `world/Tiles` (long-term; start with libGDX API) |
| **Status** | **Partial** — programmatic `TiledMap` works; `.tmx` load path not used at runtime |

**Why Tiled first:** Editor-visible layout, standard `.tmx`, same renderer used for procedural maps later.

**Note:** This project **skipped ahead** to code-built maps (`World.generateWorld`) while learning the libGDX maps API. Loading `world.tmx` via `TmxMapLoader` is still optional polish.

**Deliverables**

- ✅ Placeholder tileset under `../assets/unpacked/tiles` (Grass, Sand, Dirt, Water, Rock, …)
- ⬜ Map file `../assets/unpacked/maps` used at runtime via `TmxMapLoader` (file exists, not wired)
- ✅ Programmatic `TiledMap` + `TiledMapTileLayer` in `World.generateWorld`
- ✅ `OrthogonalTiledMapRenderer(map, unitScale)` — `unitScale = 1 / tilePixel`
- ✅ `ExtendViewport` in world/tile units (`world.getWorldWidth/Height()`)
- ✅ `draw()`: `world.render(camera)` → `player.draw(batch)`
- ✅ `dispose()`: `mapRenderer.dispose()`, `map.dispose()`
- ✅ Manual `TileType[][]` / `drawTile()` removed from `GameLoop`
- ✅ Read tile at cell — `World.hasTile`, `getFloorGrid`, `getFloorAt`, `isWalkable`, `worldToTileX/Y`
- ✅ `MapConfig.tilePixel` + `unitScale = 1 / tilePixel` aligned with `AssetsHandler.getTileWidth()`

**Do not in M7:** seed randomness, noise, infinite maps, `AssetManager` for maps

**Mindustry goal:** Visible floor grid — foundation for block placement (M8).

---

### M7b — Seed-based map generation

| | |
|---|---|
| **Goal** | Generate or refill map layout from a **`long seed`**; same `TiledMap` + renderer as M7 |
| **Pattern** | `Random(seed)` → rules fill `TiledMapTileLayer` cells (or mutate loaded template map) |
| **Wiki** | [Programmatic TiledMap](https://github.com/libgdx/libgdx/wiki/maps) examples |
| **Status** | **Largely done** — presets + island/plain lakes; polish remaining |

**Deliverables**

- ✅ `MapGenerator.generateTiledMap(mapConfig)` + `applyToLayer` → `TiledMap`
- ✅ Same seed → same map (`MapConfig.seed` + `Random(seed)` / `SampleGenerator`)
- ✅ Presets: `BIG_CONTINENT`, `MEDIUM_CONTINENT`, `SMALL_CONTINENT`, `ARCHIPELAGO`, `PLAIN`, `LARGE_LAKE`
- ✅ Gameplay grid: `FloorType[][]` + `World.getFloorGrid()` (walkable types, biome stats in debugger)
- ⬜ Optional: load `world.tmx` as **template** then randomize interior
- ⬜ UI or debug key to re-roll seed at runtime
- ⬜ Wire autotile edge PNGs for visual coast/rock blending

**Do not in M7b:** Perlin noise, infinite/chunk worlds (M11), save/load full world (M12)

**Keeps from M7:** `OrthogonalTiledMapRenderer`, tileset art, dispose rules — only **data source** changes (`load` vs `generate`).

---

### M8 — Entities on the grid

| | |
|---|---|
| **Goal** | Buildings / player occupy tiles; occupancy rules; selection |
| **libGDX** | `Array<Entity>`, spawn helper, `Rectangle` for hit tests |
| **Tutorial** | [Game Logic — Array, spawn, iterate](https://libgdx.com/wiki/start/a-simple-game#game-logic) |
| **Mindustry** | Block placement on tiles — see `world/blocks/`, placement preview |
| **Status** | **Partial** — building/grid/ghost/plan scaffolding and direct placement exist; selection and occupancy/construction rules remain incomplete |

**Pre-M8 done (hover / selection UX)**

- ✅ `Overlay` — corner-bracket highlight on hovered tile (`ShapeRenderer`, filled rects)
- ✅ Walkable vs blocked color (white / red via `World.isWalkable`)
- ✅ Smooth slide between tiles (exponential lerp; toggle `isOverlayRenderAnimationEnabled`)
- ✅ Bracket padding modes: `OUTSIDE`, `INSIDE`, `CENTER`
- ✅ Screen-pixel bracket thickness scales with camera zoom

**M8 work already present**

- ✅ `BuildingType`, `buildingGrid`, and a building map layer exist in `World`
- ✅ `BlockPlaceRequest` / `BlockPlaced` events route placement through `World`
- ✅ Click and drag placement exists in `PlayerAction`
- ✅ Selected-tile ghost preview exists in `GhostOverlay`
- ✅ `GhostType`, `PlanBuilder`, `PlanManager`, and `PlanConstructor` provide planning scaffolding; snapped/free ghost-line previews and ghost-plan placement work
- ✅ Hotbar/input path can select a `BuildingType`

**Deliverables**

- ⬜ Enforce one building per tile and distinguish floor replacement from building placement
- ⬜ Complete placement confirmation, rejection feedback, and removal/selection behavior
- ⬜ Connect plan rendering/construction lifecycle and safely remove completed plans

**Avoid:** Mindustry’s `mindustry.gen` component system and deep inheritance — use plain data first

---

### M9 — Fixed simulation tick (UPS)

| | |
|---|---|
| **Goal** | Factory logic runs at fixed 1/60 s steps, independent of FPS |
| **Pattern** | `accumulator += delta`; `while (accum >= TICK) { sim(TICK); accum -= TICK }` |
| **Tutorial** | Extends timer pattern from [Game Logic — dropTimer](https://libgdx.com/wiki/start/a-simple-game#game-logic) |
| **Mindustry** | Fixed logic updates — see `core/src/mindustry/core/Logic.java` |
| **Status** | Pending |

**First sim:** A drill/miner increments an item count in an adjacent storage each tick (Mindustry-style production, simplified).

**Rule:** Rendering stays in `draw()`; simulation never uses frame `delta` directly.

**Mindustry:** Study how block `update()` runs on a fixed tick, not every render frame.

---

### M10 — Items, inventories, conveyors

**Order matters** (matches Mindustry’s dependency chain):

1. **Item types + stack counts** — `Item` / stack size (Mindustry: `core/src/mindustry/type/`)
2. **Inventories** on storage and machines
3. **Router / inserter logic** — each tick, move 1 item A → B if allowed
4. **Conveyors last** — directional lanes, item slots on belt segments (Mindustry: conveyor blocks)

Do not implement turrets, waves, or PvP until this chain works in a test map.

---

### M11 — Performance (only when slow)

- Chunks, spatial grid lookup, draw culling (Mindustry splits the world for this)
- Skip simulating off-screen factory segments where safe
- **Correctness first, speed second**

---

### M12 — Mindustry-adjacent features (far future)

Only after M10 is solid. Pick one at a time:

- Power / energy network (generators, consumers)
- Crafting recipes (inputs → outputs over time)
- Drills on ore tiles, smelters, storage chains
- Simple enemies + turrets (tower-defense layer)
- Save/load map state

Each of these exists in Mindustry at production quality — treat the repo as reference, not a copy source.

---

## What Not To Do Yet

- Copy Mindustry’s `mindustry.gen`, Arc framework, or component ECS before **M9**
- `Game` / `Screen` multi-screen architecture (see [Extend the Simple Game](https://libgdx.com/wiki/start/simple-game-extended))
- Conveyors before inventories work
- `AssetManager` / `TexturePacker` before basic loading works
- OpenGL shaders / custom pipelines
- Multiplayer, modding API, full Mindustry UI, campaign, research trees
- Seed-based / noise maps before **M7** static Tiled pipeline works
- Procedural infinite worlds before **M7b** basic `Random(seed)` fill works
- Splitting into many classes before **M5** works in one file

---

## Key Files

| File | Purpose |
|------|---------|
| `core/src/main/core/app/Main.java` | libGDX lifecycle; creates and delegates to `GameLoop` |
| `core/src/main/core/app/GameLoop.java` | Frame update delta, input/logic/draw structure, render ordering, resize/dispose |
| `core/src/main/core/world/World.java` | `TiledMap` build/render, floor/building/ghost layers, placement, tile queries, dispose |
| `src/main/core/world/map` | Seed-based `FloorType[][]` + layer fill |
| `core/src/main/core/AssetsHandler.java` | Packed atlas and `AssetType` → `TiledMapTile` catalog |
| `core/src/main/core/Player.java` | Player sprite + draw |
| `core/src/main/core/controller/Controller.java` | WASD movement (accel/friction lerp) |
| `core/src/main/core/controller/CameraController.java` | Follow lerp, zoom modes, middle-mouse pan, world clamp |
| `core/src/main/core/render/OverlayRenderer.java` | Tile hover corner brackets + slide animation |
| `core/src/main/core/InputHandler.java` | Keyboard + scroll input |
| `core/src/main/data/map` | Map size, seed, presets, `tilePixel`, floor definitions |
| `core/src/main/data/map/asset` | Floor/building/ghost asset types and states |
| `core/src/main/data/debug` | Debug mode flags (PERFORMANCE, RENDER, CAMERA, …) |
| `core/src/main/core/debug/Debug.java` | Debug mode orchestration (update/render per type) |
| `core/src/main/core/debug/PerformanceDebugger.java` | FPS / heap metrics (register + per-metric interval) |
| `core/src/main/core/debug/RenderDebugger.java` | Visible tile grid when `DebugType.RENDER` |
| `core/src/main/core/debug/CameraDebugger.java` | Camera state debug when `DebugType.CAMERA` |
| `core/src/main/core/debug/MapGenDebugger.java` | Map generation debug reports |
| `lwjgl3/src/main/core/lwjgl3/Lwjgl3Launcher.java` | Window title, size, VSync |
| `../assets/unpacked/maps` | Optional authored map (not loaded at runtime yet) |
| `../assets/unpacked/tiles` | Per-biome tile PNGs |
| `assets/` | All runtime assets (case-sensitive paths) |
| `core/MILESTONES.md` | This document |

---

## Next Step for Agent

**Current focus:** **M8** — finish occupancy validation, placement confirmation, and plan construction wiring.

Suggested order:

1. **M8** — enforce occupancy and finish direct/ghost/plan placement behavior
2. **M8** — connect plan rendering/construction updates and add clear placement feedback
3. **M4/M5 polish** — restore player bounds clamp and centralize frame-delta ownership/camera focus behavior
4. **M7b/M7 polish** — fix seed wiring, add a debug re-roll, autotile edge variants, and optionally load authored `.tmx`

**Example prompt:**

> Follow `core/MILESTONES.md`. M8 placement scaffolding and `BuildingType` selection exist — finish occupancy validation, ghost/plan construction, and keep the diff minimal.

---

## Where you are (quick read)

```text
M0 ✅  M1 ✅  M2 ✅  M3 ✅
M4 ⬜ partial (hover unproject ✅; click-move + bounds clamp ⬜)
M5 ⬜ partial (lerp follow, zoom modes, middle-mouse pan ✅; delta/focus polish ⬜)
M6 ⬜ partial (packed atlas + tile catalog ✅; autotile edges ⬜)
M7 ⬜ partial (programmatic TiledMap ✅; TmxMapLoader ⬜)
M7b ✅ largely (seed presets ✅; re-roll + autotile polish ⬜)
Pre-M8 hover overlay ✅ (corner brackets, animation, walkable tint)
M8 ⬜ partial ← YOU ARE HERE (building/grid/ghost/plan scaffolding + building selection ✅; occupancy and construction wiring ⬜)
M9+ ⬜ factory simulation not started
```

---

## Reference Links

| Topic | URL |
|-------|-----|
| **Mindustry (reference game)** | https://github.com/Anuken/Mindustry |
| Mindustry — building from source | https://github.com/Anuken/Mindustry#building-from-source |
| **Tiled (map editor)** | https://www.mapeditor.org/ |
| **Tile maps (libGDX wiki)** | https://libgdx.com/wiki/graphics/2d/tile-maps |
| A Simple Game (libGDX tutorial) | https://libgdx.com/wiki/start/a-simple-game |
| Extend the Simple Game (Screens) | https://libgdx.com/wiki/start/simple-game-extended |
| Application life cycle | https://libgdx.com/wiki/app/the-life-cycle |
| Orthographic camera | https://libgdx.com/wiki/graphics/2d/orthographic-camera |
| Viewports | https://libgdx.com/wiki/graphics/2d/viewports |

### Mindustry code map (study when milestone reached)

| System | Path in Mindustry repo |
|--------|------------------------|
| World / tiles | `core/src/mindustry/world/` |
| Blocks (buildings) | `core/src/mindustry/world/blocks/` |
| Items & liquids | `core/src/mindustry/type/` |
| Input & placement | `core/src/mindustry/input/` |
| Game loop / logic | `core/src/mindustry/core/Logic.java` |
| Entities (advanced) | `core/src/mindustry/entities/comp/` |

---

## Master checklist

Track progress at a glance. Check items only when they work in a runnable build. Details live in each milestone section above.

| Symbol | Meaning |
|--------|---------|
| ✅ | **Done** — works in a runnable build |
| ⬜ | **Todo** — not done yet |

---

### Foundation

- ✅ **M0** — Java basics sufficient for loops, classes, arrays
- ✅ **M1** — Window, game loop, `SpriteBatch`, clean `dispose()`
- ✅ **M2** — `ExtendViewport`, `resize()`, `input()` / `logic()` / `draw()`
- ✅ **M2** — FPS/heap logging gated via `DebugType.PERFORMANCE` + `PerformanceDebugger` (not raw spam)

### Player & world view

- ✅ **M3** — `Player` sprite, WASD, movement uses `delta` time (+ velocity lerp)
- ⬜ **M4** — Player clamped inside world bounds (add back to `PlayerController`)
- ✅ **M4** — `viewport.unproject()` for **tile hover** (`Overlay`)
- ⬜ **M4** — Click-to-move or placement click
- ✅ **M5** — Map larger than screen; camera **lerp**-follows player
- ✅ **M5** — Mouse-wheel zoom (animated) + **`CURSOR_ZOOM` / `CENTER_ZOOM`**
- ✅ **M5** — Middle-mouse pan; scroll blocked while panning
- ⬜ **M5** — Edge-scroll pan (optional)

### Assets

- ✅ **M6** — Multi-biome/building atlas + player texture load/dispose
- ✅ **M6** — `AssetType` → `TiledMapTile` catalog
- ✅ **M6** — World tile size aligned with atlas / `MapConfig.tilePixel`
- ⬜ **M6** — Full autotile edge variants wired
- ⬜ **M6** — Final tile art swapped without breaking map logic

### Map — Tiled (M7)

- ✅ **M7** — Tileset images in `../assets/unpacked/tiles`
- ⬜ **M7** — Runtime load of `../assets/unpacked/maps` via `TmxMapLoader`
- ✅ **M7** — Programmatic `TiledMap` + `TiledMapTileLayer` (`World.generateWorld`)
- ✅ **M7** — `OrthogonalTiledMapRenderer(map, unitScale)`
- ✅ **M7** — `ExtendViewport` in world/tile units
- ✅ **M7** — `world.render(camera)` before player draw
- ✅ **M7** — `mapRenderer.dispose()` + `map.dispose()` in `dispose()`
- ✅ **M7** — Manual per-tile draw loop removed from `GameLauncher`
- ✅ **M7** — Tile queries via `World.hasTile` / `getFloorGrid` / `getFloorAt` / `isWalkable`

### Map — Seed generation (M7b)

- ✅ **M7b** — `MapGenerator` builds grid + fills layer from seed
- ✅ **M7b** — Same seed → same layout; presets (`PLAIN`, `ISLAND`, …)
- ⬜ **M7b** — Optional `.tmx` template + randomized interior
- ✅ **M7b** — Gameplay grid (`FloorType[][]`) for biome/walk logic
- ⬜ **M7b** — Debug re-roll seed key

### Pre-M8 / overlay (hover UX)

- ✅ **Hover overlay** — corner brackets on hovered tile (`Overlay`)
- ✅ **Walkable tint** — white vs red from `World.isWalkable`
- ✅ **Slide animation** between tiles (toggleable)
- ✅ **Bracket padding modes** — OUTSIDE / INSIDE / CENTER

### Factory core (M8–M10)

- ⬜ **M8 partial** — Building/grid/ghost/plan scaffolding, direct placement, ghost-line previews, and building selection exist; one-per-tile rules, confirmation, and construction wiring remain
- ⬜ **M9** — Fixed simulation tick (60 UPS); sim separate from `draw()`
- ⬜ **M10** — Item types + stack counts
- ⬜ **M10** — Inventories on storage / machines
- ⬜ **M10** — Router / inserter move items each tick
- ⬜ **M10** — Conveyors (directional lanes) last

### Scale & polish (when needed)

- ⬜ **M11** — Chunks / culling / spatial lookup (only if performance requires)
- ⬜ **M12** — Power, recipes, drills, enemies/turrets, save/load (one at a time)

### Project hygiene

- ✅ Packages under `core/src/main/core/`; Gradle source root configured
- ✅ Run via `gradlew lwjgl3:run` with working directory `assets/`
- ⬜ Window size set in `Lwjgl3Launcher` only (not duplicated in `Main.create()`)
- ✅ `Player` uses injected `PlayerController` from `GameLauncher` (not `new Controller()`)

**Current focus:** **M8** — finish building placement on grid: occupancy, confirmation, and plan construction wiring; then start M9 fixed 60 UPS simulation.
