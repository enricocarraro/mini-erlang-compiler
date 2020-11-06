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
id = [A-Za-z_][A-Za-z0-9_]*
integer =  ([1-9][0-9]*|0)
double = (([0-9]+\.[0-9]*) | ([0-9]*\.[0-9]+)) (e|E('+'|'-')?[0-9]+)?

%%
"("     {return symbol(sym.RO);}
")"     {return symbol(sym.RC);}
"{"     {return symbol(sym.BO);}
"}"     {return symbol(sym.BC);}
"="     {return symbol(sym.EQ);}
"+"     {return symbol(sym.PLUS);}
"-"     {return symbol(sym.MINUS);}
"*"     {return symbol(sym.STAR);}
"/"     {return symbol(sym.DIV);}
"<"     {return symbol(sym.MIN);}
">"     {return symbol(sym.MAJ);}
"<="    {return symbol(sym.MIN_EQ);}
"=<"    {return symbol(sym.EQ_MIN);}
">="    {return symbol(sym.MAJ_EQ);}
"=>"    {return symbol(sym.EQ_MAJ);}
"&"     {return symbol(sym.AND);}
"|"     {return symbol(sym.OR);}
"!"     {return symbol(sym.NOT);}

"["     {return symbol(sym.SO);}
"]"     {return symbol(sym.SC);}

"int"   {return symbol(sym.INT_TYPE);}
"double" {return symbol(sym.DOUBLE_TYPE);}

print   {return symbol(sym.PRINT);}
if      {return symbol(sym.IF);}
while   {return symbol(sym.WHILE);}
else    {return symbol(sym.ELSE);}
then    {return symbol(sym.THEN);}
;       {return symbol(sym.S);}
,       {return symbol(sym.CM);}

{id}      {return symbol(sym.ID, yytext());}
{integer} {return symbol(sym.INT, new Integer(yytext()));}
{double}  {return symbol(sym.DOUBLE, new Double(yytext()));}

"/*" ~ "*/"     {;}

{ws}|{nl}       {;}

. {System.out.println("SCANNER ERROR: "+yytext());}
