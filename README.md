# Fabrication Engine

| Release Branch         | Coverage   | Status      |
| ---------------------- |:----------:| ----------- |
| `main`                 | [![codecov](https://codecov.io/gh/MrTJP/fabrication-engine/branch/main/graph/badge.svg?token=PF8PTB92Y0)](https://codecov.io/gh/MrTJP/fabrication-engine)             | Active development |
| `publish/release`      | [![codecov](https://codecov.io/gh/MrTJP/fabrication-engine/branch/publish/release/graph/badge.svg?token=PF8PTB92Y0)](https://codecov.io/gh/MrTJP/fabrication-engine)  | ![badge](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/MrTJP/405427e70bea42393b7b8e4548393e9a/raw/fabrication-engine-release.json) |
| `publish/beta`         | [![codecov](https://codecov.io/gh/MrTJP/fabrication-engine/branch/publish/beta/graph/badge.svg?token=PF8PTB92Y0)](https://codecov.io/gh/MrTJP/fabrication-engine)     | ![badge](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/MrTJP/405427e70bea42393b7b8e4548393e9a/raw/fabrication-engine-beta.json)    |

A simple, minimal logic circuit compiler and simulator.
* Written as a Scala library
* Describe a circuit drawing by adding tiles to a 3D map
* Flat-build that drawing into a logical schematic
* Convert logical schematics to simulatable objects

## Usage

1. Add the following to your `build.gradle` file:

``` groovy

repositories {
    maven { 
        url = "https://proxy-maven.covers1624.net/" 
    }
}

dependencies {
    implementation 'com.github.mrtjp:fabrication-engine:<X.Y.Z>'
}

```

2. `// TODO add code sample`