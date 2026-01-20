#!/bin/bash
cd /home/kavia/workspace/code-generation/big-screen-media-hub-26375-26384/frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

