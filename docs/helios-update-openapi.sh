#!/bin/zsh

set -euo pipefail
scp "${0:A:h}"/*.yaml se:public_html/swagger/
