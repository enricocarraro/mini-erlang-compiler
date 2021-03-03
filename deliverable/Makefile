all:
	+$(MAKE) -C minierlang && cat base.ll new.ll  > compiled.ll && rm new.ll && lli compiled.ll

clean:
	+$(MAKE) -C minierlang clean

