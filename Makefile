.PHONY: instruction part-1 test-part-1 part-2 test-part-2 clean

ifndef VERBOSE
.SILENT:
endif

instruction:
	echo "Available commands: "
	echo "\tmake part-1"
	echo "\tmake test-part-1"
	echo "\tmake part-2"
	echo "\tmake test-part-2"
	echo "\tclean"

part-1: ./build/base-out/Main.class
	mkdir -p ./build/out
	java -cp ./build/base-out Main run 1

test-part-1: ./build/base-out/Main.class
	mkdir -p ./build/out
	java -cp ./build/base-out Main test 1


part-2: ./build/base-out/Main.class
	mkdir -p ./build/out
	java -cp ./build/base-out Main run 2

test-part-2: ./build/base-out/Main.class
	mkdir -p ./build/out
	java -cp ./build/base-out Main test 2


./build/base-out/Main.class: ./build/Main.java
	echo "Building the build file"
	mkdir -p ./build/base-out
	javac -d ./build/base-out ./build/Main.java

clean:
	echo "Deleting build directories"
	rm -rf ./build/out
	rm -rf ./build/base-out
