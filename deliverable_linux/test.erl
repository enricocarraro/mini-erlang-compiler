start() ->
    % Terms
    AnAtom = atom,
    Integer = 1,
    Float = 2.2,
    ListOfNumbers = [3.1415, 2.7182, 1.61803],
    ListFromString = "String",
    MixedNotationList = [ 119 | "ords"],
    ImproperList = [98 | [101, 105 | hello]],
    "words" = MixedNotationList,
    % Function Calls
    io:format("fib(0): ~w, fib(1): ~w, fib(2): ~w, fib(3): ~w, fib(4): ~w, fib(5): ~w ~n", [fib(0), fib(1), fib(2), fib(3), fib(4), fib(5)]),
    io:format("funFunction(5)= ~w ~n", [funFunction(5)]),
    io:format("funFunction(2)= ~w ~n", [funFunction(2)]),
    io:format("funFunction(4)= ~w ~n", [funFunction(4)]),
    io:format("funFunction(1)= ~w ~n", [funFunction(1)]),
    io:format("funFunction(0)= ~w ~n", [funFunction(0)]),
    % Concatenating lists:
    HelloWorld = "hello " ++ "world",
    io:format("'hello ' ++ 'world' -> ~s~n", [HelloWorld]),
    OneToSix = [1,2,3] ++ [4, 5, 6],
    io:format("[1,2,3] ++ [4, 5, 6] -> ~w~n", [OneToSix]),
    % Pattern Matching
    [H | T] = [1, 2, 3, 4],
    io:format("H: ~w T: ~w~n", [H, T]),
    ComplexList = [1, 2, [3, 4, 5]],
    io:format("ComplexList: ~w~n", [ComplexList]),
    [First, Second, [HeadOfThird | TailOfThird]] = ComplexList,
    io:format("Destructured ComplexList -> First: ~w Second: ~w HeadOfThird: ~w TailOfThird: ~w~n", [First, Second, HeadOfThird, TailOfThird]).
    
% Function to compute the n-th Fibonacci number.
fib(0) -> 0;    
fib(1) -> 1;
fib(N) -> Result = fib(N-1) + fib(N-2).


% Example Function to show the use of guards.
funFunction(N) when N > 3, N * 2 < 9 -> N + 3;
funFunction(N) when N > 2 -> N + 2;
funFunction(N) when N > 1 -> N + 1;
funFunction(0) -> hello;
funFunction(N) -> N.


