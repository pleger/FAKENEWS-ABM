JAVAC ?= javac
JAVA ?= java
CLASSPATH := build/classes:lib/*

.PHONY: build test run clean

build:
	mkdir -p build/classes
	$(JAVAC) -cp "lib/*" -d build/classes $$(find src -name "*.java")

test: build
	$(JAVAC) -cp "$(CLASSPATH)" -d build/classes $$(find tests -name "*.java")
	$(JAVA) -cp "$(CLASSPATH)" TestRunner

run: build
	$(JAVA) -cp "$(CLASSPATH)" Main --input FAKENEWS_BASELINE --no-gui

clean:
	rm -rf build output
