#!/usr/bin/env bash

source $(dirname $0)/common.sh

OUTPUT="$1"; shift
TOP_MODULE="$1"; shift

if test -z "$1"; then
	echo "No vhdl files supplied!" >&2
	exit 1
fi

TMP="$(mktemp)"
(
	echo \#Read sources...
	while ! test -z "$1" ; do
		echo read_vhdl "$1"
		shift
	done
	echo
	echo \#Process design...
	echo synth_design -top $TOP_MODULE
	echo
	echo \#Dump results design...
	echo write_verilog -force $OUTPUT
) | colorize tee "$TMP"

colorize $XILINX_TOP_DIR/bin/vivado -mode batch -source "$TMP"

rm "$TMP"
rm *.jou *.log
