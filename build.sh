#!/usr/bin/env bash

./gradlew clean
./gradlew build

zip -r build/RxCommon.zip native/build/konan/libs/* common/build/libs/* js/build/libs/* jvm/build/libs/*
