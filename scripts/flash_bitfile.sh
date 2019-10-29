#!/bin/bash
source $(dirname $0)/common.sh

BITFILE="$1"; shift
MCSFILE="$BITFILE.mcs"
CFGMEM="$1"; shift

if ! test -f "$BITFILE"; then
	echo "ERROR: No bitfile to upload!"
	exit 1
fi

# Flash the bitfile to the FPGA SPI Flash

TMP="$(mktemp)"
colorize tee "$TMP" <<- EOT

	# start the hardware server
	open_hw

	# Connect to the Digilent Cable on localhost:3121
	connect_hw_server -url localhost:3121
	current_hw_target [get_hw_targets *]
	open_hw_target

	# Program and Refresh the Device
	current_hw_device [lindex [get_hw_devices] 0]
	refresh_hw_device -update_hw_probes false [lindex [get_hw_devices] 0]

	write_cfgmem -force -format MCS -interface spix4 -size 128 -loadbit "up 0x0 $BITFILE" -file $MCSFILE

	# Set up the flash chip
	create_hw_cfgmem -hw_device [current_hw_device] $CFGMEM
	set_property PROGRAM.FILES [list "$MCSFILE"] [current_hw_cfgmem]
	set_property PROGRAM.ADDRESS_RANGE  {entire_device} [current_hw_cfgmem]
	set_property PROGRAM.BLANK_CHECK  0 [current_hw_cfgmem]
	set_property PROGRAM.ERASE  1 [current_hw_cfgmem]
	set_property PROGRAM.CFG_PROGRAM  1 [current_hw_cfgmem]
	set_property PROGRAM.VERIFY  0 [current_hw_cfgmem]
	startgroup
	create_hw_bitstream -hw_device [lindex [get_hw_devices] 0] [get_property PROGRAM.HW_CFGMEM_BITFILE [ lindex [get_hw_devices] 0]]; program_hw_devices [lindex [get_hw_devices] 0]; refresh_hw_device [lindex [get_hw_devices] 0];
	program_hw_cfgmem [current_hw_cfgmem]
	endgroup

	boot_hw_device [current_hw_device]

EOT

colorize $XILINX_TOP_DIR/bin/vivado -mode batch -source "$TMP"
RET=$?
rm "$TMP"
exit $RET
