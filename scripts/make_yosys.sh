#!/usr/bin/env bash
test "$1" = "" && (
	echo please supply at least one verilog file
	exit 1
)

mkdir -p yosys

TMP="$(mktemp)"
(
	while ! test -z "$1"; do
		echo "1 read_verilog $1"
		grep "^module" "$1" | cut -d" " -f2- | cut -d"(" -f1 |
		while read MODULE; do
			echo "6 show -format svg -colors 42 -stretch -prefix yosys/${MODULE}_gen $MODULE"
			echo "7 show -format svg -colors 42 -stretch -prefix yosys/${MODULE}_opt $MODULE"
		done
		shift
	done
	echo "0 # read files"
	echo "3 # process files"
	echo "4 proc"
	echo "5 opt; fsm; opt; memory_dff; memory_share; memory_collect; memory_map; opt_rmdff; wreduce; clean;"
) |
sort |
tee "$TMP" | cut -c3-

cat "$TMP"  | grep -v ^5 | grep -v ^7  | cut -c3- | yosys -Q -T -
cat "$TMP"  | grep -v ^6               | cut -c3- | yosys -Q -T -
