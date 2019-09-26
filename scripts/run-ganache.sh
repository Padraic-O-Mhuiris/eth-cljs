#!/usr/bin/env bash

CWD="${0%/*}"

GANACHE_CLI=$CWD/../node_modules/.bin/ganache-cli
ganache_args=(
    $GANACHE_CLI
    -i 999
    -b 10
    -a 1000
    -m "hill law jazz limb penalty escape public dish stand bracket blue jar"
)

"${ganache_args[@]}"
