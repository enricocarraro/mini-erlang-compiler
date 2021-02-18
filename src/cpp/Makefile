default: prod

debug: emitdebug replacenames

prod: emitprod replacenames

emitprod:
	@clang -DTEST='0' -DDEBUG='0' -emit-llvm -S TypedefLiteralWrappedExc.cpp -o ../llvm/base.ll

emitdebug:
	@clang -DTEST='0' -DDEBUG='1' -emit-llvm -S TypedefLiteralWrappedExc.cpp -o ../llvm/base.ll

# TODO: Create a cpp file with tests to emit here.
emittest: ../llvm/test.ll
	@clang -DTEST='1' -DDEBUG='1' -emit-llvm -S TypedefLiteralWrappedExc.cpp -o ../llvm/test.ll

test: emittest
	lli ../llvm/test.ll

replacenames: replacebifs replacelibfun replacestart 

replacestart:
	@awk '{gsub("invoke void \@\_Z11placeholderv","invoke void \@start.0")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll

# BIFs
replacebifs:	
	@awk '{gsub("_Z14BIF_is_booleanRK7Literal","BIF_is_boolean.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z12BIF_is_floatRK7Literal","BIF_is_float.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z14BIF_is_integerRK7Literal","BIF_is_integer.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z15BIF_is_functionRK7Literal","BIF_is_function.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z11BIF_is_listRK7Literal","BIF_is_list.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z13BIF_is_numberRK7Literal","BIF_is_number.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z9BIF_truncRK7Literal","BIF_trunc.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z9BIF_roundRK7Literal","BIF_round.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z10BIF_lengthRK7Literal","BIF_length.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z6BIF_tlRK7Literal","BIF_tl.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z6BIF_hdRK7Literal","BIF_hd.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z9BIF_floatRK7Literal","BIF_float.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z7BIF_absRK7Literal","BIF_abs.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll

replacelibfun:
	@awk '{gsub("_Z8ioformatRK7LiteralS1_","io.format.2")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z8ioformatRK7Literal","io.format.1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll

clean:
	rm -f *.*~