#!/usr/bin/env bash
# Usage: ./debloat.sh packages.txt   (one package name per line, # for comments)
# Removes each package for the current user only (reversible: see README).
set -u
while read -r pkg; do
  pkg="${pkg%%#*}"; pkg="$(echo "$pkg" | tr -d '[:space:]')"
  [ -z "$pkg" ] && continue
  echo "Removing $pkg"
  adb shell pm uninstall -k --user 0 "$pkg"
done < "${1:?give a packages file}"
