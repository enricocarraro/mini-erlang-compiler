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
    io:format("ciao: ~s ~w ~n", ["format", 2.0]),
    io:format("ciao ~w ~n", [fib(10)]),

    Bad = badMatchStore().
fib(0) -> 0;    
fib(1) -> 1;
fib(N) -> Res = fib(N-1) + fib(N-2).
easystore() -> Num = 2.
store2Items() -> 
    Listing = [104, 101],
    Listing2 = [],
    Listing3 = [1].
badMatchStore() -> 
    Num = 2,
    Num = 90.
doubleStore() -> 
    Num = 3,
    Atom = hello.
doubleStoreDoubleDef(4) ->
    Atom = hello,
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