include Makefile.compiler

EXAMPLE_FILES=$(wildcard examples/*.sjava)
EXAMPLE_FILES_NAME=$(patsubst examples/%.sjava,%,$(EXAMPLE_FILES))
EXAMPLE_FILES_BUILD_NAME=$(patsubst %,build-%,$(EXAMPLE_FILES_NAME))
EXAMPLE_FILES_RUN_NAME=$(patsubst %,run-%,$(EXAMPLE_FILES_NAME))
EXAMPLE_FILES_MAINCLASS=$(patsubst %,bin/sjava/examples/%/Main.class,$(EXAMPLE_FILES_NAME))

EXAMPLE_FOLDERS=$(filter-out examples/,$(sort $(dir $(wildcard examples/*/))))
EXAMPLE_FOLDERS_NAME=$(patsubst examples/%/,%,$(EXAMPLE_FOLDERS))
EXAMPLE_FOLDERS_BUILD_NAME=$(patsubst %,build-%,$(EXAMPLE_FOLDERS_NAME))
EXAMPLE_FOLDERS_RUN_NAME=$(patsubst %,run-%,$(EXAMPLE_FOLDERS_NAME))
EXAMPLE_FOLDERS_MAINCLASS=$(patsubst %,bin/sjava/examples/%/Main.class,$(EXAMPLE_FOLDERS_NAME))

BUILDS=$(EXAMPLE_FILES_BUILD_NAME) $(EXAMPLE_FOLDERS_BUILD_NAME)
RUNS=$(EXAMPLE_FILES_RUN_NAME) $(EXAMPLE_FOLDERS_RUN_NAME)

.PHONY: all clean tester $(BUILDS) $(RUNS)

all: $(BUILDS)

$(EXAMPLE_FILES_BUILD_NAME): build-%: bin/sjava/examples/%/Main.class

$(EXAMPLE_FILES_MAINCLASS): bin/sjava/examples/%/Main.class: $(COMPILER) examples/%.sjava
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main build $(word 2,$^) -d bin/

$(EXAMPLE_FILES_RUN_NAME): run-%: build-%
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.examples.$*.Main

$(EXAMPLE_FOLDERS_BUILD_NAME): build-%: bin/sjava/examples/%/Main.class

.SECONDEXPANSION:
$(EXAMPLE_FOLDERS_MAINCLASS): bin/sjava/examples/%/Main.class: $(COMPILER) $$(call rwildcard,examples/%/,*.sjava)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main build $(call rwildcard,examples/$*/,*.sjava) -d bin/

$(EXAMPLE_FOLDERS_RUN_NAME): run-%: build-%
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.examples.$*.Main

clean:
	rm -rf bin

tester: $(BUILDS)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Tester