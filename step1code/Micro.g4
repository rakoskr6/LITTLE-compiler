// Define a grammar called Hello
grammar Micro;
r  : 'hello' ID ;         // match keyword hello followed by an identifier
ID : [a-z]+ ;             // match lower-case identifiers
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

/*
//CAPS : CAPS is a token (terminal) made up of one or more characters.  
//small case symbols are non-terminals.

// Program
program           : PROGRAM id BEGIN pgm_body END ;
id                : IDENTIFIER ;
pgm_body          : decl func_declarations ;
decl              : string_decl decl | var_decl decl | empty ;

// Global String Declaration 
string_decl       : STRING id := str ;
str               : STRINGLITERAL ;

// Variable Declaration 
var_decl          : var_type id_list ;
var_type          : FLOAT | INT ;
any_type          : var_type | VOID ;
id_list           : id id_tail ;
id_tail           : , id id_tail | empty ;

// Function Paramater List 
param_decl_list   : param_decl param_decl_tail | empty ;
param_decl        : var_type id ;
param_decl_tail   : , param_decl param_decl_tail | empty ;

// Function Declarations 
func_declarations : func_decl func_declarations | empty ;
func_decl         : FUNCTION any_type id (param_decl_list) BEGIN func_body END ;
func_body         : decl stmt_list ;

// Statement List 
stmt_list         : stmt stmt_list | empty ;
stmt              : base_stmt | if_stmt | do_while_stmt ;
base_stmt         : assign_stmt | read_stmt | write_stmt | return_stmt ;

// Basic Statements 
assign_stmt       : assign_expr ;
assign_expr       : id := expr ;
read_stmt         : READ ( id_list ) ;
write_stmt        : WRITE ( id_list ) ;
return_stmt       : RETURN expr ;

// Expressions 
expr              : expr_prefix factor ;
expr_prefix       : expr_prefix factor addop | empty ;
factor            : factor_prefix postfix_expr ;
factor_prefix     : factor_prefix postfix_expr mulop | empty ;
postfix_expr      : primary | call_expr ;
call_expr         : id ( expr_list ) ;
expr_list         : expr expr_list_tail | empty ;
expr_list_tail    : , expr expr_list_tail | empty ;
primary           : (expr) | id | INTLITERAL | FLOATLITERAL ;
addop             : + | - ;
mulop             : * | / ;

// Complex Statements and Condition  
if_stmt           : IF ( cond ) decl stmt_list else_part ENDIF ;
else_part         : ELSIF ( cond ) decl stmt_list else_part | empty ;
cond              : expr compop expr | TRUE | FALSE ;
compop            : < | > | = | != | <= | >= ;

do_while_stmt       : DO decl stmt_list WHILE ( cond ); ;

//------

//an IDENTIFIER token will begin with a letter, and be followed by up to 30 letters and //numbers.  
//IDENTIFIERS are case sensitive. 

INTLITERAL: integer number 
            ex) 0, 123, 678
FLOATLITERAL: floating point number available in two different format
                yyyy.xxxxxx or .xxxxxxx
            ex) 3.141592 , .1414 , .0001 , 456.98

STRINGLITERAL  (Max 80 characters including '\0')
        :      any sequence of character except '"' 
            between '"' and '"' 
            ex) "Hello world!" , "***********" , "this is a string"

COMMENT:
      Starts with "--" and lasts till the end of line
      ex) -- this is a comment
      ex) -- any thing after the "--" is ignored 


Keywords

PROGRAM,BEGIN,END,FUNCTION,READ,WRITE,
IF,ELSIF,ENDIF,DO,WHILE,CONTINUE,BREAK,
RETURN,INT,VOID,STRING,FLOAT,
TRUE,FALSE

Operators
:= + - * / = != < > ( ) ; , <= >=
*/