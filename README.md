Gradle plugin for building Swift for Android using the Swift Package Manager.

Very early proof of concept; only works for Debug targets, and is pretty awful. (Your help is appreciated!)

Licensed under Apache License version 2.0.

## Setup

You'll need the latest version of the SwiftAndroid toolchain (old versions
do not have the Swift Package Manager)

You need to add swift to the PATH:

`export PATH=$PATH:/path/to/swift/bin`

Then run in the source folder

`./gradlew install`

to install the addon.

## Usage

See [sample programs](https://github.com/SwiftAndroid/swift-android-samples).
