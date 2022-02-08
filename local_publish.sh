#!/bin/bash

./gradlew :core:publishToMavenLocal \
          :xplat-common:publishToMavenLocal \
          :xplat-eventus-common:publishToMavenLocal \
          :xplat-yandex-pay:publishToMavenLocal
