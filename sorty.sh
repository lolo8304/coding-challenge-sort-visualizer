#!/bin/zsh

JAVA_VERSION=""
GRADLE_FILES=(*.gradle(N))

if (( ${#GRADLE_FILES} > 0 )); then
  JAVA_VERSION="$(
    find . \
      -path './.gradle' -prune -o \
      -path './build' -prune -o \
      -path './*/build' -prune -o \
      \( -name '*.gradle' -o -name '*.gradle.kts' -o -name 'gradle.properties' \) \
      -type f \
      -exec sed -nE \
        -e 's/.*JavaLanguageVersion\.of\(([0-9]+)\).*/\1/p' \
        -e 's/.*JavaVersion\.VERSION_([0-9]+).*/\1/p' \
        -e 's/.*JavaVersion\.toVersion\(([0-9]+)\).*/\1/p' \
        -e 's/.*(sourceCompatibility|targetCompatibility)[^0-9]*([0-9]+).*/\2/p' \
        {} + \
      | head -n 1
  )"
fi

if [[ -z "$JAVA_VERSION" ]]; then
  echo "Could not find a Java version in Gradle files." >&2
else
  export JAVA_HOME="$(/usr/libexec/java_home -v "$JAVA_VERSION")"
fi

./sorty/build/install/sorty/bin/sorty "$@"
