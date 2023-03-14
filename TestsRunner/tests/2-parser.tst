!compiler_flags: --dump SYN --exec SYN
-- prva vrstica v datoteki je namenjena
-- konfiguraciji prevajalnika

-- -------------------------------------

-- struktura 'delujočega' testa
-- !name: -- značka !name ni obvezna
-- !code:
-- 
-- !expected:
-- 
-- !end

-- struktura 'nedelujočega' testa
-- !name:
-- !code:
-- 
-- !failure:
-- 99 -- izhodna koda programa
-- !end

-- ------------------------------------

!name: Type Definition
!code:
typ neki : string
!expected: 
source -> definitions
definitions -> definition definitions_1
definition -> type_definition
type_definition -> typ id : type
type -> string
definitions_1 -> ε
!end


!name: Variable Arr Definition
!code:
var neki : arr [ 10 ] string
!expected: 
source -> definitions
definitions -> definition definitions_1
definition -> variable_definition
variable_definition -> var id : type
type -> arr [ integer_const ] type
type -> string
definitions_1 -> ε
!end

!name: Function Compare Statement
!code:
fun id ( neki : integer ) : logical = neki == 99
!expected: 
source -> definitions
definitions -> definition definitions_1
definition -> function_definition
function_definition -> fun id '(' parameters ')' ':' type '=' expression
parameters -> parameter parameters_1
parameter -> id : type
type -> integer
parameters_1 -> ε
type -> logical
expression -> logical_ior_expression expression_1
logical_ior_expression -> logical_and_expression logical_ior_expression_1
logical_and_expression -> compare_expression logical_and_expression_1
compare_expression -> additive_expr compare_expression_1
additive_expr -> multiplicative_expression additive_expression_1
multiplicative_expression -> prefix_expression multiplicative_expression_1
prefix_expression -> postfix_expression
postfix_expression -> atom_expression postfix_expression_1
atom_expression -> id atom_expression_id
atom_expression_id -> ε
postfix_expression_1 -> ε
multiplicative_expression_1 -> ε
additive_expression_1 -> ε
compare_expression_1 -> '==' additive_expr
additive_expr -> multiplicative_expression additive_expression_1
multiplicative_expression -> prefix_expression multiplicative_expression_1
prefix_expression -> postfix_expression
postfix_expression -> atom_expression postfix_expression_1
atom_expression -> integer_const
postfix_expression_1 -> ε
multiplicative_expression_1 -> ε
additive_expression_1 -> ε
logical_and_expression_1 -> ε
logical_ior_expression_1 -> ε
expression_1 -> ε
definitions_1 -> ε
!end


