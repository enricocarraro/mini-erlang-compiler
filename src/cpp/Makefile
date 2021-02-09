default: emitdebug replacenames addcompiled

emitprod:
	@clang -DTEST='0' -DDEBUG='0' -emit-llvm -S TypedefLiteralWrappedExc.cpp -o ../llvm/base.ll

emitdebug:
	@clang -DTEST='0' -DDEBUG='1' -emit-llvm -S TypedefLiteralWrappedExc.cpp -o ../llvm/base.ll

# TODO: Create a cpp file with tests to emit here.
emittest:
	@clang -DTEST='1' -DDEBUG='1' -emit-llvm -S TypedefLiteralWrappedExc.cpp -o ../llvm/base.ll

test: emittest
	lli ../llvm/base.ll

replacenames: replacebifs replacestart

replacestart:
	@awk '{gsub("invoke void \@\_Z11placeholderv","invoke void \@start_0")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll

# BIFs
replacebifs:
	
	@awk '{gsub("_Z14BIF_is_booleanRK7Literal","BIF_is_boolean_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z12BIF_is_floatRK7Literal","BIF_is_float_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z14BIF_is_integerRK7Literal","BIF_is_integer_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z15BIF_is_functionRK7Literal","BIF_is_function_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z11BIF_is_listRK7Literal","BIF_is_list_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z13BIF_is_numberRK7Literal","BIF_is_number_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z9BIF_truncRK7Literal","BIF_trunc_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z9BIF_roundRK7Literal","BIF_round_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z10BIF_lengthRK7Literal","BIF_length_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z6BIF_tlRK7Literal","BIF_tl_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z6BIF_hdRK7Literal","BIF_hd_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z9BIF_floatRK7Literal","BIF_float_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll
	@awk '{gsub("_Z7BIF_absRK7Literal","BIF_abs_1")}1' ../llvm/base.ll  > ../llvm/tmp.ll && mv ../llvm/tmp.ll ../llvm/base.ll

addcompiled:
	@awk '//; /\@llvm\.trap\(\)\ \#6/{while(getline line<"../llvm/new.ll"){print line}}' ../llvm/base.ll > ../llvm/tmp
	@mv ../llvm/tmp ../llvm/base.ll


clean:
	rm -f *.ll
	rm -f *.*~