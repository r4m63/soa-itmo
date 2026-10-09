#!/bin/sh

set -eu
dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
scp "$dir"/*.yaml se:public_html/swagger/
