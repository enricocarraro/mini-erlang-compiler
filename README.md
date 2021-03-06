# Mini-Erlang Compiler

Compiler for a subset of Erlang (mini-Erlang) that generates LLVM IR, built using JFlex and CUP.

## Subset implemented

- Types: , List, String, Boolean
  - Number: [Erlang specific notations](https://erlang.org/doc/reference_manual/data_types.html#number) are not supported.
  - Note: a term of type String is not a List of integers like in Erlang.
  - Comparison between different types is handled with the following order: Number < Atom < Function < List < String < Boolean.
- Arithmetic Expressions
- Boolean Expression
- Non anonymous functions that have at most one parameter
- One variable Pattern Matching
- Guards

## Design decisions

Since Erlang is a dynamically typed language executed on a virtual machine, to reduce development time I chose to implement a small set of C++ helpers to handle dynamic typing, throw exceptions, and implement Erlang's Built-in Function (BIFs). The C++ code is compiled to LLVM IR and added to each LLVM file produced by the CUP parser.

## Requirements

- [JFlex](https://jflex.de/) and [CUP](http://www2.cs.tum.edu/projects/cup/) (installation guide for the version with the parse tree drawer [here](https://www.skenz.it/compilers/install_linux_bash))
- [LLVM](https://llvm.org/docs/)
- Java

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

### Evaluating LLVM bitcode

```bash
lli example.bc
# We can also directly evaluate .ll files.
lli example.ll
```

### Converting LLVM bitcode to target machine assembly

```bash
llc example.bc –o example.s
```

### Creating an executable from LLVM bitcode

```bash
llc -filetype=obj example.bc
g++ example.o # Alternatively: clang++ example.o
./a.out # Runs the executable that was just created.
```

Since the type abstraction is written in C++ we use g++ (or clang++)
instead of gcc (or clang), otherwise we would get a compilation error.

## Further resources

- [Getting Started with Erlang](https://erlang.org/doc/getting_started/intro.html)
- [Learn Erlang in Y minutes](https://learnxinyminutes.com/docs/erlang/)
- [Erlang by Example](https://erlangbyexample.org/)
- [JFlex](https://jflex.de/)
- [CUP](http://www2.cs.tum.edu/projects/cup/)
- [LLVM Cookbook](https://subscription.packtpub.com/book/application_development/9781785285981): [Converting a C source code to LLVM assembly](https://subscription.packtpub.com/book/application_development/9781785285981/1/ch01lvl1sec12/-converting-a-c-source-code-to-llvm-assembly)
- [Formal Languages and Compilers extra-project slides: Introduction to Erlang, LLVM pipeline overview, and proposal](https://slides.com/enricocarraro/erlang)
- [Mapping High Level Constructs to LLVM IR](https://mapping-high-level-constructs-to-llvm-ir.readthedocs.io/)
- [LLVM.js](https://github.com/kripken/llvm.js) and [LLVM.js demo](https://kripken.github.io/llvm.js/demo.html) to write and run LLVM IR directly from your browser.
