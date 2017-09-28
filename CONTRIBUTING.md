# Developer Manual
This document briefly guides the developers who would like to contribute to ELVIS understand the code structure.

### Table of Contents

1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Architecture Design](#architecture-design)
4. [Knowledge Requirement](#knowledge-requirement)

## Introduction
ELVIS is a batch file downloader for NUS IVLE workbins. It allows the student to manage their files more efficiently.
## Installation
### Prerequisite
- IDE with Kotlin support (Recommend IntelliJ)
- Gradle
- Java SDK 8+

### Setting up
Assuming you are using latest version of IntelliJ:
1. Import and clone this repository as Gradle project.
2. Execute Gradle task to sync the dependencies.
3. Code and have fun!

## Architecture Design
![elvis-arch](http://sk.uploads.im/d/5mxwK.png)
### Components
**Main Launcher** is the entry point for the application. It lanches the UI.

**Utils** is the utility component where it stores constants and static helpers such as Preference API and API Key.

**Event Bus** facilitates communication between controllers and UI, as well as inter-UIs.

**UI** contains main user interface. It makes use of TornadoFX (extension for JavaFX) to render the UI.

**Controller** is the center of control for different components. Controllers do not know each other and they are mostly passive (called by other component, like UI).

## Knowledge Requirement
**Kotlin** is a fairly new language developed by Jetbrains and popularize by Google as first class language for next generation of Android development. Kotlin is 100% compatible with Java and can coexist within same project. While it is possible to write components in Java, we prefer Kotlin as our primary language in this project (except 3rd party libraries that this project depends on).

**Fuel** is a library that assist in RESTful requests. It simplify the process of managing HTTP verbs and parameters. LAPI component depends heavily on it.

**TornadoFX** is the Kotlin version of JavaFX. It does not render UI but make use of existing JavaFX with a twist of Kotlin flavor in it. This makes the UI render logic concise and easier to understand.

**Event Bus** is a pub/sub library written by Google that facilitates the communication process between different component. We make use of this library to display useful log messages from different components to the users.