## Arty-7 Constraints, with chisel 3.1 naming scheme
## For The Effect Box IV
## XC7A100TFTG256-1

set_property CONFIG_VOLTAGE 3.3 [current_design]
set_property CFGBVS VCCO [current_design]

## Clock

set_property -dict {PACKAGE_PIN N14  IOSTANDARD LVCMOS33} [get_ports { clock }];
create_clock -add -name sys_clk_pin -period 62.5 \
    -waveform {0 5} [get_ports { clock }];

set_property -dict {PACKAGE_PIN L15  IOSTANDARD LVCMOS33} [get_ports { io_sysClock }];
create_clock -add -name dac_sys_clk_pin -period 122.1 \
    -waveform {0 5} [get_ports { io_sysClock }];

set_property -dict {PACKAGE_PIN L14  IOSTANDARD LVCMOS33} [get_ports { io_bitClock }];
create_generated_clock -master_clock sys_clk_pin -add -name dac_bit_clk_pin -divide_by 4 \
    -source [get_ports { io_sysClock }] [get_ports { io_bitClock }];

set_property -dict {PACKAGE_PIN M15  IOSTANDARD LVCMOS33} [get_ports { io_sampleClock }];
create_generated_clock -master_clock sys_clk_pin -add -name dac_sample_clk_pin -divide_by 32 \
    -source [get_ports { io_bitClock }] [get_ports { io_sampleClock }];

## Reset (use the one that works for you)

# pinout15
#set_property -dict {PACKAGE_PIN P8  IOSTANDARD LVCMOS33} [get_ports { reset }];

# GPIO_0 from MCU
set_property -dict {PACKAGE_PIN P15  IOSTANDARD LVCMOS33} [get_ports { reset }];
set_property DRIVE 8 [get_ports { reset }];


## DAC/ADC

set_property -dict {PACKAGE_PIN M14 IOSTANDARD LVCMOS33} [get_ports { io_adcIn }];

set_property -dict {PACKAGE_PIN K13 IOSTANDARD LVCMOS33} [get_ports { io_dacOut }];


# Test pins
set_property -dict {PACKAGE_PIN P14  IOSTANDARD LVCMOS33} [get_ports { io_pinout0 }];
set_property -dict {PACKAGE_PIN N11  IOSTANDARD LVCMOS33} [get_ports { io_pinout1 }];
set_property -dict {PACKAGE_PIN N12  IOSTANDARD LVCMOS33} [get_ports { io_pinout2 }];
set_property -dict {PACKAGE_PIN P10  IOSTANDARD LVCMOS33} [get_ports { io_pinout3 }];
set_property -dict {PACKAGE_PIN P11  IOSTANDARD LVCMOS33} [get_ports { io_pinout4 }];
set_property -dict {PACKAGE_PIN R12  IOSTANDARD LVCMOS33} [get_ports { io_pinout5 }];
set_property -dict {PACKAGE_PIN T12  IOSTANDARD LVCMOS33} [get_ports { io_pinout6 }];
set_property -dict {PACKAGE_PIN R13  IOSTANDARD LVCMOS33} [get_ports { io_pinout7 }];
set_property -dict {PACKAGE_PIN T13  IOSTANDARD LVCMOS33} [get_ports { io_pinout8 }];
set_property -dict {PACKAGE_PIN R10  IOSTANDARD LVCMOS33} [get_ports { io_pinout9 }];
set_property -dict {PACKAGE_PIN R11  IOSTANDARD LVCMOS33} [get_ports { io_pinout10 }];
set_property -dict {PACKAGE_PIN N9   IOSTANDARD LVCMOS33} [get_ports { io_pinout11 }];
set_property -dict {PACKAGE_PIN P9   IOSTANDARD LVCMOS33} [get_ports { io_pinout12 }];
set_property -dict {PACKAGE_PIN M6   IOSTANDARD LVCMOS33} [get_ports { io_pinout13 }];
set_property -dict {PACKAGE_PIN N6   IOSTANDARD LVCMOS33} [get_ports { io_pinout14 }];
set_property -dict {PACKAGE_PIN P8   IOSTANDARD LVCMOS33} [get_ports { io_pinout15 }];

# MCU spi
set_property -dict {PACKAGE_PIN T14  IOSTANDARD LVCMOS33} [get_ports { io_spi_clk }];
set_property -dict {PACKAGE_PIN R15  IOSTANDARD LVCMOS33} [get_ports { io_spi_miso }];
set_property -dict {PACKAGE_PIN R16  IOSTANDARD LVCMOS33} [get_ports { io_spi_mosi }];
set_property -dict {PACKAGE_PIN P16  IOSTANDARD LVCMOS33} [get_ports { io_spi_cs_n }];
