# AdventOfCode2024

Find all the solutions to the [Advent of Code 2024](https://adventofcode.com/2024) written in Kotlin
and organized by day with the respective inputs in the [resources folder](solutions/src/main/resources).

## The Nicest Day?

### Day 15

The funniest day, for me, was Day 15!
I had a lot of fun implementing
a [visual mode simulation](https://github.com/ADarko22/AdventOfCode2024/blob/main/solutions/src/main/kotlin/edu/adarko22/Day15.kt#L192),
have look at it:

#### Compile the solution for Day 15 to a Jar

```bash
./gradlew clean build 
./gradlew generateJar -PmainFile=Day15.kt
```

#### Run it in the terminal

Make sure to use Java SDK 21+ for running the Jar.

```bash
java -jar build/libs/days/Day15.jar
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
./gradlew generateJar -PmainFile=Day14.kt
java -jar build/libs/days/Day14.jar
```

## Extra

### Auto-generate DayX Class

This project provides the script [PrepareWorkTask.kt](buildSrc/src/main/kotlin/edu/adarko22/PrepareWorkTask.kt)
which is handy to get started with the implementation for the solution of a new Day. It can be used with the command:

```bash
./gradlew PrepareWorkTask -Pday="day26" -Ppkg="edu.adarko22"
```

The script generates the `Day26.kt` file in the kotlin sources in the `solutions` module, with the predefined code
structure.

### Generate Runnable Jars

This project provides also the script [GenerateJar.kt](buildSrc/src/main/kotlin/edu/adarko22/GenerateJar.kt) to generate
a runnable Jar for all or a specific file:

- `./gradlew generateJar -Pall=true`
- `./gradlew generateJar -PmainFile=DayX.kt`

The jar files will be available in the `build/libs/days/` folder.