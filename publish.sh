#!/bin/bash

./gradlew :core:publishReleasePublicationToArtifactoryRepository \
          :xplat-common:publishReleasePublicationToArtifactoryRepository \
          :xplat-eventus-common:publishReleasePublicationToArtifactoryRepository \
          :xplat-yandex-pay:publishReleasePublicationToArtifactoryRepository
