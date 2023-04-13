!compiler_flags: --dump TYP --exec TYP

!name: Program 1: Basic VarDef
!code:
var y : logical;
var x : integer
!expected:
Defs [1:1-2:16]
  VarDef [1:1-1:16]: y
    # typed as: log
    Atom [1:9-1:16]: LOG
      # typed as: log
  VarDef [2:1-2:16]: x
    # typed as: int
    Atom [2:9-2:16]: INT
      # typed as: int
!end


!name: Program 2: Basic TypeDef
!code:
typ log : logical;
typ int : integer;
var y : int;
var x : log
!expected:
Defs [1:1-4:12]
  TypeDef [1:1-1:18]: log
    # typed as: log
    Atom [1:11-1:18]: LOG
      # typed as: log
  TypeDef [2:1-2:18]: int
    # typed as: int
    Atom [2:11-2:18]: INT
      # typed as: int
  VarDef [3:1-3:12]: y
    # typed as: int
    TypeName [3:9-3:12]: int
      # defined at: [2:1-2:18]
      # typed as: int
  VarDef [4:1-4:12]: x
    # typed as: log
    TypeName [4:9-4:12]: log
      # defined at: [1:1-1:18]
      # typed as: log
!end


!name: Program 3: Basic TypeDef and VarDef with Array
!code:
typ log : logical;
typ int : integer;
var y : int;
var x : arr [20] int
!expected:
Defs [1:1-4:21]
  TypeDef [1:1-1:18]: log
    # typed as: log
    Atom [1:11-1:18]: LOG
      # typed as: log
  TypeDef [2:1-2:18]: int
    # typed as: int
    Atom [2:11-2:18]: INT
      # typed as: int
  VarDef [3:1-3:12]: y
    # typed as: int
    TypeName [3:9-3:12]: int
      # defined at: [2:1-2:18]
      # typed as: int
  VarDef [4:1-4:21]: x
    # typed as: ARR(20,int)
    Array [4:9-4:21]
      # typed as: ARR(20,int)
      [20]
      TypeName [4:18-4:21]: int
        # defined at: [2:1-2:18]
        # typed as: int
!end


!name: Program 4: Basic FunDef
!code:
typ int : integer;
fun f (x: int) : int = 10
!expected:
Defs [1:1-2:26]
  TypeDef [1:1-1:18]: int
    # typed as: int
    Atom [1:11-1:18]: INT
      # typed as: int
  FunDef [2:1-2:26]: f
    # typed as: (int) -> int
    Parameter [2:8-2:14]: x
      # typed as: int
      TypeName [2:11-2:14]: int
        # defined at: [1:1-1:18]
        # typed as: int
    TypeName [2:18-2:21]: int
      # defined at: [1:1-1:18]
      # typed as: int
    Literal [2:24-2:26]: INT(10)
      # typed as: int
!end


!name: Program 5: Basic FunDef with Name return
!code:
typ int : integer;
fun f (x: int, y: int) : int = x
!expected:
Defs [1:1-2:33]
  TypeDef [1:1-1:18]: int
    # typed as: int
    Atom [1:11-1:18]: INT
      # typed as: int
  FunDef [2:1-2:33]: f
    # typed as: (int, int) -> int
    Parameter [2:8-2:14]: x
      # typed as: int
      TypeName [2:11-2:14]: int
        # defined at: [1:1-1:18]
        # typed as: int
    Parameter [2:16-2:22]: y
      # typed as: int
      TypeName [2:19-2:22]: int
        # defined at: [1:1-1:18]
        # typed as: int
    TypeName [2:26-2:29]: int
      # defined at: [1:1-1:18]
      # typed as: int
    Name [2:32-2:33]: x
      # defined at: [2:8-2:14]
      # typed as: int
!end


!name: Program 6: Basic FunDef with ADD Binary expression
!code:
typ int : integer;
fun f (x: int, y: int) : int = x + y
!expected:
Defs [1:1-2:37]
  TypeDef [1:1-1:18]: int
    # typed as: int
    Atom [1:11-1:18]: INT
      # typed as: int
  FunDef [2:1-2:37]: f
    # typed as: (int, int) -> int
    Parameter [2:8-2:14]: x
      # typed as: int
      TypeName [2:11-2:14]: int
        # defined at: [1:1-1:18]
        # typed as: int
    Parameter [2:16-2:22]: y
      # typed as: int
      TypeName [2:19-2:22]: int
        # defined at: [1:1-1:18]
        # typed as: int
    TypeName [2:26-2:29]: int
      # defined at: [1:1-1:18]
      # typed as: int
    Binary [2:32-2:37]: ADD
      # typed as: int
      Name [2:32-2:33]: x
        # defined at: [2:8-2:14]
        # typed as: int
      Name [2:36-2:37]: y
        # defined at: [2:16-2:22]
        # typed as: int
!end


!name: Program 7: Basic FunDef with wrong ADD Binary expression
!code:
typ int : integer;
fun f (x: int, y: string) : int = x + y
!failure:
99
!end


!name: Program 8: Basic FunDef with wrong SUB Binary expression
!code:
typ int : integer;
fun f (x: int, y: string) : int = x - y
!failure:
99
!end


!name: Program 9: Basic FunDef with SUB Binary expression
!code:
typ int : integer;
fun f (x: int, y: int) : int = x - y
!expected:
Defs [1:1-2:37]
  TypeDef [1:1-1:18]: int
    # typed as: int
    Atom [1:11-1:18]: INT
      # typed as: int
  FunDef [2:1-2:37]: f
    # typed as: (int, int) -> int
    Parameter [2:8-2:14]: x
      # typed as: int
      TypeName [2:11-2:14]: int
        # defined at: [1:1-1:18]
        # typed as: int
    Parameter [2:16-2:22]: y
      # typed as: int
      TypeName [2:19-2:22]: int
        # defined at: [1:1-1:18]
        # typed as: int
    TypeName [2:26-2:29]: int
      # defined at: [1:1-1:18]
      # typed as: int
    Binary [2:32-2:37]: SUB
      # typed as: int
      Name [2:32-2:33]: x
        # defined at: [2:8-2:14]
        # typed as: int
      Name [2:36-2:37]: y
        # defined at: [2:16-2:22]
        # typed as: int
!end


!name: Program 10: Basic FunDef with wrong return type (logical)
!code:
typ int : integer;
typ log : logical;
fun f (x: log, y: log) : int = x & y
!failure:
99
!end


!name: Program 11: Basic FunDef with wrong return type (ARR(10, int))
!code:
typ int : integer;
typ log : logical;
var neki : arr [10] arr [10] int;
fun f (x: arr [10] arr [10] int, y: log) : int = x[0]
!failure:
99
!end


!name: Program 12: Basic FunDef with ARR return type
!code:
typ int : integer;
typ log : logical;
var neki : arr [10] arr [10] int;
fun f (x: arr [10] arr [10] int, y: log) : arr [10] int = x[0]
!expected:
Defs [1:1-4:63]
  TypeDef [1:1-1:18]: int
    # typed as: int
    Atom [1:11-1:18]: INT
      # typed as: int
  TypeDef [2:1-2:18]: log
    # typed as: log
    Atom [2:11-2:18]: LOG
      # typed as: log
  VarDef [3:1-3:33]: neki
    # typed as: ARR(10,ARR(10,int))
    Array [3:12-3:33]
      # typed as: ARR(10,ARR(10,int))
      [10]
      Array [3:21-3:33]
        # typed as: ARR(10,int)
        [10]
        TypeName [3:30-3:33]: int
          # defined at: [1:1-1:18]
          # typed as: int
  FunDef [4:1-4:63]: f
    # typed as: (ARR(10,ARR(10,int)), log) -> ARR(10,int)
    Parameter [4:8-4:32]: x
      # typed as: ARR(10,ARR(10,int))
      Array [4:11-4:32]
        # typed as: ARR(10,ARR(10,int))
        [10]
        Array [4:20-4:32]
          # typed as: ARR(10,int)
          [10]
          TypeName [4:29-4:32]: int
            # defined at: [1:1-1:18]
            # typed as: int
    Parameter [4:34-4:40]: y
      # typed as: log
      TypeName [4:37-4:40]: log
        # defined at: [2:1-2:18]
        # typed as: log
    Array [4:44-4:56]
      # typed as: ARR(10,int)
      [10]
      TypeName [4:53-4:56]: int
        # defined at: [1:1-1:18]
        # typed as: int
    Binary [4:59-4:63]: ARR
      # typed as: ARR(10,int)
      Name [4:59-4:60]: x
        # defined at: [4:8-4:32]
        # typed as: ARR(10,ARR(10,int))
      Literal [4:61-4:62]: INT(0)
        # typed as: int
!end