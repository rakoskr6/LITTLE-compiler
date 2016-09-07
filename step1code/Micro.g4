// File name:   Micro.g4
// Updated:     6 September 2016
// Authors:     Brian Rieder
//              Kyle Rakos
// Description: g4 file to generate grammar for LITTLE within Antlr

grammar Micro;
// Program
program
    : 'PROGRAM' id 'BEGIN' pgm_body 'END';
id
    : IDENTIFIER ;
pgm_body
    : decl func_declarations ;
decl
    : string_decl decl
    | var_decl decl | ;

// Global String Declaration
string_decl
    : 'STRING' id ':=' str ';' ;
str:
    STRINGLITERAL ;

// Variable Declaration
var_decl
    : var_type id_list ';' ;
var_type
    : 'FLOAT'
    | 'INT' ;
any_type
    : var_type
    | 'VOID' ;
id_list
    : id id_tail ;
id_tail
    : ',' id id_tail
    | ;

// Function Parameter List
param_decl_list
    : param_decl param_decl_tail
    | ;
param_decl
    : var_type id ;
param_decl_tail
    : ',' param_decl param_decl_tail
    | ;

// Function Declarations
func_declarations
    : func_decl func_declarations
    | ;
func_decl
    : 'FUNCTION' any_type  id '(' param_decl_list ')' 'BEGIN' func_body 'END' ;
func_body
    : decl stmt_list ;

// Statement List
stmt_list
    : stmt stmt_list
    | ;
stmt
    : base_stmt
    | if_stmt
    | do_while_stmt ;
base_stmt
    : assign_stmt
    | read_stmt
    | write_stmt
    | return_stmt ;

// Basic Statements
assign_stmt
    : assign_expr ';' ;
assign_expr
    : id ':=' expr ;
read_stmt
    : 'READ' '(' id_list ')' ';' ;
write_stmt
    : 'WRITE' '(' id_list ')' ';' ;
return_stmt
    : 'RETURN' expr ';' ;

// Expressions
expr
    : expr_prefix factor ;
expr_prefix
    : expr_prefix factor addop
    | ;
factor
    : factor_prefix postfix_expr ;
factor_prefix
    : factor_prefix postfix_expr mulop
    | ;
postfix_expr
    : primary
    | call_expr ;
call_expr
    : id '(' expr_list ')' ;
expr_list
    : expr expr_list_tail
    | ;
expr_list_tail
    : ',' expr expr_list_tail
    | ;
primary
    : '(' expr ')'
    | id
    | INTLITERAL
    | FLOATLITERAL ;
addop
    : '+'
    | '-' ;
mulop
    : '*'
    | '/' ;

// Complex Statement and Condition
if_stmt
    : 'IF' '(' cond ')' decl stmt_list else_part 'ENDIF' ;
else_part
    : 'ELSIF' '(' cond ')' decl stmt_list else_part | ;
cond
    : expr compop expr | 'TRUE' | 'FALSE' ;
compop
    : '<'
    | '>'
    | '='
    | '!='
    | '<='
    | '>=' ;

do_while_stmt
    : 'DO' decl stmt_list 'WHILE' '(' cond ')' ';' ;

// Identifiers and Literals
IDENTIFIER // will begin with a letter and be followed by up to 30 letters/numbers -- THIS IS SUPER HACKY RIGHT NOW
    // : [A-z][A-z0-9]{0,30} ;
    : [A-z]
    [A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?
    [A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?
    [A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]?[A-z0-9]? ;
INTLITERAL
    : [0-9]+ ;
FLOATLITERAL
    : [0-9]*?'.'[0-9]+ ;
STRINGLITERAL // begins and ends with ", contains 0-80 non-" characters -- SUPER HACKY AGAIN
    // : '"'~["]{0,80}'"' ;
    : '"'
    ~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?
    ~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?
    ~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?
    ~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?~["]?
    '"' ;
COMMENT
    : '--'.*?'\n' ;

WS
    : [ \t\r\n]+
    -> skip
;

// Operators and Keywords
OPERATOR
    : (':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=') ;
KEYWORD
    : ('PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE' | 'IF' | 'ELSIF' | 'ENDIF' | 'DO' | 'WHILE'
    | 'CONTINUE' | 'BREAK' | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT' | 'TRUE' | 'FALSE') ;

/*
Keywords

PROGRAM,BEGIN,END,FUNCTION,READ,WRITE,
IF,ELSIF,ENDIF,DO,WHILE,CONTINUE,BREAK,
RETURN,INT,VOID,STRING,FLOAT,
TRUE,FALSE */

