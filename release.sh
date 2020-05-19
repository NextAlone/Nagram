#!/usr/bin/env bash

git checkout master
git reset dev --hard
git checkout dev
git tag "$1" -f
git push origin "$1" -f