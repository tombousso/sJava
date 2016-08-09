include Makefile.compiler

EXAMPLE_FILES=$(wildcard examples/*.sjava)
EXAMPLE_FILES_NAME=$(patsubst examples/%.sjava,%,$(EXAMPLE_FILES))
EXAMPLE_FILES_MAINCLASS=$(patsubst %,bin/%/Main.class,$(EXAMPLE_FILES_NAME))

EXAMPLE_FOLDERS=$(filter-out examples/,$(sort $(dir $(wildcard examples/*/))))
EXAMPLE_FOLDERS_NAME=$(patsubst examples/%/,%,$(EXAMPLE_FOLDERS))
EXAMPLE_FOLDERS_MAINCLASS=$(patsubst %,bin/%/Main.class,$(EXAMPLE_FOLDERS_NAME))

TARGETS=$(EXAMPLE_FILES_NAME) $(EXAMPLE_FOLDERS_NAME)
CLEAN_TARGETS=$(patsubst %,clean-%,$(TARGETS))

.PHONY: all clean $(TARGETS) $(CLEAN_TARGETS)

all: $(TARGETS)

$(STD): $(COMPILER) $(STD_FILES)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main $(STD_FILES) -d bin/

$(EXAMPLE_FILES_NAME): %: bin/%/Main.class

$(EXAMPLE_FILES_MAINCLASS): bin/%/Main.class: $(COMPILER) $(STD) examples/%.sjava
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main $(STD_MACROS) $(word 3,$^) -d bin/$*/

$(EXAMPLE_FOLDERS_NAME): %: bin/%/Main.class

.SECONDEXPANSION:
$(EXAMPLE_FOLDERS_MAINCLASS): bin/%/Main.class: $(COMPILER) $(STD) $$(call rwildcard,examples/%/,*.sjava)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main $(STD_MACROS) $(call rwildcard,examples/$*/,*.sjava) -d bin/$*/

clean:
	rm -rf bin

$(CLEAN_TARGETS): clean-%:
	rm -rf bin/$*