# DominusGuilds

> **Status: INCOMPLETE / early development**
>
> This repository is a work-in-progress guilds/clans system for Minecraft networks.
> The **Velocity module is the primary working implementation right now**.
> The **Bukkit module is currently a placeholder**.

## What is this?

**DominusGuilds** is a multi-module Gradle project that provides:

- A **platform-agnostic Guilds API** (domain model + interfaces)
- A **Velocity implementation** (commands, chat hooks, storage wiring)
- A planned **Bukkit/Paper implementation** (not implemented yet)

The project aims to support typical guild/clan features: members, roles/permissions, tags, invites, MOTD, alliances/enemies, and (eventually) cross-server syncing.

## Repository modules

| Module | Purpose | Current state |
|---|---|---|
| `Guilds-API` | Shared API: guild model, roles, tags, invites, manager interfaces, future-style operations | Implemented (core types exist) |
| `Guilds-Velocity` | Velocity plugin implementation: commands, listeners, storage selection, caching | Implemented (actively used) |
| `Guilds-Bukkit` | Bukkit/Paper plugin implementation | **INCOMPLETE** (skeleton only) |

## Implemented today (from sources)

### Velocity plugin

- Plugin entrypoint: `Guilds-Velocity/src/main/java/eg/mqzen/guilds/DominusGuilds.java`
- Command root: `/guild` (aliases: `/g`, `/clan`, `/c`)
- Uses a command framework (Imperat) with many subcommands registered
- Loads/copies default configs on first run:
  - `config.yml` (currently empty in resources)
  - `database.yml` (storage selection)
- Storage selection via `GuildStorageFactory`:
  - `EMPTY` (in-memory / dev)
  - `MONGO` (supported)
  - `SQLITE` / `MY_SQL` are present in config but **not implemented**
- In-memory caching via Caffeine:
  - guild-by-id, guild-by-name, player→guild mappings

### Core API (high level)

The API module contains the vendor-neutral “guild domain”:

- `Guild`, `GuildMember`, `GuildManager`
- `GuildRole` + permissions enum (`GuildRole.Permission`)
- `GuildTag`, distinct tag tracking (`DistinctTagTracker`)
- Invites (`GuildInviteList`)
- MOTD (`GuildMOTD`)
- Async-like operations (`FutureOperation`)

## Commands

The Velocity command system registers a lot of subcommands. **Not all are guaranteed complete yet**, but the structure is there.

- Root command: `/guild` (aliases: `/g`, `/clan`, `/c`)

### Command list (from sources)

Top-level subcommands:

- `/guild create <name>`
- `/guild disband`
- `/guild info`
- `/guild list`
- `/guild find <player>`
- `/guild permissions`
- `/guild motd <message> [-time <duration>]`
- `/guild invite <player>`
- `/guild join <guild>`
- `/guild deny <guild name>`
- `/guild leave`
- `/guild kick <player>`
- `/guild rename <name>`
- `/guild tag <tag>`
- `/guild tagcolor <color>`
- `/guild setrole <player> <role>`
- `/guild promote <player>`
- `/guild demote <player>`
- `/guild toggle` (alias: `/guild t`)
- `/guild chat <message>` (aliases: `/guild c`)

Relationship subcommands:

- `/guild ally add <guild>`
- `/guild ally remove <guild>`
- `/guild enemy add <guild>`
- `/guild enemy remove <guild>`

Tip: implementations live under `Guilds-Velocity/src/main/java/eg/mqzen/guilds/commands/guildsubs/`.

## Configuration

On first launch, the Velocity plugin copies defaults into its data folder.

### `database.yml`

Select storage here:

- `storage.type`: `EMPTY` | `MONGO` | `SQLITE` | `MY_SQL`
  - Note: `SQLITE` / `MY_SQL` are **not implemented yet** and will fail if selected.

Mongo settings (used when `storage.type: MONGO`):

- `storage.connection.mongo.connection-url`
- `storage.connection.mongo.database`

### `config.yml`

Currently **empty** (reserved for future settings like chat formats, limits, defaults, etc.).

## Build / Run (developer notes)

This repo uses **Gradle**.

- Build artifacts are produced per-module.
- The playable plugin at the moment is **`Guilds-Velocity`**.

General workflow:

1. Build the project with Gradle.
2. Put the Velocity jar into your proxy’s `plugins/` directory.
3. Start Velocity once to generate config files.
4. Edit `database.yml` (optional), then restart.

(If you want, I can tailor this section to your exact Gradle tasks once we confirm whether you’re using `build`, `shadowJar`, or both.)

## TODO / Roadmap

### Done / Present

- [x] Multi-module structure (`Guilds-API`, `Guilds-Velocity`, `Guilds-Bukkit`)
- [x] Core API model (guilds/members/roles/tags/invites/MOTD)
- [x] Velocity plugin bootstrap + config copying
- [x] Storage factory with `EMPTY` + `MONGO`
- [x] Caffeine caches in `SimpleGuildManager`
- [x] `/guild create <name>` command

### In progress / Partially implemented

- [ ] Verify and finish all Velocity subcommands (`commands/guildsubs/*`)
- [ ] Improve command UX (messages, validation, help text consistency)
- [ ] Expand `config.yml` with real settings (chat format, limits, defaults)
- [ ] Add tests for API + storage implementations

### Planned / Future work

- [ ] Bukkit/Paper plugin implementation (`Guilds-Bukkit`) — currently empty
- [ ] Network syncing / pub-sub (Redis is referenced in code but currently commented out)
- [ ] MySQL + SQLite storage implementations (config keys exist, runtime not implemented)
- [ ] Permissions integration strategy across platforms
- [ ] Better indexing/lookups (ex: name→UUID cache mentioned as a TODO in member lookup areas)

## Contributing

PRs and issues are welcome, but keep in mind this project is **INCOMPLETE** and the API/behavior may change.

Suggested contribution areas:

- Finish and validate Velocity subcommands
- Implement missing storages (MySQL/SQLite)
- Implement the Bukkit/Paper bridge module for GUIs and more interactions
- Add documentation and examples
