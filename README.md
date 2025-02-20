# AdventOfCode2024

Find all the solutions to the [Advent of Code 2024](https://adventofcode.com/2024) written in Kotlin
and organized by day with the respective inputs in the [resources folder](solutions/src/main/resources).

## The Nicest Day?

### Day 15

The funniest day, for me, was Day 15!
I had a lot of fun implementing
a [visual mode simulation](https://github.com/ADarko22/AdventOfCode2024/blob/main/solutions/src/main/kotlin/org/example/Day15.kt#L192),
have look at it:

#### Compile the solution for Day 15 to a Jar

```bash
./gradlew clean build 
./gradlew generateJarFor -PmainFile=Day15.kt
```

#### Run it in the terminal

Make sure to use Java SDK 21+ for running the Jar.

```bash
java -jar build/libs/Day15.jar
```

To stop the simulation press `ctrl + C`.

Note that the IDE terminal may not support properly the ANSI escaping sequence
which makes the simulation output smooth.
I recommend running it in _iTerm2 (macOS)_, _Windows Terminal (Windows)_, and _GNOME Terminal (Linux)_ terminals.

## The "Must Run" Day?

### Day 14

I loved the surprise hidden in the output of the second part of Day 14! You Must Run It!

```bash
./gradlew clean build 
./gradlew generateJarFor -PmainFile=Day14.kt
java -jar build/libs/Day14.jar
```

## Extra

This project provides the script [PrepareWorkTask.kt](buildSrc/src/main/kotlin/org/example/PrepareWorkTask.kt)
which is handy to get started with the implementation for the solution of a new Day. It can be used with the command:

```bash
./gradlew PrepareWorkTask -Pday="day26" -Ppkg="org.example"
```

The script generates the `Day26.kt` file in the kotlin sources in the `solutions` module, with the predefined code
structure.