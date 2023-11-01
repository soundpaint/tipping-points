include ../common.mk
ROOT=..
BUILD_SCHEMATICS=$(BUILD)/schematics
SRC_PDF=$(wildcard tp-sim/*.pdf)
OBJ_PDF=$(patsubst tp-sim/%,$(BUILD_SCHEMATICS)/%,$(SRC_PDF:.pdf=_cropped.pdf))
OBJ_EPS=$(BUILD_SCHEMATICS)/OBJ_EPS
OBJ=$(OBJ_EPS)

all: $(BUILD_SCHEMATICS) $(OBJ)
	@echo OBJ=$(OBJ)

$(BUILD_SCHEMATICS):
	mkdir -p $@

$(OBJ_EPS): $(OBJ_PDF)
	pdftops -eps -f 1 -l 1 $< $(BUILD_SCHEMATICS)/voltage-supply.eps
	pdftops -eps -f 2 -l 2 $< $(BUILD_SCHEMATICS)/virtual-ground.eps
	pdftops -eps -f 3 -l 3 $< $(BUILD_SCHEMATICS)/oscillator.eps
	pdftops -eps -f 4 -l 4 $< $(BUILD_SCHEMATICS)/integrator.eps
	pdftops -eps -f 5 -l 5 $< $(BUILD_SCHEMATICS)/osc-amplitude-ctrl.eps
	pdftops -eps -f 6 -l 6 $< $(BUILD_SCHEMATICS)/osc-level-shift-ctrl.eps
	pdftops -eps -f 7 -l 7 $< $(BUILD_SCHEMATICS)/schmitt-trigger.eps
	pdftops -eps -f 8 -l 8 $< $(BUILD_SCHEMATICS)/audio-out.eps
	touch $@

$(OBJ_PDF): $(SRC_PDF)
	pdfcrop $< $@

objclean:
	rm -rf $(BUILD_SCHEMATICS)

bkpclean:
	rm -f *~

clean: bkpclean objclean
