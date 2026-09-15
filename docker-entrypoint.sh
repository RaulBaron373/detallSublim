#!/bin/sh
set -eu

STORAGE_DIR="${APPLICATION_STORAGE_HISTORIAS_LOCATION:-/data/historias}"

if [ "$(id -u)" -eq 0 ]; then
    mkdir -p "$STORAGE_DIR"
    chown -R detallsublim:detallsublim "$STORAGE_DIR"

    USER_UID="$(id -u detallsublim)"
    USER_GID="$(id -g detallsublim)"

    exec setpriv \
        --reuid="$USER_UID" \
        --regid="$USER_GID" \
        --clear-groups \
        "$@"
fi

exec "$@"