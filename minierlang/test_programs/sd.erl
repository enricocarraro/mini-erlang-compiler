
Entity { 
    parserScope: //different functions should have different scopes with different variables
}
VariableValue {
    Type: {Int, Float, Atom}
    Value: 
}    
Variable extends Entity {
    Type: {Unassigned, Int, Float, Atom}
    Value: VariableValue;
    
}

Num = 43. 
/*
 *  Variables[Num] = {Type: Int, Value: 43}
 */

Numero = Mouse.
/*
 *  Variables[Mouse] = {Type: Unassigned}
 *  Variables[Numero] = {Type: Unassigned} 
 */


// Function Overloading works only for functions with a different number of parameters
fib(0) -> 1;    
/*
 *  if fib/1 has not been defined add to scope
 *  Function[fib/1].addbranch({
 *    if(fib.param[0].Type == Int && fib.param[0].Value == 0)
 *       return 1;
 *    })
 */
fib(1) -> 1;
/*
 *  if fib/1 has not been defined add to scope
 *  Function[fib/1].addbranch({
 *    if(fib.param[0].Type == Int && fib.param[0].Value == 1)
 *       return 1;
 *    })
 */
fib(N) when is_integer(N), N > 1 -> fib(N-1) + fib(N-2).
/*
 *  if fib/1 has not been defined add to scope
 *  Function[fib/1].addbranch({
 *    if(fib.param[0].Type == Int && fib.param[0].Value == 1)
 *       return 1;
 *    })
 */

/*
* 
* List comprehension 
* 
*/
[X || X <- [1,2,a,3,4,b,5,6], X > 3].
// [a,4,b,5,6]
[{X, Y} || X <- [1,2,3], Y <- [a,b]].
// [{1,a},{1,b},{2,a},{2,b},{3,a},{3,b}]

