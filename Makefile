ifeq ($(OS),Windows_NT)
	SEPARATOR=\;
else
	SEPARATOR=:
endif
classpathify=$(subst $(eval) ,$(SEPARATOR),$1)

rwildcard=$(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2) $(filter $(subst *,%,$2),$d))

STD=$(wildcard std/*)

JARS=$(wildcard lib/*)

EXAMPLE_FILES=$(wildcard examples/*.sjava)
EXAMPLE_FILES_NAME=$(patsubst examples/%.sjava,%,$(EXAMPLE_FILES))
EXAMPLE_FILES_MAINCLASS=$(patsubst %,bin/%/Main.class,$(EXAMPLE_FILES_NAME))

EXAMPLE_FOLDERS=$(filter-out examples/,$(sort $(dir $(wildcard examples/*/))))
EXAMPLE_FOLDERS_NAME=$(patsubst examples/%/,%,$(EXAMPLE_FOLDERS))
EXAMPLE_FOLDERS_MAINCLASS=$(patsubst %,bin/%/Main.class,$(EXAMPLE_FOLDERS_NAME))

TARGETS=$(EXAMPLE_FILES_NAME) $(EXAMPLE_FOLDERS_NAME)
CLEAN_TARGETS=$(patsubst %,clean-%,$(TARGETS))

JAVA=java

.PHONY: all recompile clean diff $(TARGETS) $(CLEAN_TARGETS)

all: bin/compile/Compiler.class $(TARGETS)

recompile:
	$(JAVA) -classpath $(call classpathify,$(JARS) bin/compile) Compiler compile.sjava -d bin/compile/
	diff bin/out bin/compile

$(EXAMPLE_FILES_NAME): %: bin/%/Main.class

$(EXAMPLE_FILES_MAINCLASS): bin/%/Main.class: bin/out/Compiler.class examples/%.sjava $(STD)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin/out) Compiler $(word 2,$^) $(STD) -d bin/$*/

$(EXAMPLE_FOLDERS_NAME): %: bin/%/Main.class

.SECONDEXPANSION:
$(EXAMPLE_FOLDERS_MAINCLASS): bin/%/Main.class: bin/out/Compiler.class $$(call rwildcard,examples/%/,*.sjava) $(STD)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin/out) Compiler $(filter-out $<,$^) $(STD) -d bin/$*/

diff: bin/compile/Compiler.class
	diff bin/out bin/compile

bin/compile/Compiler.class: bin/out/Compiler.class compile.sjava
	$(JAVA) -classpath $(call classpathify,$(JARS) bin/out) Compiler compile.sjava -d bin/compile/

bin/out/Compiler.class: compile.scm compile.sjava
	$(JAVA) -classpath $(call classpathify,$(JARS)) kawa.repl compile.scm compile.sjava

clean:
	rm -rf bin

$(CLEAN_TARGETS): clean-%:
	rm -rf bin/$*