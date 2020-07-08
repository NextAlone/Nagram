#!/bin/bash

git tag "$1" -f &&
git push origin "$1" -f