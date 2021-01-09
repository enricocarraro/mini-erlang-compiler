default: scanner fast_parser
	javac *.java 

draw: scanner parser
	javac *.java 

scanner:
	jflex  scanner.jflex

parser:
	java java_cup.MainDrawTree -expect 1 -parser parser parser.cup

fast_parser:
	java java_cup.Main -expect 1 -parser parser parser.cup

run: 
	java Main subex.erl > file.cpp

clean:
	rm -f parser.java scanner.java sym.java
	rm -f *.class
	rm -f *.*~

