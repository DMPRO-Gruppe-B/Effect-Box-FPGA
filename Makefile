#TOP_MODULE  := EffectBox
#TOP_MODULE  := BRAMTest
TOP_MODULE  := FPGATest
TOP_MODULE  := BRAMTest
TOP_MODULE  := SPITest

XILINX_PART := xc7a100tftg256-1# the chip on out pcb
CFGMEM_PART := s25fl128sxxxxxx0-spi-x1_x2_x4
CONSTRAINTS_FILE := constraints.xdc

BUILD_DIR   := build
SCALA_TARGETS    := $(shell find src/main/scala/             -type f -name '*.scala')
VERILOG_TARGETS  := $(shell find src/main/resources/verilog/ -type f -name '*.v')
VHDL_TARGETS     := $(shell find src/main/resources/vhdl/ -mindepth 1 -maxdepth 1 -type d)
VHDL_DESTS       := $(patsubst src/main/resources/vhdl/%,$(BUILD_DIR)/include/%.v,$(VHDL_TARGETS))

ifdef DEVKIT
	CFGMEM_PART := mt25ql128-spi-x1_x2_x4
	XILINX_PART := xc7a35ticsg324-1L# the arty 7 dev kit
	CONSTRAINTS_FILE := constraints-devkit.xdc
endif

FLAGS := ""
ifdef FAST
	FLAGS += "FAST"
endif
ifdef NO_EDIF
	FLAGS := "NO_EDIF"
	NETLIST_FORMAT := "v"
else
	NETLIST_FORMAT := "edif"
endif

TOP_MODULE_VERILOG_TARGET := $(BUILD_DIR)/$(TOP_MODULE).v
TOP_MODULE_FIRRTL_TARGET  := $(BUILD_DIR)/$(TOP_MODULE).fir
TOP_MODULE_NETLIST_TARGET := $(BUILD_DIR)/$(TOP_MODULE).$(NETLIST_FORMAT)
TOP_MODULE_BITFILE_TARGET := $(BUILD_DIR)/$(TOP_MODULE).bit


.PHONY: all
all: bitfile

.PHONY: bitfile
bitfile: $(VHDL_DESTS) $(TOP_MODULE_BITFILE_TARGET)

.PHONY: upload
upload:
	cd $(BUILD_DIR); ../scripts/upload_bitfile.sh ../$(TOP_MODULE_BITFILE_TARGET)

.PHONY: flash
flash:
	cd $(BUILD_DIR); ../scripts/flash_bitfile.sh ../$(TOP_MODULE_BITFILE_TARGET) $(CFGMEM_PART)

.PHONY: graphs graphs_yosys graphs_diagrammer
graphs: graphs_yosys graphs_diagrammer
graphs_yosys: $(TOP_MODULE_VERILOG_TARGET) $(VERILOG_TARGETS) $(VHDL_DESTS)
	rm -v graphs/yosys/* || true
	cd graphs; ../scripts/make_yosys.sh $(patsubst %,../%,$^)
graphs_diagrammer: $(TOP_MODULE_FIRRTL_TARGET) diagrammer/diagram.sh
	rm graphs/diagrammer/* || true
	cd diagrammer; ./diagram.sh -i ../$(TOP_MODULE_FIRRTL_TARGET) -t ../graphs/diagrammer -o '""'
diagrammer/diagram.sh:
	git submodule update --init

$(BUILD_DIR):
	mkdir $(BUILD_DIR)

# todo: this won't re-make on edits for vhdl files
$(BUILD_DIR)/include/%.v: $(BUILD_DIR) src/main/resources/vhdl/%
	cd $(BUILD_DIR); mkdir -p include
	cd $(BUILD_DIR); ../scripts/synth_vhdl.sh ../$@ $* ../$</*.vhd


$(TOP_MODULE_FIRRTL_TARGET): $(SCALA_TARGETS)
	sbt "run $(TOP_MODULE) $(TOP_MODULE_FIRRTL_TARGET)"

#%.v: %.fir
$(TOP_MODULE_VERILOG_TARGET): $(TOP_MODULE_FIRRTL_TARGET)
	firrtl -i $< -o $@ --info-mode=ignore -ll Info

$(TOP_MODULE_NETLIST_TARGET): $(BUILD_DIR) $(TOP_MODULE_VERILOG_TARGET) $(VERILOG_TARGETS) $(VHDL_DESTS)
	cd $(BUILD_DIR); \
	../scripts/synth_netlist.sh $(FLAGS) $(TOP_MODULE) ../$(TOP_MODULE_NETLIST_TARGET) $(patsubst %,../%,$^)

$(TOP_MODULE_BITFILE_TARGET): $(BUILD_DIR) $(CONSTRAINTS_FILE) $(TOP_MODULE_NETLIST_TARGET)
	cd $(BUILD_DIR); \
	../scripts/synth_bitfile.sh $(FLAGS) $(TOP_MODULE) $(XILINX_PART) ../$(CONSTRAINTS_FILE) ../$(TOP_MODULE_NETLIST_TARGET) ../$@

.PHONY: clean
clean:
	rm -vf $(TOP_MODULE_VERILOG_TARGET) || true
	rm -vf $(TOP_MODULE_FIRRTL_TARGET)  || true
	rm -vf $(TOP_MODULE_NETLIST_TARGET) || true
	rm -vf $(TOP_MODULE_BITFILE_TARGET) || true
	rm -v -rf $(BUILD_DIR)

.PHONY: clean_all
clean_all: clean
	rm -v -rf $(VHDL_DESTS)
