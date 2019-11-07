## Arty-7 Constraints, with chisel 3.1 naming scheme
## For dev kit
## xc7a35ticsg324-1L

set_property CONFIG_VOLTAGE 3.3 [current_design]
set_property CFGBVS VCCO [current_design]

## Clock

# Main clock
set_property -dict {PACKAGE_PIN E3  IOSTANDARD LVCMOS33} [get_ports { clock }];
create_clock -add -name sys_clk_pin -period 10.0 \
    -waveform {0 5} [get_ports { clock }];

## Derived clocks

## Sysclock IO41
#set_property -dict {PACKAGE_PIN N17  IOSTANDARD LVCMOS33} [get_ports { io_sysClk }];
#create_clock -add -name dac_sys_clk_pin -period 122.1 \
#    -waveform {0 5} [get_ports { io_sysClk }];
#
## BitClock IO40
#set_property -dict {PACKAGE_PIN P18  IOSTANDARD LVCMOS33} [get_ports { io_bitClk }];
##create_clock -add -name dac_bit_clk_pin -period 976.56 \
##    -waveform {0 5} [get_ports { io_bitClk }];
#create_generated_clock -add -name dac_bit_clk_pin -divide_by 8 \
#    -source [get_ports {io_sysClk}] [get_ports { io_bitClk }];
#
#
## Test output IO39
#set_property -dict {PACKAGE_PIN R18  IOSTANDARD LVCMOS33} [get_ports { io_testOut }];



## Reset (use the one that works for you)

set_property -dict {PACKAGE_PIN C2  IOSTANDARD LVCMOS33} [get_ports { reset }];


## Buttons

set_property -dict {PACKAGE_PIN D9  IOSTANDARD LVCMOS33} [get_ports { io_btn[0] }];       #IO_L6N_T0_VREF_16 Sch=btn[0]
set_property -dict {PACKAGE_PIN C9  IOSTANDARD LVCMOS33} [get_ports { io_btn[1] }];       #IO_L11P_T1_SRCC_16 Sch=btn[1]
set_property -dict {PACKAGE_PIN B9  IOSTANDARD LVCMOS33} [get_ports { io_btn[2] }];       #IO_L11N_T1_SRCC_16 Sch=btn[2]
set_property -dict {PACKAGE_PIN B8  IOSTANDARD LVCMOS33} [get_ports { io_btn[3] }];       #IO_L12P_T1_MRCC_16 Sch=btn[3]
#set_property -dict {PACKAGE_PIN B8  IOSTANDARD LVCMOS33} [get_ports { reset }];


## Switches

set_property -dict {PACKAGE_PIN A8  IOSTANDARD LVCMOS33} [get_ports { io_sw[0] }];        #IO_L12N_T1_MRCC_16 Sch=sw[0]
set_property -dict {PACKAGE_PIN C11 IOSTANDARD LVCMOS33} [get_ports { io_sw[1] }];        #IO_L13P_T2_MRCC_16 Sch=sw[1]
set_property -dict {PACKAGE_PIN C10 IOSTANDARD LVCMOS33} [get_ports { io_sw[2] }];        #IO_L13N_T2_MRCC_16 Sch=sw[2]
set_property -dict {PACKAGE_PIN A10 IOSTANDARD LVCMOS33} [get_ports { io_sw[3] }];        #IO_L14P_T2_SRCC_16 Sch=sw[3]


## LEDs

set_property -dict {PACKAGE_PIN H5  IOSTANDARD LVCMOS33} [get_ports { io_led[0] }];       #IO_L24N_T3_35 Sch=led[4]
set_property -dict {PACKAGE_PIN J5  IOSTANDARD LVCMOS33} [get_ports { io_led[1] }];       #IO_25_35 Sch=led[5]
set_property -dict {PACKAGE_PIN T9  IOSTANDARD LVCMOS33} [get_ports { io_led[2] }];       #IO_L24P_T3_A01_D17_14 Sch=led[6]
set_property -dict {PACKAGE_PIN T10 IOSTANDARD LVCMOS33} [get_ports { io_led[3] }];       #IO_L24N_T3_A00_D16_14 Sch=led[7]


## Tricolor LEDs

set_property -dict {PACKAGE_PIN G6  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_0[0] }];  #IO_L19P_T3_35 Sch=led0_r
set_property -dict {PACKAGE_PIN F6  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_0[1] }];  #IO_L19N_T3_VREF_35 Sch=led0_g
set_property -dict {PACKAGE_PIN E1  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_0[2] }];  #IO_L18N_T2_35 Sch=led0_b
set_property -dict {PACKAGE_PIN G3  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_1[0] }];  #IO_L20N_T3_35 Sch=led1_r
set_property -dict {PACKAGE_PIN J4  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_1[1] }];  #IO_L21P_T3_DQS_35 Sch=led1_g
set_property -dict {PACKAGE_PIN G4  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_1[2] }];  #IO_L20P_T3_35 Sch=led1_b
set_property -dict {PACKAGE_PIN J3  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_2[0] }];  #IO_L22P_T3_35 Sch=led2_r
set_property -dict {PACKAGE_PIN J2  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_2[1] }];  #IO_L22N_T3_35 Sch=led2_g
set_property -dict {PACKAGE_PIN H4  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_2[2] }];  #IO_L21N_T3_DQS_35 Sch=led2_b
set_property -dict {PACKAGE_PIN K1  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_3[0] }];  #IO_L23N_T3_35 Sch=led3_r
set_property -dict {PACKAGE_PIN H6  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_3[1] }];  #IO_L24P_T3_35 Sch=led3_g
set_property -dict {PACKAGE_PIN K2  IOSTANDARD LVCMOS33} [get_ports { io_rgbled_3[2] }];  #IO_L23P_T3_35 Sch=led3_b


##Pmod Header JB

set_property -dict {PACKAGE_PIN E15 IOSTANDARD LVCMOS33} [get_ports { io_spi_mosi }];      #IO_L11P_T1_SRCC_15 Sch=jb_p[1]
set_property -dict {PACKAGE_PIN E16 IOSTANDARD LVCMOS33} [get_ports { io_spi_miso }];      #IO_L11N_T1_SRCC_15 Sch=jb_n[1]
set_property -dict {PACKAGE_PIN D15 IOSTANDARD LVCMOS33} [get_ports { io_spi_clk  }];      #IO_L12P_T1_MRCC_15 Sch=jb_p[2]
set_property -dict {PACKAGE_PIN C15 IOSTANDARD LVCMOS33} [get_ports { io_spi_cs_n }];      #IO_L12N_T1_MRCC_15 Sch=jb_n[2]
