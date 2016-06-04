ifeq ($(OS),Windows_NT)
	SEPARATOR=\;
else
	SEPARATOR=:
endif
classpathify=$(subst $(eval) ,$(SEPARATOR),$1)

rwildcard=$(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2) $(filter $(subst *,%,$2),$d))

JARS=$(wildcard lib/*)

EXAMPLE_FILES=$(wildcard examples/*.sjava)
EXAMPLE_FILES_NAME=$(patsubst examples/%.sjava,%,$(EXAMPLE_FILES))
EXAMPLE_FILES_MAINCLASS=$(patsubst %,bin/%/Main.class,$(EXAMPLE_FILES_NAME))

EXAMPLE_FOLDERS=$(filter-out examples/,$(sort $(dir $(wildcard examples/*/))))
EXAMPLE_FOLDERS_NAME=$(patsubst examples/%/,%,$(EXAMPLE_FOLDERS))
EXAMPLE_FOLDERS_MAINCLASS=$(patsubst %,bin/%/Main.class,$(EXAMPLE_FOLDERS_NAME))

TARGETS=$(EXAMPLE_FILES_NAME) $(EXAMPLE_FOLDERS_NAME)

.PHONY: all recompile clean diff $(TARGETS)

all: bin/compile/Main.class $(TARGETS)

recompile:
	java -classpath $(call classpathify,$(JARS) bin/compile) Main compile.sjava -d bin/compile/
	diff bin/out bin/compile

$(EXAMPLE_FILES_NAME): %: bin/%/Main.class

$(EXAMPLE_FILES_MAINCLASS): bin/%/Main.class: bin/out/Main.class examples/%.sjava
	java -classpath $(call classpathify,$(JARS) bin/out) Main $(word 2,$^) -d bin/$*/

$(EXAMPLE_FOLDERS_NAME): %: bin/%/Main.class

.SECONDEXPANSION:
$(EXAMPLE_FOLDERS_MAINCLASS): bin/%/Main.class: bin/out/Main.class $$(call rwildcard,examples/%/,*.sjava)
	java -classpath $(call classpathify,$(JARS) bin/out) Main $(filter-out $<,$^) -d bin/$*/

diff: bin/compile/Main.class
	diff bin/out bin/compile

bin/compile/Main.class: bin/out/Main.class compile.sjava
	java -classpath $(call classpathify,$(JARS) bin/out) Main compile.sjava -d bin/compile/

bin/out/Main.class: compile.scm compile.sjava
	java -classpath $(call classpathify,$(JARS)) kawa.repl compile.scm compile.sjava

clean:
	rm -rf bin

clean-%:
	rm -rf bin/$*