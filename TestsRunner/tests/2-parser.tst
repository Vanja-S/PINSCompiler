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

!name: program1
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