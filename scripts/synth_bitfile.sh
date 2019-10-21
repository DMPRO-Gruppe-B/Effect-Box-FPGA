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

TOP_MODULE="$1";       shift
XILINX_PART="$1";      shift
CONSTRAINTS_FILE="$1"; shift
NETLIST_FILE="$1";     shift
OUTPUT_PATH="$1";      shift


# run vivado to convert to a bitfile for our FPGA
TMP="$(mktemp)"
colorize tee "$TMP" <<- EOT

	# read shit
	read_xdc $CONSTRAINTS_FILE
	${EDIF_MODE}read_edif $NETLIST_FILE
	${SV_MODE}read_verilog $TOP_MODULE.sv
	${SV_MODE}synth_design -top $TOP_MODULE -part $XILINX_PART

	# Select the FPGA to target and denote which module is the top module
	${EDIF_MODE}set_msg_config -id "Vivado 12-1411" -new_severity ERROR
	${EDIF_MODE}link_design -part $XILINX_PART -top $TOP_MODULE
	${EDIF_MODE}if [expr {[get_msg_config -severity Error -count] > 0}] { error "ERROR: Errors encountered! Abort!" }

	# This will detect errors early
	${SLOW}set_property SEVERITY {Error} [get_drc_checks NSTD-1]
	${SLOW}set_property SEVERITY {Error} [get_drc_checks UCIO-1]
	${SLOW}report_drc -return_string
	${SLOW}if [expr {[get_msg_config -severity Error -count] > 0}] { error "ERROR: Errors encountered! Abort!" }

	# Optimizer: deduce a more optimal design
	${SLOW}opt_design

	# Placer: Physical placement of cells from netlist, minimizing total wire length and routing congestions
	place_design

	# Router: Create physical wiring for placed design
	route_design

	# debug shit
	report_utilization; report_timing

	# dump results
	write_bitstream -force $OUTPUT_PATH

EOT

colorize $XILINX_TOP_DIR/bin/vivado -mode batch -source "$TMP"
RET=$?
rm "$TMP"
exit $RET
