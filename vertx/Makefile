include ../Makefile.compiler

.PHONY: all run

NET_JARS=$(shell mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -q && cat cp.txt)

bin/Main.class: $(COMPILER) Main.sjava
	@echo compile
	@$(JAVA) -classpath $(call classpathify,$(TOP)bin $(JARS) $(NET_JARS)) sjava.compiler.Main $(STD_MACROS) Main.sjava -d bin/

all: bin/Main.class
	@echo run
	@$(JAVA) -classpath $(call classpathify,bin $(NET_JARS)) Main