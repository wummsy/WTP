# WTP Utils

A Paper plugin for **Minecraft 1.21.11** adding:

- **TPA teleport requests**: `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny`, `/tpcancel`
  - Configurable request timeout, cooldown, and optional "don't move" warmup
    (cancelled on movement or damage).
- **`/rtp`** — random teleport with a **GUI dimension picker**. Running `/rtp`
  with no arguments opens a chest GUI listing every *enabled* dimension; click
  one to be randomly teleported there. `/rtp <world>` skips the GUI and RTPs
  directly into that world if it's enabled and you have permission.
- Safe-location finder: scans for solid ground with clear head/feet space and
  avoids lava, water, fire, cacti, magma blocks, etc. (configurable list).
- **Full config.yml control**:
  - Enable/disable RTP per dimension (`rtp.dimensions.<world>.enabled`)
  - Per-dimension center coordinates, min/max radius, permission node, icon,
    display name, and lore
  - Global RTP cooldown (`rtp.cooldown-seconds`) and TPA cooldown
    (`tpa.cooldown-seconds`)
  - Every message is editable in `messages:`

## Commands

| Command                | Description                                   | Permission              |
|-------------------------|------------------------------------------------|--------------------------|
| `/tpa <player>`         | Request to teleport to a player                | `wtputils.tpa`            |
| `/tpahere <player>`     | Request a player to teleport to you             | `wtputils.tpa`            |
| `/tpaccept`             | Accept the pending incoming request             | `wtputils.tpa`            |
| `/tpdeny`               | Deny the pending incoming request               | `wtputils.tpa`            |
| `/tpcancel`             | Cancel your outgoing request                    | `wtputils.tpa`            |
| `/rtp`                  | Open the dimension-picker RTP GUI               | `wtputils.rtp`            |
| `/rtp <world>`          | RTP directly into `<world>`                     | `wtputils.rtp` + per-world|
| `/wtputils reload`      | Reload `config.yml`                             | `wtputils.reload`         |

## Permissions

- `wtputils.tpa` (default: true)
- `wtputils.rtp` (default: true)
- `wtputils.rtp.<world>` — per-dimension node set in config (e.g. `wtputils.rtp.world`, `wtputils.rtp.nether`, `wtputils.rtp.end`)
- `wtputils.rtp.*` — bypasses all per-dimension RTP permission checks
- `wtputils.bypass.cooldown` — skips both TPA and RTP cooldowns
- `wtputils.reload` (default: op)

## Configuration

See `src/main/resources/config.yml` for the full, commented default config.
Dimension keys under `rtp.dimensions` must match your server's actual world
folder/name (e.g. `world`, `world_nether`, `world_the_end`, or any custom
world name if you run multiverse-style setups — just add a new entry).

```yaml
rtp:
  cooldown-seconds: 30
  dimensions:
    world:
      enabled: true
      display-name: "&aOverworld"
      icon: "GRASS_BLOCK"
      permission: "wtputils.rtp.world"
      center-x: 0
      center-z: 0
      min-radius: 100
      max-radius: 5000
```

## Building

This project uses Maven and depends on the Paper API.

```bash
mvn clean package
```

The compiled jar will be at `target/WTPUtils-1.0.0.jar`. Drop it into your
server's `plugins/` folder and restart (or use a plugin manager that supports
hot-reload).

> **Note:** The `pom.xml` pins `paper-api` to `1.21.11-R0.1-SNAPSHOT`. If that
> exact artifact isn't published yet on `repo.papermc.io` when you build,
> check https://repo.papermc.io/#browse/browse:maven-public:io%2Fpapermc%2Fpaper%2Fpaper-api
> for the closest available 1.21.x version string and update the `<version>`
> tag accordingly — the code itself only uses stable, long-standing Bukkit/Paper
> API (commands, inventories, PersistentDataContainer, events), so it will
> compile against any recent 1.21.x Paper API with little to no changes.

## Requirements

- Java 21+
- Paper 1.21.11 (or a nearby 1.21.x Paper build)
