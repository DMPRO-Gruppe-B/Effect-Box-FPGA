#!/usr/bin/env bash
source $(dirname $0)/common.sh
if [ "$1" = "FAST" ]; then
	SLOW="#"
	shift
fi
if [ "$1" = "NO_EDIF" ]; then
	SV_MODE=""
	EDIF_MODE="#"
	shift
else
	SV_MODE="#"
	EDIF_MODE=""
	shift
fi
if [ -z "$1" ]; then
	# make sucks
	shift
fi

TOP_MODULE="$1";  shift
OUTPUT_PATH="$1"; shift


# run yosys to synthesize the verilog file into a netlist
TMP="$(mktemp)"
colorize tee "$TMP" <<- EOT
	# read shit
	$(
		while ! test -z "$1"; do
			echo read_verilog $1
			shift
		done
	)

	# Compile to System Verilog
	${SV_MODE}hierarchy -top $TOP_MODULE
	${SV_MODE}proc; opt; techmap; opt
	${SV_MODE}write_verilog $TOP_MODULE.sv

	# Synthesize into netlist
	${EDIF_MODE}synth_xilinx -top $TOP_MODULE -edif $OUTPUT_PATH -flatten

EOT

colorize yosys -s "$TMP"
RET=$?
rm "$TMP"
exit $RET
