# PEPSE
Precise Environmental Procedural Simulator Extraordinaire

A 2D side-scrolling simulation built with DanoGameLab featuring procedural terrain generation, day/night cycle, avatar with energy system, and trees with fruits.

![Gameplay Screenshot](screenshot.png)

## How to Run
- Java 17 required
- Open in IntelliJ
- Ensure `libs/DanoGameLab/DanoGameLab.jar` is added as a dependency
- Run `PepseGameManager`

## Design

### Avatar
- **State Pattern** — `IdleState`, `RunState`, `JumpState` each handle their own logic
- **Energy** — encapsulated in a dedicated `Energy` class
- **EnergyDisplay** — receives a `Supplier<Float>` callback, decoupled from `Avatar`
- Double jump implemented inside `JumpState`

### Trees
- `Flora` receives a `groundHeightAt` callback instead of depending on `Terrain` directly
- Leaves use `Transition` + `ScheduledTask` with random delay for natural wind animation
- Fruits disappear on collision and reappear after 30 seconds

### Infinite World
- `InfiniteWorldManager` manages the world in chunks using a `Map`
- Distant chunks are removed to save memory
- `Objects.hash(x, seed)` ensures consistent world generation across sessions

## API Changes
- `AvatarState` — interface separating state logic from `Avatar`
- `Energy` — new class encapsulating energy management
- `Flora` — `onFruitEaten` callback so `Fruit` notifies `Avatar` without direct coupling