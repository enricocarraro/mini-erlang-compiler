easystore() -> Num = 2.
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
    Num = N * 4.
start() ->
    easystore(),
    doubleStore(),
    N = doubleStoreDoubleDef(5),
    Num = (doubleStoreDoubleDef(4) + 4) = N,
    Resex = start(2),
    Resex = example@node,
    Res3 = start(3),
    Res3 = 3,
    Bad = badMatchStore().
%    StrangeString = "example@n\"ode",
%    Listing = [1, 2, 3, 4].
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