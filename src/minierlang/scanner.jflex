package minierlang;
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
"/"     {return symbol(sym.SLASH);}
"."     {return symbol(sym.DOT);}
","     {return symbol(sym.COMMA);}
":"     {return symbol(sym.COLON);}
";"     {return symbol(sym.SEMICOLON);}
"="     {return symbol(sym.MATCH);}
"|"     {return symbol(sym.VERTICAL_BAR);}
"!"     {return symbol(sym.NOT);}
"-"     {return symbol(sym.HYPHEN);}
"+"     {return symbol(sym.PLUS);}
"++"    {return symbol(sym.PLUS_PLUS);}
"*"     {return symbol(sym.STAR);}
"->"    {return symbol(sym.RIGHT_ARROW);}
"<-"    {return symbol(sym.LEFT_ARROW);}
"=="    {return symbol(sym.EQ);}
"=:="   {return symbol(sym.EXACT_EQ);}
"/="    {return symbol(sym.NOT_EQ);}
"=/="   {return symbol(sym.EXACT_NOT_EQ);}
"<"     {return symbol(sym.LESS);}
"=<"    {return symbol(sym.LESS_EQ);}
">"     {return symbol(sym.GREATER);}
">="    {return symbol(sym.GREATER_EQ);}


/* Keywords */
"and"       {return symbol(sym.K_AND);}
"not"       {return symbol(sym.K_NOT);}
"or"        {return symbol(sym.K_OR);}
"xor"       {return symbol(sym.K_XOR);}
"div"       {return symbol(sym.K_DIV);}
"rem"       {return symbol(sym.K_REM);}
"when"      {return symbol(sym.K_WHEN);}

/* Boolean terms */
"false"	{ return new Symbol(sym.BOOLEAN, yyline, yycolumn, new String(yytext())); }
"true"	{ return new Symbol(sym.BOOLEAN, yyline, yycolumn, new String(yytext())); }

/* String term */
[\"]([^\"\\]|\\.)*[\"]         { return new Symbol(sym.STRING, yyline, yycolumn, new String(yytext().substring(1, yytext().length() - 1))); }

/* Variable names */
[A-Z_][0-9a-zA-Z_@]*    { return new Symbol(sym.VARIABLE, yyline, yycolumn, new String(yytext())); }

/* Atom terms */
[a-z][0-9a-zA-Z_@]*     { return new Symbol(sym.ATOM, yyline, yycolumn, new String(yytext())); }
[\']([^\'\\]|\\.)*[\']         { return new Symbol(sym.ATOM, yyline, yycolumn, new String(yytext().substring(1, yytext().length() - 1))); }

/* Float terms */
[0-9]*\.[0-9]+          { return new Symbol(sym.FLOAT, yyline, yycolumn, new Float(yytext())); }

/* Integer terms */
[1-9][0-9]*|0     { return new Symbol(sym.INT, yyline, yycolumn, new Integer(yytext())); }

/* Comments */
"%" [^\r\n]* {nl}?  {;}

{ws}|{nl}       {;}

. {System.out.println("SCANNER ERROR: "+yytext());}