//
// Dual-Port RAM with Synchronous Read (Read Through)
// Copied from Xilinx XST User Guide page 182
//
module v_rams_11 #(
		  parameter DATA_WIDTH = 16,
		  parameter ADDR_WIDTH = 16
)
  (clk,we,a,dpra,di,spo,dpo);
   input  clk,we;
   input  [ADDR_WIDTH-1:0] a,dpra;
   input  [DATA_WIDTH-1:0] di;
   output [DATA_WIDTH-1:0] spo,dpo;
   reg    [DATA_WIDTH-1:0] ram [2**ADDR_WIDTH-1:0];
   reg    [ADDR_WIDTH-1:0] read_a,read_dpra;

   always @(posedge clk) begin
      if (we)
	ram[a] <= di;
      read_a <= a;
      read_dpra <= dpra;
   end

   assign spo = ram[read_a];
   assign dpo = ram[read_dpra];
endmodule
