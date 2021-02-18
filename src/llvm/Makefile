default:
	@awk '//; /\@printf\(\i8\*, \.\.\.\)\ \#[0-9][0-9]*/{while(getline line<"new.ll"){print line}}' base.ll > compiled.ll
	lli compiled.ll
executable:
	llvm-as compiled.ll
	llc -filetype=obj compiled.bc
	g++ example.o

run: default 
	./a.out

clean:
	rm -f *.ll
	rm -f *.bc
	rm -f *.o
	rm -f *.out

	