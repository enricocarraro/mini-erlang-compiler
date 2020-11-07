import java_cup.runtime.*;

%%

%class scanner
%unicode
%cup
%line
%column


%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
	
  }
%}

nl = \r|\n|\r\n
tab = \t
id = [A-Za-z_][A-Za-z0-9_]*
integer =  ([1-9][0-9]*|0)
double = (([0-9]+\.[0-9]*) | ([0-9]*\.[0-9]+)) (e|E('+'|'-')?[0-9]+)?

%%

/* Symbols */
"("     {return symbol(sym.ROUND_OPEN);}
")"     {return symbol(sym.ROUND_CLOSE);}
"["     {return symbol(sym.SQUARE_OPEN);}
"]"     {return symbol(sym.SQUARE_CLOSE);}
"{"     {return symbol(sym.BRACE_OPEN);}
"}"     {return symbol(sym.BRACE_CLOSE);}
"#"     {return symbol(sym.SHARP);}
"/"     {return symbol(sym.SLASH);}
"."     {return symbol(sym.DOT);}
".."    {return symbol(sym.DOUBLE_DOT);}
"..."   {return symbol(sym.TRIPLE_DOT);}
","     {return symbol(sym.COMMA);}
":"     {return symbol(sym.COLON);}
";"     {return symbol(sym.SEMICOLON);}
"="     {return symbol(sym.MATCH);}
":="    {return symbol(sym.MAP_MATCH);}
"|"     {return symbol(sym.VERTICAL_BAR);}
"||"    {return symbol(sym.DOUBLE_VERTICAL_BAR);}
"?"     {return symbol(sym.QUESTION);}
"??"    {return symbol(sym.DOUBLE_QUESTION);}
"!"     {return symbol(sym.NOT);}
"-"     {return symbol(sym.HYPHEN);}
"--"    {return symbol(sym.MINUS_MINUS);}
"+"     {return symbol(sym.PLUS);}
"*"     {return symbol(sym.MULTIPLY);}
"->"    {return symbol(sym.RIGHT_ARROW);}
"<-"    {return symbol(sym.LEFT_ARROW);}
"=>"    {return symbol(sym.DOULBE_RIGHT_ARROW);}
"<="    {return symbol(sym.DOUBLE_LEFT_ARROW);}
">>"    {return symbol(sym.DOULBE_RIGHT_ANGLE);}
"<<"    {return symbol(sym.DOUBLE_LEFT_ANGLE);}
"=="    {return symbol(sym.EQ);}
"=:="   {return symbol(sym.EXACT_EQ);}
"/="    {return symbol(sym.NOT_EQ);}
"=/="   {return symbol(sym.EXACT_NOT_EQ);}
"<"     {return symbol(sym.LESS);}
"=<"    {return symbol(sym.LESS_EQ);}
">"     {return symbol(sym.GREATER);}
">="    {return symbol(sym.GREATER_EQ);}

/* Whitespaces */
" "     {return symbol(sym.SPACE);}
{tab}   {return symbol(sym.TAB);}
{nl}    {return symbol(sym.NEW_LINE);}

/* Keywords */
"after"     {return symbol(sym.AFTER);}
"and"       {return symbol(sym.AND);}
"andalso"   {return symbol(sym.ANDALSO);}
"not"       {return symbol(sym.NOT);}
"or"        {return symbol(sym.OR);}
"orelse"    {return symbol(sym.ORELSE);}
"xor"       {return symbol(sym.XOR);}
"band"      {return symbol(sym.BAND);}
"begin"     {return symbol(sym.BEGIN);}
"bnot"      {return symbol(sym.BNOT);}
"bor"       {return symbol(sym.BOR);}
"bsl"       {return symbol(sym.BSL);}
"bsr"       {return symbol(sym.BSR);}
"bxor"      {return symbol(sym.BXOR);}
"case"      {return symbol(sym.CASE);}
"catch"     {return symbol(sym.CATCH);}
"cond"      {return symbol(sym.COND);}
"div"       {return symbol(sym.DIV);}
"end"       {return symbol(sym.END);}
"fun"       {return symbol(sym.FUN);}
"if"        {return symbol(sym.IF);}
"let"       {return symbol(sym.LET);}
"of"        {return symbol(sym.OF);}
"rem"       {return symbol(sym.REM);}
"try"       {return symbol(sym.TRY);}
"when"      {return symbol(sym.WHEN);}


"int"   {return symbol(sym.INT_TYPE);}
"double" {return symbol(sym.DOUBLE_TYPE);}

print   {return symbol(sym.PRINT);}
if      {return symbol(sym.IF);}
while   {return symbol(sym.WHILE);}
else    {return symbol(sym.ELSE);}
;       {return symbol(sym.S);}
,       {return symbol(sym.CM);}

{id}      {return symbol(sym.ID, yytext());}
{integer} {return symbol(sym.INT, new Integer(yytext()));}
{double}  {return symbol(sym.DOUBLE, new Double(yytext()));}

"/*" ~ "*/"     {;}

{ws}|{nl}       {;}

. {System.out.println("SCANNER ERROR: "+yytext());}

