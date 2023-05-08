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

Currently available commands are `/freeze` and `/track`.

Detailed information on commands and configuration can be found on [the wiki][wiki].

## FAQ

### The version title says for game version `1.19.4`, but Modrinth says `1.19-1.19.4`. What gives?

When I am developing, I use the latest version of the fabric api available for the version I am working on (which is usually the latest stable version of Minecraft). While I don't expect there to be incompatibility issues between the 1.19.4 and 1.19 api, there may be a change I am not aware of. Also, I generally like to encourage use of the latest version anyway.

### Forge port?

WIP. You can check out the `forge-main` branch on GitHub to see progress if you want.

**Status (5/8/23)**: Freeze command is working, beta version has been released. No track command yet.

Development will be focused on Fabric first, so Forge versions will probably lag behind the Fabric version.

### Will you support \<specific version\>?

I would be happy to look at the API docs for a specific version to see if the code I have in place now works (with possible minor changes). Please create an issue to request a version.

## My Modpack Policy

So my mod is posted under the MIT license, which means you can do a lot with it. However, if you include it in a modpack which is distributed on Modrinth, I would appreciate linking back here and/or inviting me to your project and giving me a small portion of the revenue from the project. This is completely volunteer work for me.

You may fork and edit this project as long as the title doesn't have my username, but again, I would appreciate attribution/revenue sharing.

Please do not post unedited copies under any circumstances.

---

_Logo is a combination of a command block and an image from flaticon.com. Flaticon attribution is below._

_<a href="https://www.flaticon.com/free-icons/server" title="server icons">Server icons created by Pixel perfect - Flaticon</a>_

[wiki]: https://github.com/AdamRaichu/server-side-commands/wiki
