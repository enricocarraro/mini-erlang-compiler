
% Function Overloading works only for functions with a different number of parameters
fib(0) -> 1;    

fib(1) -> 1;

fib(N) -> fib(N-1) + fib(N-2).

% List comprehension 

%[X || X <- [1,2,a,3,4,b,5,6], X > 3].
% [a,4,b,5,6]
%[{X, Y} || X <- [1,2,3], Y <- [a,b]].
% [{1,a},{1,b},{2,a},{2,b},{3,a},{3,b}]

