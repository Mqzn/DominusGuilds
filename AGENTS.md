# AGENTS — Quick guide for coding agents

Purpose: help an AI agent become productive in this repository (DominusGuilds). Focused, actionable notes derived from the source tree so the agent can make code changes, run the project, and respect project-specific conventions.

1) Big picture / architecture
- Multi-module Gradle project with three modules: `Guilds-API`, `Guilds-Velocity`, `Guilds-Bukkit` (Bukkit is a skeleton).
- `Guilds-API` contains the domain model and storage interfaces (e.g. `eg/mqzen/guilds/Guild.java`, `GuildManager.java`, `database/StorageFactory.java`). Treat this as platform-agnostic core.
- `Guilds-Velocity` is the primary working plugin implementation (commands, listeners, storage wiring, caching). Key entrypoint: `Guilds-Velocity/src/main/java/eg/mqzen/guilds/DominusGuilds.java`.
- Dataflow: commands → command handlers (under `commands/` and `commands/guildsubs/`) → `GuildManager` implementation (`SimpleGuildManager` under `base/`) → `GuildStorage` (selected via `GuildStorageFactory` / `database.yml`) → persistence (in-memory EMPTY or Mongo currently).

2) Key files to read first (in order)
- README.md — high-level overview (already up-to-date). (root)
- `Guilds-Velocity/src/main/java/eg/mqzen/guilds/DominusGuilds.java` — plugin bootstrap, library loader, config copy behavior.
- `Guilds-Velocity/src/main/java/eg/mqzen/guilds/CommandRegistrar.java` and `commands/GuildCommand.java` — command registration and context resolution.
- `Guilds-Velocity/src/main/java/eg/mqzen/guilds/base/SimpleGuildManager.java` — in-process caching behavior and how commands call into the manager.
- `Guilds-Velocity/src/main/java/eg/mqzen/guilds/database/GuildStorageFactory.java` and `database/storage/*` — storage selection and adapters (EmptyGuildStorage, MongoGuildStorage, Mongo adapters).
- `Guilds-API/src/main/java/eg/mqzen/guilds` — domain types: `Guild`, `GuildMember`, `GuildRole`, `DistinctTagTracker`, `FutureOperation`.

3) Project-specific conventions and patterns
- Storage selection is configuration-driven via `Guilds-Velocity/src/main/resources/database.yml` (copied on first-run). Valid types: `EMPTY`, `MONGO`, `SQLITE`, `MY_SQL`. Only `EMPTY` and `MONGO` are implemented. Changing type to an unimplemented option will fail at runtime.
- Async-style operations: the API uses a small custom `FutureOperation` (not java.util.concurrent.Future). When modifying persistence code, follow the existing async patterns in `GuildStorage` implementations.
- Caching: `SimpleGuildManager` uses Caffeine caches for guild-by-id, guild-by-name and player→guild. Follow the same cache invalidation patterns (look for `GuildUpdateAction` events) when editing data flow.
- Command framework: uses an Imperat-like command registrar (`VelocityImperat`). Command argument types and suggestion providers live under `commands/` (e.g., `GuildMemberArgumentType`, `NonGuildMembersSuggestionProvider`). Modify them to change tab-completion or validation.
- Resource copying: plugin copies default `config.yml` and `database.yml` into the plugin data directory on first run. Tests or local runs that expect these files should simulate that copy or run Velocity once to generate them.

4) Build, run and developer workflows (concrete commands)
- Build everything: `./gradlew build` (Windows: `.\gradlew.bat build` from repo root).
- Build runnable Velocity plugin (shadow jar): `.\gradlew.bat :Guilds-Velocity:shadowJar` — the `shadowJar` task is configured to output to the path given by the root project property `shadowJarOutputDir` (check root `build.gradle.kts` or `settings.gradle` for that property).
- Quick dev loop: run shadowJar, copy produced jar to your local Velocity proxy `plugins/` directory, start the proxy. On first start plugin will copy `config.yml` and `database.yml` into its data folder.
- To run unit tests (if added) use `.\gradlew.bat test` for all modules or `.\gradlew.bat :Guilds-API:test`.

5) Integration points & external dependencies
- Mongo: `MongoGuildStorage` + `database/mongo/*` adapters. Relevant config keys in `database.yml`: `storage.connection.mongo.connection-url` and `storage.connection.mongo.database`.
- Velocity libraries: `DominusLibs` and `VelocityLibraryManager` dynamically load external JARs. Be cautious when changing runtime dependency loading — tests may not load these same libs.
- Caffeine: used for in-memory caching (present in `SimpleGuildManager`). Follow existing eviction and lookup patterns.

6) Patterns to preserve when editing code
- Keep platform-neutral logic in `Guilds-API` (domain + interfaces). Implementations for Velocity should stay in `Guilds-Velocity` and reference API types only via the API module.
- Storage adapters implement `GuildStorage` and extend `BaseGuildStorage` — reuse `MongoDocument*Adapter` classes when adding new fields to persisted documents.
- Command handlers must throw the repository's domain exceptions (e.g., `NotInGuildException`, `InsufficientGuildPermissionException`) so the command framework can format help/error messages consistently.

7) Quick search hints for agents
- To find command implementations: search `commands/guildsubs/` under `Guilds-Velocity`.
- To find storage code: search `database/storage` and `database/mongo` under `Guilds-Velocity` and `database` under `Guilds-API`.
- To track cache logic: open `base/SimpleGuildManager.java`.

8) Safety notes for automated edits
- Don't change public API types in `Guilds-API` without updating all implementations; CI and runtime code expect stable method signatures.
- When changing storage schemas: update `MongoDocumentGuildAdapter` and `MongoDocumentObjectAdapter` to preserve backward compatibility and migration paths.

If you need more detail (examples of a specific command, or a walkthrough for adding a new storage), ask and I will expand with code pointers and a small step-by-step change set.

