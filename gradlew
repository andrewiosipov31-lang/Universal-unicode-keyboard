#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
VERSION="8.7"
CACHE="$ROOT/.gradle-local/gradle-$VERSION"
ZIP="$ROOT/.gradle-local/gradle-$VERSION-bin.zip"
URL="https://services.gradle.org/distributions/gradle-$VERSION-bin.zip"
if [ ! -x "$CACHE/bin/gradle" ]; then
  mkdir -p "$ROOT/.gradle-local"
  if [ ! -f "$ZIP" ]; then
    if command -v curl >/dev/null 2>&1; then curl -L "$URL" -o "$ZIP"; else wget -O "$ZIP" "$URL"; fi
  fi
  rm -rf "$CACHE.tmp"
  mkdir -p "$CACHE.tmp"
  if command -v unzip >/dev/null 2>&1; then unzip -q "$ZIP" -d "$CACHE.tmp"; else echo "unzip is required" >&2; exit 1; fi
  mv "$CACHE.tmp/gradle-$VERSION" "$CACHE"
  rm -rf "$CACHE.tmp"
fi
exec "$CACHE/bin/gradle" "$@"
