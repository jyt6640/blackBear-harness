#!/usr/bin/env bash
# 이 하네스 저장소의 단일 green-bar.
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"

scripts/local-agent/test-runner.sh
scripts/verify-harness.sh
