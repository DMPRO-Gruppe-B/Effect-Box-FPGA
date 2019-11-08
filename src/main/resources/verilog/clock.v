// PLLE2_BASE: Base Phase Locked Loop (PLL)
// 7 Series
// Xilinx HDL Libraries Guide, version 2012.2

module PLLE2 #(
	       parameter BANDWIDTH = "OPTIMIZED",
	       parameter CLK_PERIOD = 0.0,
	       parameter BASE_MULT = 2,
	       parameter BASE_DIVIDE = 1,
	       parameter CLK0_DIVIDE = 1,
	       parameter CLK1_DIVIDE = 1,
	       parameter CLK2_DIVIDE = 1,
	       parameter CLK3_DIVIDE = 1,
	       parameter CLK4_DIVIDE = 1,
	       parameter CLK5_DIVIDE = 1,
	       parameter CLK0_DUTY_CYCLE = 0.5,
	       parameter CLK1_DUTY_CYCLE = 0.5,
	       parameter CLK2_DUTY_CYCLE = 0.5,
	       parameter CLK3_DUTY_CYCLE = 0.5,
	       parameter CLK4_DUTY_CYCLE = 0.5,
	       parameter CLK5_DUTY_CYCLE = 0.5,
	       parameter CLK0_PHASE = 0.0,
	       parameter CLK1_PHASE = 0.0,
	       parameter CLK2_PHASE = 0.0,
	       parameter CLK3_PHASE = 0.0,
	       parameter CLK4_PHASE = 0.0,
	       parameter CLK5_PHASE = 0.0,
	       )
   (clkin, reset, pwrdwn, clkout0, clkout1, clkout2, clkout3, clkout4, clkout5, clkfbout, clkfbin, locked);
   input clkin, reset, pwrdwn, clkfbin;
   output clkout0, clkout1, clkout2, clkout3, clkout4, clkout5, clkfbout, locked;

   PLLE2_BASE #(
		.BANDWIDTH(BANDWIDTH),      // OPTIMIZED, HIGH, LOW
		.CLKFBOUT_MULT(BASE_MULT),  // Multiply value for all CLKOUT, (2-64)
		.CLKFBOUT_PHASE(0.0),       // Phase offset in degrees of CLKFB, (-360.000-360.000).
		.CLKIN1_PERIOD(62.5), // Input clock period in ns to ps resolution (i.e. 33.333 is 30 MHz).
		// CLKOUT0_DIVIDE - CLKOUT5_DIVIDE: Divide amount for each CLKOUT (1-128)
		.CLKOUT0_DIVIDE(CLK0_DIVIDE),
		.CLKOUT1_DIVIDE(CLK1_DIVIDE),
		.CLKOUT2_DIVIDE(CLK2_DIVIDE),
		.CLKOUT3_DIVIDE(CLK3_DIVIDE),
		.CLKOUT4_DIVIDE(CLK4_DIVIDE),
		.CLKOUT5_DIVIDE(CLK5_DIVIDE),
		// CLKOUT0_DUTY_CYCLE - CLKOUT5_DUTY_CYCLE: Duty cycle for each CLKOUT (0.001-0.999).
		.CLKOUT0_DUTY_CYCLE(CLK0_DUTY_CYCLE),
		.CLKOUT1_DUTY_CYCLE(CLK1_DUTY_CYCLE),
		.CLKOUT2_DUTY_CYCLE(CLK2_DUTY_CYCLE),
		.CLKOUT3_DUTY_CYCLE(CLK3_DUTY_CYCLE),
		.CLKOUT4_DUTY_CYCLE(CLK4_DUTY_CYCLE),
		.CLKOUT5_DUTY_CYCLE(CLK5_DUTY_CYCLE),
		// CLKOUT0_PHASE - CLKOUT5_PHASE: Phase offset for each CLKOUT (-360.000-360.000).
		.CLKOUT0_PHASE(CLK0_PHASE),
		.CLKOUT1_PHASE(CLK1_PHASE),
		.CLKOUT2_PHASE(CLK2_PHASE),
		.CLKOUT3_PHASE(CLK3_PHASE),
		.CLKOUT4_PHASE(CLK4_PHASE),
		.CLKOUT5_PHASE(CLK5_PHASE),
		.DIVCLK_DIVIDE(BASE_DIVIDE),  // Master division value, (1-56)
		.REF_JITTER1(0.0),            // Reference input jitter in UI, (0.000-0.999).
		.STARTUP_WAIT("FALSE")        // Delay DONE until PLL Locks, ("TRUE"/"FALSE")
		)
   PLLE2_BASE_inst (
		    // Clock Outputs: 1-bit (each) output: User configurable clock outputs
		    .CLKOUT0(clkout0),
		    .CLKOUT1(clkout1),
		    .CLKOUT2(clkout2),
		    .CLKOUT3(clkout3),
		    .CLKOUT4(clkout4),
		    .CLKOUT5(clkout5),
		    // Feedback Clocks: 1-bit (each) output: Clock feedback ports
		    .CLKFBOUT(clkfbout), // 1-bit output: Feedback clock
		    // Status Port: 1-bit (each) output: PLL status ports
		    .LOCKED(locked),     // 1-bit output: LOCK
		    // Clock Input: 1-bit (each) input: Clock input
		    .CLKIN1(clkin),     // 1-bit input: Input clock
		    // Control Ports: 1-bit (each) input: PLL control ports
		    .PWRDWN(pwrdwn),     // 1-bit input: Power-down
		    .RST(reset),           // 1-bit input: Reset
		    // Feedback Clocks: 1-bit (each) input: Clock feedback ports
		    .CLKFBIN(clkfbin)    // 1-bit input: Feedback clock
		    ); // End of PLLE2_BASE_inst instantiation
endmodule


/*
module MMCME2 #(
		)
   ();

   //
   // MMCME2_BASE: Base Mixed Mode Clock Manager
   // 7 Series
   // Xilinx HDL Libraries Guide, version 2012.2
   MMCME2_BASE #(
		 .BANDWIDTH("OPTIMIZED"), // Jitter programming (OPTIMIZED, HIGH, LOW)
		 .CLKFBOUT_MULT_F(5.0),   // Multiply value for all CLKOUT (2.000-64.000).
		 .CLKFBOUT_PHASE(0.0),    // Phase offset in degrees of CLKFB (-360.000-360.000).
		 .CLKIN1_PERIOD(0.0),     // Input clock period in ns to ps resolution (i.e. 33.333 is 30 MHz).
		 // CLKOUT0_DIVIDE - CLKOUT6_DIVIDE: Divide amount for each CLKOUT (1-128)
		 .CLKOUT1_DIVIDE(1),
		 .CLKOUT2_DIVIDE(1),
		 .CLKOUT3_DIVIDE(1),
		 .CLKOUT4_DIVIDE(1),
		 .CLKOUT5_DIVIDE(1),
		 .CLKOUT6_DIVIDE(1),
		 .CLKOUT0_DIVIDE_F(1.0),  // Divide amount for CLKOUT0 (1.000-128.000).
		 // CLKOUT0_DUTY_CYCLE - CLKOUT6_DUTY_CYCLE: Duty cycle for each CLKOUT (0.01-0.99).
		 .CLKOUT0_DUTY_CYCLE(0.5),
		 .CLKOUT1_DUTY_CYCLE(0.5),
		 .CLKOUT2_DUTY_CYCLE(0.5),
		 .CLKOUT3_DUTY_CYCLE(0.5),
		 .CLKOUT4_DUTY_CYCLE(0.5),
		 .CLKOUT5_DUTY_CYCLE(0.5),
		 .CLKOUT6_DUTY_CYCLE(0.5),
		 // CLKOUT0_PHASE - CLKOUT6_PHASE: Phase offset for each CLKOUT (-360.000-360.000).
		 .CLKOUT0_PHASE(0.0),
		 .CLKOUT1_PHASE(0.0),
		 .CLKOUT2_PHASE(0.0),
		 .CLKOUT3_PHASE(0.0),
		 .CLKOUT4_PHASE(0.0),
		 .CLKOUT5_PHASE(0.0),
		 .CLKOUT6_PHASE(0.0),
		 .CLKOUT4_CASCADE("FALSE"), // Cascade CLKOUT4 counter with CLKOUT6 (FALSE, TRUE)
		 .DIVCLK_DIVIDE(1),         // Master division value (1-106)
		 .REF_JITTER1(0.0),         // Reference input jitter in UI (0.000-0.999).
		 .STARTUP_WAIT("FALSE")     // Delays DONE until MMCM is locked (FALSE, TRUE)
		 )
   MMCME2_BASE_inst (
		     // Clock Outputs: 1-bit (each) output: User configurable clock outputs
		     .CLKOUT0(CLKOUT0),     // 1-bit output: CLKOUT0
		     .CLKOUT0B(CLKOUT0B),   // 1-bit output: Inverted CLKOUT0
		     .CLKOUT1(CLKOUT1),     // 1-bit output: CLKOUT1
		     .CLKOUT1B(CLKOUT1B),   // 1-bit output: Inverted CLKOUT1
		     .CLKOUT2(CLKOUT2),     // 1-bit output: CLKOUT2
		     .CLKOUT2B(CLKOUT2B),   // 1-bit output: Inverted CLKOUT2
		     .CLKOUT3(CLKOUT3),     // 1-bit output: CLKOUT3
		     .CLKOUT3B(CLKOUT3B),   // 1-bit output: Inverted CLKOUT3
		     .CLKOUT4(CLKOUT4),     // 1-bit output: CLKOUT4
		     .CLKOUT5(CLKOUT5),     // 1-bit output: CLKOUT5
		     .CLKOUT6(CLKOUT6),     // 1-bit output: CLKOUT6
		     // Feedback Clocks: 1-bit (each) output: Clock feedback ports
		     .CLKFBOUT(CLKFBOUT),   // 1-bit output: Feedback clock
		     .CLKFBOUTB(CLKFBOUTB), // 1-bit output: Inverted CLKFBOUT
		     // Status Ports: 1-bit (each) output: MMCM status ports
		     .LOCKED(LOCKED),       // 1-bit output: LOCK
		     // Clock Inputs: 1-bit (each) input: Clock input
		     .CLKIN1(CLKIN1),       // 1-bit input: Clock
		     // Control Ports: 1-bit (each) input: MMCM control ports
		     .PWRDWN(PWRDWN),       // 1-bit input: Power-down
		     .RST(RST),             // 1-bit input: Reset
		     // Feedback Clocks: 1-bit (each) input: Clock feedback ports
		     .CLKFBIN(CLKFBIN)      // 1-bit input: Feedback clock
		     );
endmodule
*/
