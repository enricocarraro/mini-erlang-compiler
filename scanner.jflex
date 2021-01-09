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
ws = [ \t]

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
"*"     {return symbol(sym.STAR);}
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


/* Keywords */
"after"     {return symbol(sym.K_AFTER);}
"and"       {return symbol(sym.K_AND);}
"andalso"   {return symbol(sym.K_ANDALSO);}
"not"       {return symbol(sym.K_NOT);}
"or"        {return symbol(sym.K_OR);}
"orelse"    {return symbol(sym.K_ORELSE);}
"xor"       {return symbol(sym.K_XOR);}
"band"      {return symbol(sym.K_BAND);}
"begin"     {return symbol(sym.K_BEGIN);}
"bnot"      {return symbol(sym.K_BNOT);}
"bor"       {return symbol(sym.K_BOR);}
"bsl"       {return symbol(sym.K_BSL);}
"bsr"       {return symbol(sym.K_BSR);}
"bxor"      {return symbol(sym.K_BXOR);}
"case"      {return symbol(sym.K_CASE);}
"catch"     {return symbol(sym.K_CATCH);}
"cond"      {return symbol(sym.K_COND);}
"div"       {return symbol(sym.K_DIV);}
"end"       {return symbol(sym.K_END);}
"fun"       {return symbol(sym.K_FUN);}
"if"        {return symbol(sym.K_IF);}
"let"       {return symbol(sym.K_LET);}
"of"        {return symbol(sym.K_OF);}
"rem"       {return symbol(sym.K_REM);}
"try"       {return symbol(sym.K_TRY);}
"when"      {return symbol(sym.K_WHEN);}

/* BIFs */

/* Allowed in Guards */
"is_atom" {return symbol(sym.B_IS_ATOM);}
"is_boolean" {return symbol(sym.B_IS_BOOLEAN);}
"is_float" {return symbol(sym.B_IS_FLOAT);}
"is_function" {return symbol(sym.B_IS_FUNCTION);}
"is_integer" {return symbol(sym.B_IS_INTEGER);}
"is_list" {return symbol(sym.B_IS_LIST);}
"is_number" {return symbol(sym.B_IS_NUMBER);}
/* Other BIFs */
"abs" {return symbol(sym.B_ABS);}
"float" {return symbol(sym.B_FLOAT);}
"hd" {return symbol(sym.B_HD);}
"length" {return symbol(sym.B_LENGTH);}
"round" {return symbol(sym.B_ROUND);}
"tl" {return symbol(sym.B_TL);}
"trunc" {return symbol(sym.B_TRUNC);}

/*  
Unsupported BIFs:
  is_map, is_binary,is_pid,is_port, is_record,
  is_record,is_reference, is_tuple, bit_size,
  byte_size, element, map_get, map_size, node,
  self, size, tuple_size. 
*/

[\"]([^\"\\]|\\.)*[\"]         { return new Symbol(sym.STRING, yyline, yycolumn, new String(yytext().substring(1, yytext().length() - 1))); }
[A-Z_][0-9a-zA-Z_@]*    { return new Symbol(sym.VARIABLE, yyline, yycolumn, new String(yytext())); }

[a-z][0-9a-zA-Z_@]*     { return new Symbol(sym.ATOM, yyline, yycolumn, new String(yytext())); }
[\']([^\'\\]|\\.)*[\']         { return new Symbol(sym.ATOM, yyline, yycolumn, new String(yytext().substring(1, yytext().length() - 1))); }

[0-9]*\.[0-9]+          { return new Symbol(sym.FLOAT, yyline, yycolumn, new Float(yytext())); }

[1-9][0-9]*|0     { return new Symbol(sym.INT, yyline, yycolumn, new Integer(yytext())); }

"/*" ~ "*/"     {;}

{ws}|{nl}       {;}

. {System.out.println("SCANNER ERROR: "+yytext());}

