grammar TomLang;

// ---------- Parser ----------
prog        : (decl | stmt)* EOF ;

decl        : type ID ('=' expr)? ';' ;

type        : 'int' | 'float' | 'string' ;

stmt
    : assign ';'
    | ifStmt
    | whileStmt
    | doWhileStmt
    | forStmt
    | switchStmt
    | breakStmt
    | printStmt ';'
    | readStmt ';'
    | block
    ;

assign      : ID '=' expr ;

ifStmt      : 'if' '(' expr ')' block ('else' block)? ;

whileStmt   : 'while' '(' expr ')' block ;

doWhileStmt : 'do' block 'while' '(' expr ')' ';';

switchStmt : 'switch' '(' expr ')' '{' switchSection* defaultSection? '}' ;

switchSection : 'case' switchLabel ':' stmt* ;

defaultSection : 'default' ':' stmt* ;

switchLabel : INT_LIT
            | STRING
            | ID
            ;

breakStmt : 'break' ';' ;

forStmt     : 'for' '(' forInit ';' forCond? ';' forUpdate? ')' block ;
forInit     : decl
            | assign
            |
            ;
forCond     : expr ;
forUpdate   : assign ;

block       : '{' stmt* '}' ;

printStmt   : 'printf' '(' (STRING | expr) ')' ;
readStmt    : 'scanf' '(' ID ')' ;

// ---------- Expressions (precedência alta -> baixa no fundo) ----------
expr        : orExpr ;
orExpr      : andExpr ( '||' andExpr )* ;
andExpr     : eqExpr ( '&&' eqExpr )* ;
eqExpr      : relExpr ( ('==' | '!=') relExpr )* ;
relExpr     : addExpr ( ('<' | '>' | '<=' | '>=') addExpr )* ;
addExpr     : mulExpr ( ('+' | '-') mulExpr )* ;
mulExpr     : unary ( ('*' | '/') unary )* ;
unary       : ('!' | '+' | '-') unary
            | primary
            ;
primary     : INT_LIT
            | FLOAT_LIT
            | STRING
            | ID
            | '(' expr ')'
            ;

// ---------- Lexer ----------
ID          : [a-zA-Z_][a-zA-Z0-9_]* ;

FLOAT_LIT   : DIGIT+ '.' DIGIT+ ;
INT_LIT     : DIGIT+ ;

STRING      : '"' ( ~["\\] | '\\' . )* '"' ;

WS          : [ \t\r\n]+ -> skip ;
LINE_COMMENT: '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT: '/*' .*? '*/' -> skip ;

fragment DIGIT : [0-9] ;