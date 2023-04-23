# Server Side Commands Mod

<!-- markdownlint-disable MD040 MD033 -->

<div align="center">

![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/55BTPGN3?label=Supported%20Versions&logo=Modrinth)
![Supported Environment](https://img.shields.io/badge/environment-server-yellow)

![Modrinth Downloads](https://img.shields.io/modrinth/dt/55BTPGN3?color=brightgreen&label=Downloads&logo=Modrinth)
![Modrinth Followers](https://img.shields.io/modrinth/followers/55BTPGN3?logo=Modrinth)

</div>

This server-side mod adds a couple commands for operators to use.

## Commands

Right now the only one is `/freeze`, which traps a player in place.

### /freeze

Requires [permission level][permission-level] **2** ([configurable][config]).

```
/freeze <player> <duration>
```

Gives `player` the following effects for `duration` seconds.

- Slowness VII
- Mining Fatigue X
- Jump Boost -V
- Blindness I
- Weakness XX

Usage is logged to server console ([configurable][config]).

---

_Logo is a combination of a command block and an image from flaticon.com. Flaticon attribution is below._

_<a href="https://www.flaticon.com/free-icons/server" title="server icons">Server icons created by Pixel perfect - Flaticon</a>_

[permission-level]: https://minecraft.fandom.com/wiki/Permission_level#Java_Edition_2
[config]: https://github.com/AdamRaichu/server-side-commands/wiki
