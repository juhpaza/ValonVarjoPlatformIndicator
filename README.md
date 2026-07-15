# ValonVarjoPlatformIndicator

Early access Paper plugin that shows a staff-only Java/Bedrock chat platform indicator.

## What It Does

Staff with the configured permission see a small platform marker before chat messages:

```text
[B] [VIP] BedrockPlayer: Hello
[J] [MOD] JavaPlayer: Hello
```

Players without the permission see the normal chat message:

```text
[VIP] BedrockPlayer: Hello
[MOD] JavaPlayer: Hello
```

The plugin does not add anything to LuckPerms prefixes. It wraps Paper's `AsyncChatEvent` renderer per recipient, so the marker is visible only to recipients who are allowed to see it.

## Status

This is an early access release.

Tested on:

- Paper `26.2`
- Java `25.0.3`
- Geyser `2.11.0-SNAPSHOT`
- Floodgate `2.2.5-SNAPSHOT`
- EssentialsXChat `2.22.0`
- LuckPerms `5.5.53`

Other Paper, Geyser and Floodgate versions may work, but have not been fully tested yet.

## Requirements

- Paper or a compatible Paper-based server
- Geyser for Bedrock connections
- Floodgate for the most accurate Bedrock detection, recommended when available
- Java 21 or newer

Platform detection order:

1. Floodgate API, when Floodgate is loaded.
2. Geyser API fallback, when Floodgate is not loaded and Geyser is loaded.
3. Java fallback, when neither API is available.

The plugin does not detect Bedrock players from username prefixes.

## Permissions

```text
valonvarjo.chat.platformindicator
```

Allows the recipient to see `[B]` and `[J]` chat indicators.

```text
valonvarjo.chat.platformindicator.reload
```

Allows `/vvplatformindicator reload` and `/vvpi reload`.

Recommended LuckPerms setup:

```text
lp group helper permission set valonvarjo.chat.platformindicator true
lp group admin permission set valonvarjo.chat.platformindicator.reload true
lp group owner permission set valonvarjo.chat.platformindicator.reload true
```

Do not give `valonvarjo.chat.platformindicator` to normal player or VIP groups if the indicator should remain staff-only.

## Configuration

```yaml
permission: valonvarjo.chat.platformindicator
debug-log-platform-detection: false
detection:
  geyser-api-fallback: true
indicators:
  bedrock:
    open-bracket: "<gray>[</gray>"
    letter: "<dark_gray>B</dark_gray>"
    close-bracket: "<gray>]</gray>"
  java:
    open-bracket: "<gray>[</gray>"
    letter: "<green>J</green>"
    close-bracket: "<gray>]</gray>"
spacing: " "
```

Colors use MiniMessage.

Enable `debug-log-platform-detection` only during testing. It logs sender platform detection and recipient permission checks.

## Commands

```text
/vvplatformindicator reload
/vvpi reload
```

Reloads the plugin configuration.

## Compatibility Notes

The plugin uses Paper's `AsyncChatEvent` and wraps the current `ChatRenderer` at `MONITOR` priority. This was required for EssentialsXChat compatibility, because some chat plugins set their renderer late.

The plugin does not cancel chat messages or resend them manually.

## Building

```powershell
mvn clean package
```

The jar will be created in:

```text
target/ValonVarjoPlatformIndicator-0.1.3-early.jar
```

## Installation

1. Copy the jar to the server's `plugins/` folder.
2. Restart the server.
3. Configure LuckPerms permissions.
4. Test with one Bedrock sender and one Java sender.

## License

MIT License.
