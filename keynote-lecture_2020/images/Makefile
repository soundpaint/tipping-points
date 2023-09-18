include ../common.mk
ROOT=..
BUILD_IMAGES=$(BUILD)/images
SRC_GIF=$(wildcard *.gif)
OBJ_EPS_FROM_GIF=$(patsubst %,$(BUILD_IMAGES)/%,$(SRC_GIF:.gif=.eps))
SRC_PNG=$(wildcard *.png)
OBJ_PNG_FROM_PNG=$(patsubst %,$(BUILD_IMAGES)/%,$(SRC_PNG:.png=.png))
SRC_JPG=$(wildcard *.jpg)
OBJ_JPG_FROM_JPG=$(patsubst %,$(BUILD_IMAGES)/%,$(SRC_JPG:.jpg=.jpg))
SRC_DIA=$(wildcard *.dia)
OBJ_SVG_FROM_DIA=$(patsubst %,$(BUILD_IMAGES)/%,$(SRC_DIA:.dia=.svg))
OBJ_EPS_FROM_SVG=$(patsubst %,$(BUILD_IMAGES)/%,$(SRC_DIA:.dia=.eps))
OBJ=\
	$(OBJ_EPS_FROM_GIF) $(OBJ_PNG_FROM_PNG) $(OBJ_JPG_FROM_JPG) \
	$(OBJ_SVG_FROM_DIA) $(OBJ_EPS_FROM_SVG)

all: $(BUILD_IMAGES) $(OBJ)

$(BUILD_IMAGES):
	mkdir -p $@

$(BUILD_IMAGES)/%.eps: %.gif
	convert -background Gray75 -flatten -alpha on $< $@

$(BUILD_IMAGES)/%.png: %.png
	 cp -fp $< $@

$(BUILD_IMAGES)/%.jpg: %.jpg
	 cp -fp $< $@

$(BUILD_IMAGES)/%.eps: $(BUILD_IMAGES)/%.svg
	inkscape -E $@ $<

$(BUILD_IMAGES)/%.svg: %.dia
	dia --export=$@ $<

objclean:
	rm -rf $(BUILD_IMAGES)

bkpclean:
	rm -f *~

clean: bkpclean objclean
