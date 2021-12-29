# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

A collection of changes that were not pushed to the `master` branch yet.

### Added
- `CHANGELOG` file to document notable changes

### Changed
- `groupId` name to `com.lokkeestudios`
- Moved from Maven to Gradle
- Some `Database` variable names of the `ItemManager`, to be more self-explanatory
- Moved to Paper 1.18
- Moved to Java 17

### Deprecated

### Removed
- A bunch of redundant code inside `ItemGui`

### Fixed
- `loadItems()` in `ItemManager` causing an empty ResultSet Exception.
- `Database` statement Objects not closing properly
- Navigation arrows of `ItemGui` not displaying the right page
- Filter buttons of `ItemGui` not cycling correctly in both directions

## [0.1.0-SNAPSHOT]

This is the first version and thus adds all the basic Files.

### Added
- `ItemSystem` Base Structure
- `Core` System Utils and Functionality

[Unreleased]: https://github.com/lokkeeWasTaken/Skylands/releases/tag/Unreleased
[0.1.0-SNAPSHOT]: https://github.com/lokkeeWasTaken/Skylands/releases/tag/0.1.0-SNAPSHOT
