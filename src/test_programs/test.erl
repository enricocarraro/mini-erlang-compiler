start() ->
    easystore(),
    doubleStore(),
    Result = store2Items(),
    %N = doubleStoreDoubleDef(5),
    %Num = (doubleStoreDoubleDef(4) + 4) = N,
    Resex = start(2),
    Resex = example@node,
    Res3 = start(3),
    Res3 = 3,
    MynewList = [ 119 | "ords"],
    [1, 2, 3, 4],
    AnotherList = [98 | MynewList],
   ImproperList = [98 | [101, 105 | hello]],
    %ImproperList = [98 | [101, 105 | [109]]],
    io:format("AnotherList -> ~w: ~s ~n", [AnotherList, AnotherList]),
    io:format("ciao: ~s ~w: ~s ~n", ["format", MynewList, MynewList]),
    io:format("ciao ~w ~n", [fib(10)]),
    io:format("ImproperList ~w ~n", [ImproperList]),
    io:format("Second el. of MynewList (~w): ~w~n", [MynewList, lists:nth(2, MynewList)]),
    io:format("Append \"ciao \" to \"bello\": ~w -> ~s ~n", ["ciao " ++ "bello", lists:append("ciao ", "bello")]),
    %io:format("ImproperList ~s ~n", [ImproperList]),
    patternMatching(),
    io:format("funFunction(5)= ~w ~n", [funFunction(5)]),
    io:format("funFunction(2)= ~w ~n", [funFunction(2)]),
    io:format("funFunction(4)= ~w ~n", [funFunction(4)]),
    io:format("funFunction(1)= ~w ~n", [funFunction(1)]),
    io:format("funFunction(0)= ~w ~n", [funFunction(0)]).
    %Bad = badMatchStore().
fib(0) -> 0;    
fib(1) -> 1;
fib(N) -> Res = fib(N-1) + fib(N-2).
easystore() -> Num = 2.
store2Items() -> 
    Listing = [104, 101],
    Listing2 = [],
    Listing3 = [1].
patternMatching() ->
    [H | T] = [1, 2, 3, 4],
    io:format("H: ~w T: ~w~n", [H, T]),
    Five = 5,
    io:format("Five: ~w~n", [Five]),
    OriginalComplexList = [1, 2, [3, 4, 5]],
    io:format("OriginalComplexList: ~w~n", [OriginalComplexList]),
    
    [First, Second, [HeadOfThird | TailOfThird]] = OriginalComplexList,
    io:format("here"),
    io:format("Destructured OriginalComplexList -> First: ~w Second: ~w HeadOfThird: ~w TailOfThird: ~w~n", [First, Second, HeadOfThird, TailOfThird]).

badMatchStore() -> 
    Num = 2,
    Num = 90.
doubleStore() -> 
    Num = 3,
    Atom = hello.
doubleStoreDoubleDef(2) ->
    Atom = hello2,
    Num = 8;
doubleStoreDoubleDef(4) ->
    Atom = hello4,
    Num = 16;
doubleStoreDoubleDef(N) ->
    Atom = hello2,
    Num = N * 4,
    OtherNum = 5 * 4,
%    StrangeString = "example@n\"ode",
    Listing = [1, 2],
    Boolean = Atom == hello2.
start(2) ->
    Num = 42,
    Pi = 3.14159 + 4 / 3,
    Hello = hello,
    NumUndef = OtherUndef = Num = 42,
    OtherNode = example@node; %,
%    StrangeString = "example@n\"ode",
%    Listing = [1, 2, 3, 4];
start(N) ->
    Lol = N.

funFunction(N) when N > 3, N*2 < 9 -> N + 3;
funFunction(N) when N > 2 -> N + 2;
funFunction(N) when N > 1 -> N + 1;
funFunction(0) -> hello;
funFunction(N) -> N.


