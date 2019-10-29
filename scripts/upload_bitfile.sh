#!/usr/bin/env bash

source $(dirname $0)/common.sh

BITFILE="$1"; shift

if ! test -f "$BITFILE"; then
	echo "ERROR: No bitfile to upload!"
	exit 1
fi

# upload the bitfile to the FPGA

TMP="$(mktemp)"
colorize tee "$TMP" <<- EOT

	# start the hardware server
	open_hw

	# Connect to the Digilent Cable on localhost:3121
	connect_hw_server -url localhost:3121
	current_hw_target [get_hw_targets *]
	open_hw_target

	# Program and Refresh the xc7a35t_0 Device
	current_hw_device [lindex [get_hw_devices] 0]
	refresh_hw_device -update_hw_probes false [lindex [get_hw_devices] 0]
	set_property PROGRAM.FILE {$BITFILE} [lindex [get_hw_devices] 0]
	# set_property PROBES.FILE {$BITFILE.ltx} [lindex [get_hw_devices] 0]

	program_hw_devices [lindex [get_hw_devices] 0]
	refresh_hw_device [lindex [get_hw_devices] 0]

EOT

colorize $XILINX_TOP_DIR/bin/vivado -mode batch -source "$TMP"
RET=$?
rm "$TMP"
exit $RET
