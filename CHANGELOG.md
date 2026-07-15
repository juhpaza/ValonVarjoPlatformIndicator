# Changelog

## 0.1.2-early

- Fixed `/vvplatformindicator reload` and `/vvpi reload` by registering the reload command programmatically when Paper does not expose the `plugin.yml` command.
- Moved reload permission enforcement into the plugin so console/RCON can run reload without Brigadier hiding the command.

## 0.1.1-early

- Added Geyser API fallback detection when Floodgate is not loaded.
- Kept Floodgate as the primary platform detector when available.
- Documented that username prefixes are not used for Bedrock detection.

## 0.1.0-early

Initial early access release.

- Staff-only `[B]` Bedrock and `[J]` Java chat indicators.
- Per-recipient Paper `ChatRenderer` integration.
- Floodgate API based Bedrock detection.
- Configurable MiniMessage indicator colors.
- Reload command: `/vvplatformindicator reload` and `/vvpi reload`.
- Debug logging option for live testing.
