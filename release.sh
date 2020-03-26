#!/usr/bin/env bash

git branch -D release
git checkout -b release
git push origin release -f
git checkout master