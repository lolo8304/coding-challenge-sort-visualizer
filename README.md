# Sorty

Sorty is a Java 26 command-line sorting visualizer. It generates random integers, sorts them with the current sorting algorithm, and can display progress in a fullscreen Lanterna text UI or as console logs.

The project is a Gradle multi-project build with one module:

- `sorty`

## Requirements

- Java 26
- macOS `/usr/libexec/java_home` support for `sorty.sh`
- Gradle wrapper from this repository

The Gradle build is configured for Java 26 in `sorty/build.gradle`. The root `gradle.properties` pins the Gradle daemon to the local Java 26 JDK path.

## Build

Run tests:

```bash
env -u JAVA_HOME ./gradlew test
```

Build the install distribution:

```bash
env -u JAVA_HOME ./gradlew installDist
```

Run the installed command through the helper script:

```bash
./sorty.sh
```

`sorty.sh` reads the Java version from Gradle files in the current directory and uses `/usr/libexec/java_home` to select a matching JDK before launching `sorty`.

## Usage

Lanterna fullscreen UI is the default:

```bash
./sorty.sh -n 40 --seed 1 -f
```

Console logging can be selected explicitly:

```bash
./sorty.sh -c -vv -n 10 --seed 1 -f
```

Descending sort:

```bash
./sorty.sh -d -n 40 --seed 1
```

Custom generated number range:

```bash
./sorty.sh -n 30 --seed 7 -from 10 -to 200
```

Stop the fullscreen UI with Ctrl-C.

Pause the fullscreen UI with Space. Press `s` while running or paused to restart from the original values.

## CLI Options

```text
Usage: sorty [-cdfhlmsvV] [-vv] [-from=MIN] [-n=TOTAL] [--seed=SEED]
             [--speed=SPEED] [-to=MAX]
```

Options:

- `-n`, `--number-count=TOTAL`: total generated numbers to sort. Default: `20`.
- `--seed=SEED`: random seed. Default: `0`.
- `-from`, `--from=MIN`: smallest generated number. Default: `10`.
- `-to`, `--to=MAX`: largest generated number. Default: `100`.
- `-d`, `--descending`: sort largest to smallest.
- `-l`, `--lanterna`: use fullscreen Lanterna text UI. This is the default.
- `-c`, `--console`: use console UI logging instead of Lanterna.
- `-v`: verbose output.
- `-vv`: extra verbose output.
- `-f`, `--fast`: fast visualization speed, `25ms` delay.
- `-m`, `--medium`: medium visualization speed, `50ms` delay.
- `-s`, `--slow`: slow visualization speed, `100ms` delay.
- `--speed=SPEED`: explicit speed enum: `FAST`, `MEDIUM`, or `SLOW`. Default: `MEDIUM`.
- `-h`, `--help`: show help.
- `-V`, `--version`: show version.

Only one startup speed shortcut may be selected at a time: `--slow`, `--medium`, or `--fast`.

Important: `-s` is the short option for `--slow`. Use `--seed` for random seed values.

## UI Behavior

Lanterna renders bars in the terminal: was an Google Code and now on github https://github.com/mabe02/lanterna

- Each bar represents one generated value.
- The value is displayed centered above its bar.
- Compared indices are highlighted yellow.
- Swapped indices are highlighted orange.
- The header displays number count, total operations, comparisons, swaps, and value accesses.
- Press Space to pause the animation, `n` to step one frame, or `s` to restart from the original values.
- The final screen remains visible for `500ms` as part of the close procedure.

Console UI logs protocol events and summary counters when verbose flags are enabled.

### Snapshots

Bubble sort - yellow comparing
![screenshot-bubble-sort-1.png](images/screenshot-bubble-sort-1.png)

Bubble sort - orange swapping
![screenshot-bubble-sort-2.png](images/screenshot-bubble-sort-2.png)


Bubble sort - almost done
![screenshot-bubble-sort-3.png](images/screenshot-bubble-sort-3.png)

## Development Notes

The core flow is:

- `Sorty`: Picocli command and option parsing.
- `Sorter`: random number generation and algorithm wiring.
- `EventHandler`: protocol counters and speed throttling.
- `SorterProtocol`: UI/event protocol.
- `BubbleSorter`: current sorting algorithm.
- `LanternaUiDelegate`: fullscreen terminal visualization.
- `ConsoleUiDelegate`: console event logging.
- `NoOpUiDelegate`: quiet test/default delegate for non-CLI sorter use.

When CLI switches are changed, update this README in the same change.
