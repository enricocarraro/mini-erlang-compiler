default: compile
	cd .. && java minierlang/Main ./test_programs/subex.erl > ./llvm/new.ll

compile: scanner parser
	javac *.java ./**/*.java ./**/**/*.java ./**/**/**/*.java

scanner:
	jflex  scanner.jflex

parser:
	java java_cup.Main -expect 1 -parser parser parser.cup

clean:
	rm -f parser.java scanner.java sym.java
	rm -f *.class
	rm -f *.*~