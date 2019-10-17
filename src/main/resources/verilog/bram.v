//
// Dual-Port Block RAM with Two Write Ports
// Copied from Xilinx XST User Guide page 198-202
//
module v_rams_16 #(
		  parameter DATA_WIDTH = 16,
		  parameter ADDR_WIDTH = 6
)
  (clka,clkb,ena,enb,wea,web,addra,addrb,dia,dib,doa,dob);
   input  clka,clkb,ena,enb,wea,web;
   input  [ADDR_WIDTH-1:0] addra,addrb;
   input  [DATA_WIDTH-1:0] dia,dib;
   output [DATA_WIDTH-1:0] doa,dob;
   reg    [DATA_WIDTH-1:0] ram [63:0];
   reg    [DATA_WIDTH-1:0] doa,dob;

   always @(posedge clka) begin
      if (ena)
	begin
	   if (wea)
	     ram[addra] <= dia;
	   doa <= ram[addra];
	end
   end
   always @(posedge clkb) begin
      if (enb)
	begin
	   if (web)
	     ram[addrb] <= dib;
	   dob <= ram[addrb];
	end
   end
endmodule
