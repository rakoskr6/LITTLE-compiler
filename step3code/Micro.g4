// File name:   Micro.g4
// Updated:     17 September 2016
// Authors:     Brian Rieder
//              Kyle Rakos
// Description: g4 file to generate grammar for LITTLE within Antlr

grammar Micro;

// Program
eval
    : program;
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

// Keywords
/*KEYWORD
    : (PROGRAM_KW | BEGIN_KW | END_KW | FUNCTION_KW | READ_KW | WRITE_KW | IF_KW | ELSIF_KW | ENDIF_KW | DO_KW | WHILE_KW
   | CONTINUE_KW | BREAK_KW | RETURN_KW | INT_KW | VOID_KW | STRING_KW | FLOAT_KW | TRUE_KW | FALSE_KW) ;
PROGRAM_KW: 'PROGRAM';
BEGIN_KW: 'BEGIN';
END_KW: 'END';
FUNCTION_KW: 'FUNCTION';
READ_KW: 'READ';
WRITE_KW: 'WRITE';
IF_KW: 'IF';
ELSIF_KW: 'ELSIF';
ENDIF_KW: 'ENDIF';
DO_KW: 'DO';
WHILE_KW: 'WHILE';
CONTINUE_KW: 'CONTINUE';
BREAK_KW: 'BREAK';
RETURN_KW: 'RETURN';
INT_KW: 'INT';
VOID_KW: 'VOID';
STRING_KW: 'STRING';
FLOAT_KW: 'FLOAT';
TRUE_KW: 'TRUE';
FALSE_KW: 'FALSE';*/

// Operators
/*OPERATOR
    : (ASSIGN_OP | ADD_OP | SUBTRACT_OP | MULTIPLY_OP | DIVIDE_OP | EQUAL_OP | NOTEQUAL_OP | LT_OP | GT_OP | LPAREN | RPAREN | SEMICOL | COMMA | LTE_OP | GTE_OP) ;
ASSIGN_OP: ':=' ;
ADD_OP: '+' ;
SUBTRACT_OP: '-' ;
MULTIPLY_OP: '*';
DIVIDE_OP: '/';
EQUAL_OP: '=';
NOTEQUAL_OP: '!=';
LT_OP: '<';
GT_OP: '>';
LPAREN: '(';
RPAREN: ')';
SEMICOL: ';';
COMMA: ',';
LTE_OP: '<=';
GTE_OP: '>=';*/

// Identifiers and Literals
COMMENT :
    '--'.*?'\n'
    -> skip ;

WS
    : [ \t\r\n]+
    -> skip ;
IDENTIFIER // will begin with a letter and be followed by up to 30 letters/numbers
    // : [A-z][A-z0-9]{0,30} ;
    : [A-z][A-z0-9]*
    {
        if (getText().length() > 31)
            throw new RuntimeException("Error: Exceeded 31 characters in identifier");
    };
INTLITERAL
    : [0-9]+ ;
FLOATLITERAL
    : [0-9]*?'.'[0-9]+ ;
STRINGLITERAL // begins and ends with ", contains 0-80 non-" characters
    // : '"'~["]{0,80}'"' ;
    : '"'~["]*'"' 
    {
        if (getText().length() > 82)
            throw new RuntimeException("Error: Exceeded 82 characters in string");
    };
