# Changelog

All notable changes to this project will be documented in this file.

This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Version [1.0.0](https://github.com/java-romp/jromp/releases/tag/1.0.0) (2024-08-12)

[1.0.0...0.0.1](https://github.com/java-romp/jromp/compare/1.0.0...0.0.1)

### ⚠️ Breaking Changes

* remove deprecated `Sub` class in [#2](https://github.com/java-romp/jromp/pull/2)
* implicit barriers in [#15](https://github.com/java-romp/jromp/pull/15)
* add `AtomicVariable` and remove atomic operations from `SharedVariable`
  in [#17](https://github.com/java-romp/jromp/pull/17)
* `begin` method to be integrated into block in [#25](https://github.com/java-romp/jromp/pull/25)

### ✨ Features

* add `toString` methods with specific format in [#4](https://github.com/java-romp/jromp/pull/4)
* `single` construct in [#12](https://github.com/java-romp/jromp/pull/12)
* add support for `atomic` construct in [#18](https://github.com/java-romp/jromp/pull/18)
* add Critical class implementation in [#21](https://github.com/java-romp/jromp/pull/21)
* add no-wait functionality to parallel and section methods in [#26](https://github.com/java-romp/jromp/pull/26)

### 🐞 Fixes

* prevent `ArrayIndexOutOfBoundsException` on sections construct in [#14](https://github.com/java-romp/jromp/pull/14)
* deps: Update all dependencies in [#23](https://github.com/java-romp/jromp/pull/23)
* deps: Update dependency org.apache.maven.plugins:maven-gpg-plugin to v3.2.5
  in [#27](https://github.com/java-romp/jromp/pull/27)

### 🛠️ Refactor

* method name from 'get' to 'value' in all Variable subclasses in [#6](https://github.com/java-romp/jromp/pull/6)
* reduction operations use general ones in [#20](https://github.com/java-romp/jromp/pull/20)

## Version [0.0.1](https://github.com/java-romp/jromp/releases/tag/0.0.1) (2024-07-18)
