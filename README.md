# Mini-Erlang Compiler
Compiler for a subset of Erlang (mini-Erlang) that generates LLVM IR, built using JFlex and CUP.

## Requirements
* [JFlex](https://jflex.de/) and [CUP](http://www2.cs.tum.edu/projects/cup/) (installation guide for the version with the parse tree drawer [here](https://www.skenz.it/compilers/install_linux_bash))
* [LLVM](https://llvm.org/docs/)
* Java


## Usage

### Converting Erlang code to LLVM IR

```bash
jflex scanner.jflex;
java java_cup.Main parser.cup;
javac *.java;
java Main example.erl
```

### Converting IR to LLVM bitcode

```bash
llvm-as example.ll –o example.bc
```

### Executing LLVM bitcode

```bash
lli example.bc
```

### Converting LLVM bitcode to target machine assembly

```bash
llc example.bc –o example.s
```

## Further resources

* [Getting Started with Erlang](https://erlang.org/doc/getting_started/intro.html)
* [Learn Erlang in Y minutes](https://learnxinyminutes.com/docs/erlang/)
* [Erlang by Example](https://erlangbyexample.org/)
* [JFlex](https://jflex.de/)
* [CUP](http://www2.cs.tum.edu/projects/cup/)
* [LLVM Cookbook](https://subscription.packtpub.com/book/application_development/9781785285981): [Converting a C source code to LLVM assembly](https://subscription.packtpub.com/book/application_development/9781785285981/1/ch01lvl1sec12/-converting-a-c-source-code-to-llvm-assembly)
* [Formal Languages and Compilers extra-project slides: Introduction to Erlang, LLVM pipeline overview, and proposal](https://slides.com/enricocarraro/erlang)
* [Mapping High Level Constructs to LLVM IR](https://mapping-high-level-constructs-to-llvm-ir.readthedocs.io/)
