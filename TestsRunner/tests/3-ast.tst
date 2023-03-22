!compiler_flags: --dump AST --exec AST

!code:
var x: integer;
typ MyInt: integer;
fun f ( x : logical ) : arr [ 10 ] integer = 0
!expected:
Defs [1:1-3:47]
  VarDef [1:1-1:15]: x
    Atom [1:8-1:15]: INT
  TypeDef [2:1-2:19]: MyInt
    Atom [2:12-2:19]: INT
  FunDef [3:1-3:47]: f
    Parameter [3:9-3:20]: x
      Atom [3:13-3:20]: LOG
    Array [3:25-3:43]
      [10]
      Atom [3:36-3:43]: INT
    Literal [3:46-3:47]: INT(0)
!end

!code:
fun f ( x : logical ) : integer = x + y
!expected:
Defs [1:1-1:40]
  FunDef [1:1-1:40]: f
    Parameter [1:9-1:20]: x
      Atom [1:13-1:20]: LOG
    Atom [1:25-1:32]: INT
    Binary [1:35-1:40]: ADD
      Name [1:35-1:36]: x
      Name [1:39-1:40]: y
!end

!code:
fun funkcija ( x : logical, b : string ) : string = 'abc'
!expected:
Defs [1:1-1:58]
  FunDef [1:1-1:58]: funkcija
    Parameter [1:16-1:27]: x
      Atom [1:20-1:27]: LOG
    Parameter [1:29-1:39]: b
      Atom [1:33-1:39]: STR
    Atom [1:44-1:50]: STR
    Literal [1:53-1:58]: STR(abc)
!end