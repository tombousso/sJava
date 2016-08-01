ifeq ($(OS),Windows_NT)
	SEPARATOR=\;
else
	SEPARATOR=:
endif
classpathify=$(subst $(eval) ,$(SEPARATOR),$1)

rwildcard=$(patsubst %,%,$(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2) $(filter $(subst *,%,$2),$d)))

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

STD=bin/sjava/std/Function0.class

COMPILER1=bin/1-2/compiler2/Main.class
COMPILER2=bin/2-2/compiler2/Main.class
COMPILER3=bin/2-3/sjava/compiler/Main.class
COMPILER=bin/sjava/compiler/Main.class
COMPILER3_3=bin/3-3/sjava/compiler/Main.class

COMPILER3_FILES=$(call rwildcard,compiler3/,*.sjava)
STD_FILES=$(wildcard std/*.sjava)

COMPILER3_CLASSFILES=$(call rwildcard,bin/sjava/compiler/,*.class)
COMPILER3_JAVA_FILES=$(patsubst bin/%.class,java/%.java,$(COMPILER3_CLASSFILES))

JAR=sjava.jar

.PHONY: all clean diff12 diff23 diff recompile $(TARGETS) $(CLEAN_TARGETS)

all: diff $(TARGETS)

$(STD): $(COMPILER) $(STD_FILES)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main $(STD_FILES) -d bin/

$(EXAMPLE_FILES_NAME): %: bin/%/Main.class

$(EXAMPLE_FILES_MAINCLASS): bin/%/Main.class: $(COMPILER) $(STD) examples/%.sjava
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main std/macros.sjava $(word 3,$^) -d bin/$*/

$(EXAMPLE_FOLDERS_NAME): %: bin/%/Main.class

.SECONDEXPANSION:
$(EXAMPLE_FOLDERS_MAINCLASS): bin/%/Main.class: $(COMPILER) $(STD) $$(call rwildcard,examples/%/,*.sjava)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main std/macros.sjava $(call rwildcard,examples/$*/,*.sjava) -d bin/$*/

diff12: $(COMPILER2)
	diff -r bin/1-2 bin/2-2

$(COMPILER3_3): $(COMPILER3_FILES)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin) sjava.compiler.Main std/macros.sjava $(COMPILER3_FILES) -d bin/3-3/

diff23: $(COMPILER) $(COMPILER3_3)
	diff -r bin/sjava/compiler bin/3-3/sjava/compiler

$(COMPILER1): compiler1.scm compiler2.sjava
	$(JAVA) -classpath $(call classpathify,$(JARS)) kawa.repl compiler1.scm compiler2.sjava

$(COMPILER2): $(COMPILER1) compiler2.sjava
	$(JAVA) -classpath $(call classpathify,$(JARS) bin/1-2) compiler2.Main compiler2.sjava -d bin/2-2/

$(COMPILER3): $(COMPILER2) $(COMPILER3_FILES) $(STD_FILES)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin/2-2) compiler2.Main std/macros.sjava $(COMPILER3_FILES) -d bin/2-3/

$(COMPILER): $(COMPILER3) $(COMPILER3_FILES) $(STD_FILES)
	$(JAVA) -classpath $(call classpathify,$(JARS) bin/2-3) sjava.compiler.Main std/macros.sjava $(COMPILER3_FILES) -d bin/

clean:
	rm -rf bin

$(CLEAN_TARGETS): clean-%:
	rm -rf bin/$*

diff: diff12 diff23

java/sjava/compiler/Main.java: $(COMPILER)
	mkdir -p java/sjava
	$(JAVA) -jar fernflower.jar -ind="    " -dgs=1 bin/sjava java/sjava

java-sources: java/sjava/compiler/Main.java

java-compile: diff
	javac -Xdiags:verbose -classpath $(call classpathify,$(JARS)) $(COMPILER3_JAVA_FILES)
	java -classpath $(call classpathify,$(JARS) java) sjava.compiler.Main std/macros.sjava $(COMPILER3_FILES) -d bin/java-3-3/
	diff -r bin/3-3 bin/java-3-3

$(JAR): $(COMPILER)
	jar cfm sjava.jar Manifest.txt -C bin sjava/

jar: $(JAR)