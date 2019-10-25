## Arty-7 Constraints, with chisel 3.1 naming scheme
## For The Effect Box IV
## XC7A100TFTG256-1

set_property CONFIG_VOLTAGE 3.3 [current_design]
set_property CFGBVS VCCO [current_design]

## Clock

set_property -dict {PACKAGE_PIN E3  IOSTANDARD LVCMOS33} [get_ports { clock }];
create_clock -add -name sys_clk_pin -period 62.5 \
    -waveform {0 5} [get_ports { clock }];


## Reset (use the one that works for you)

#set_property -dict {PACKAGE_PIN C2  IOSTANDARD LVCMOS33} [get_ports { reset }];

## Pinout

# Test pin
#set_property -dict {PACKAGE_PIN P14 IOSTANDARD LVCMOS33} [get_ports { io_test }];

# Test pins
set_property -dict {PACKAGE_PIN P14  IOSTANDARD LVCMOS33} [get_ports { io_pinout[0] }];
set_property -dict {PACKAGE_PIN N11  IOSTANDARD LVCMOS33} [get_ports { io_pinout[1] }];
set_property -dict {PACKAGE_PIN N12  IOSTANDARD LVCMOS33} [get_ports { io_pinout[2] }];
set_property -dict {PACKAGE_PIN P10  IOSTANDARD LVCMOS33} [get_ports { io_pinout[3] }];
set_property -dict {PACKAGE_PIN P11  IOSTANDARD LVCMOS33} [get_ports { io_pinout[4] }];
set_property -dict {PACKAGE_PIN R12  IOSTANDARD LVCMOS33} [get_ports { io_pinout[5] }];
set_property -dict {PACKAGE_PIN T12  IOSTANDARD LVCMOS33} [get_ports { io_pinout[6] }];
set_property -dict {PACKAGE_PIN R13  IOSTANDARD LVCMOS33} [get_ports { io_pinout[7] }];
set_property -dict {PACKAGE_PIN T13  IOSTANDARD LVCMOS33} [get_ports { io_pinout[8] }];
set_property -dict {PACKAGE_PIN R10  IOSTANDARD LVCMOS33} [get_ports { io_pinout[9] }];
set_property -dict {PACKAGE_PIN R11  IOSTANDARD LVCMOS33} [get_ports { io_pinout[10] }];
set_property -dict {PACKAGE_PIN N9   IOSTANDARD LVCMOS33} [get_ports { io_pinout[11] }];
set_property -dict {PACKAGE_PIN P9   IOSTANDARD LVCMOS33} [get_ports { io_pinout[12] }];
set_property -dict {PACKAGE_PIN M6   IOSTANDARD LVCMOS33} [get_ports { io_pinout[13] }];
set_property -dict {PACKAGE_PIN N6   IOSTANDARD LVCMOS33} [get_ports { io_pinout[14] }];
set_property -dict {PACKAGE_PIN P8   IOSTANDARD LVCMOS33} [get_ports { io_pinout[15] }];
