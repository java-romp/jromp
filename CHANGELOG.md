# Changelog

All notable changes to this project will be documented in this file.

This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## Version [2.1.0](https://github.com/java-romp/jromp/releases/tag/2.1.0) (2024-10-16)

[2.0.1...2.1.0](https://github.com/java-romp/jromp/compare/2.0.1...2.1.0)

### ⚠️ Breaking Changes

* renamed `block` to `parallel` in [#87](https://github.com/java-romp/jromp/pull/87)

> [!IMPORTANT]
> I don't want to publish new major versions for every small change (otherwise I will end up with a version
> like 100.0.0), so this issue is included in a minor version.

## Version [2.0.1](https://github.com/java-romp/jromp/releases/tag/2.0.1) (2024-10-12)

[2.0.0...2.0.1](https://github.com/java-romp/jromp/compare/2.0.0...2.0.1)

### ✨ Features

* implemented a new function to get the number of threads in the current parallel region in [#85](https://github.com/java-romp/jromp/pull/85)

## Version [2.0.0](https://github.com/java-romp/jromp/releases/tag/2.0.0) (2024-10-10)

[1.2.0...2.0.0](https://github.com/java-romp/jromp/compare/1.2.0...2.0.0)

### ⚠️ Breaking Changes

* naming and thread groups refactor in [#72](https://github.com/java-romp/jromp/pull/72)
* id removal in [#79](https://github.com/java-romp/jromp/pull/79)

### ✨ Features

* move `Utils` to base package and some methods to `JROMP` class in [#74](https://github.com/java-romp/jromp/pull/74)
* added more utility functions in [#76](https://github.com/java-romp/jromp/pull/76)
* `masked` method sends tasks to corresponding thread in the teams in [#80](https://github.com/java-romp/jromp/pull/80)
* implemented `barrier` as a method in [#82](https://github.com/java-romp/jromp/pull/82)

## Version [1.2.0](https://github.com/java-romp/jromp/releases/tag/1.2.0) (2024-09-27)

[1.1.1...1.2.0](https://github.com/java-romp/jromp/compare/1.1.1...1.2.0)

### ⚠️ Breaking Changes

* improved reduction operation creation in [#55](https://github.com/java-romp/jromp/pull/55)

### ✨ Features

* ReductionVariable constructor with Operation variable in [#54](https://github.com/java-romp/jromp/pull/54)
* package visibility for all operations in [#57](https://github.com/java-romp/jromp/pull/57)
* environment variable to set max threads in [#59](https://github.com/java-romp/jromp/pull/59)
* added `BigNumbers` to `InitialValues` in [#61](https://github.com/java-romp/jromp/pull/61)
* implemented `masked` directive in [#69](https://github.com/java-romp/jromp/pull/69)

### 🛠️ Refactor

* simplify `add` method in `Variables` class in [#63](https://github.com/java-romp/jromp/pull/63)
* replace `TreeMap` with `HashMap` in [#65](https://github.com/java-romp/jromp/pull/65)
* improve Barrier to include nowait inside it in [#67](https://github.com/java-romp/jromp/pull/67)

## Version [1.1.1](https://github.com/java-romp/jromp/releases/tag/1.1.1) (2024-08-25)

[1.1.0...1.1.1](https://github.com/java-romp/jromp/compare/1.1.0...1.1.1)

### 🐞 Fixes

* remove redundant exception in `ReductionVariable::merge` in [#50](https://github.com/java-romp/jromp/pull/50).

### 📚 Documentation

* updated developer contact information in the `pom.xml` file.

## Version [1.1.0](https://github.com/java-romp/jromp/releases/tag/1.1.0) (2024-08-25)

[1.0.0...1.1.0](https://github.com/java-romp/jromp/compare/1.0.0...1.1.0)

### ✨ Features

* add `update` method with `Operation` parameter to `Variable` interface
  in [#35](https://github.com/java-romp/jromp/pull/35).
* add `getWTime` method in [#36](https://github.com/java-romp/jromp/pull/36).
* variables are set for the whole parallel construct in [#37](https://github.com/java-romp/jromp/pull/37).
* add `nowait` parameter to `single` construct in [#38](https://github.com/java-romp/jromp/pull/38).

### 🐞 Fixes

* private var initial value is kept after the execution of parallel blocks
  in [#39](https://github.com/java-romp/jromp/pull/39).
* update parallel sections to handle single variables in [#43](https://github.com/java-romp/jromp/pull/43).
* remove `getVariables` from `Variables` in [#45](https://github.com/java-romp/jromp/pull/45).

### 🛠️ Refactor

* remove SectionBuilder and Section classes in [#41](https://github.com/java-romp/jromp/pull/41).

### 📚 Documentation

* add detailed Javadoc comments to Barrier class in [#47](https://github.com/java-romp/jromp/pull/47).

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
