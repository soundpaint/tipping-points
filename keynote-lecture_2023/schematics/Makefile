include ../common.mk
ROOT=..
BUILD_SCHEMATICS=$(BUILD)/schematics
SRC_PDF=$(wildcard tp-sim/*.pdf)
OBJ_EPS_FROM_PDF=$(patsubst tp-sim/%,$(BUILD_SCHEMATICS)/%,$(SRC_PDF:.pdf=.eps))
OBJ=$(OBJ_EPS_FROM_PDF)

all: $(BUILD_SCHEMATICS) $(OBJ)
	@echo OBJ=$(OBJ)

$(BUILD_SCHEMATICS):
	mkdir -p $@

$(BUILD_SCHEMATICS)/%.eps: $(BUILD_SCHEMATICS)/%.pdf
	pdftops -eps $< $@

$(BUILD_SCHEMATICS)/%.pdf: tp-sim/%.pdf
	pdfcrop $< $@

objclean:
	rm -rf $(BUILD_SCHEMATICS)

bkpclean:
	rm -f *~

clean: bkpclean objclean
