!compiler_flags: --dump TYP --exec TYP



!name: Program 1
!code:
typ x: y;
typ y: x
!failure:
99
!end

!name: Program 2
!code:
typ x: y;
typ y: z;
typ z: n;
var m: integer;
typ n: y
!failure:
99
!end

!name: Program 3
!code:
fun f(x: integer, y: string): string = x + y
!failure:
99
!end

!name: Program 4
!code:
fun f(x: integer, y: string): logical = x + y
!failure:
99
!end

!name: Program 5
!code:
fun f(x: integer, y: string): arr[10] arr[20] integer = x + y
!failure:
99
!end

!name: Program 6
!code:
typ x: z;
fun f(x: z, y: string): integer = x + y;
typ z: string
!failure:
99
!end

!name: Program 7
!code:
typ x: z;
fun f(x: z, y: string): integer = x + y;
typ z: n;
typ n: x
!failure:
99
!end

!name: Program 8
!code:
fun f(x: string, y: string): string = x + y
!failure:
99
!end

!name: Program 9
!code:
fun f(x: string, y: string): string = x * y
!failure:
99
!end

!name: Program 10
!code:
fun f(x: string, y: string): string = x / y
!failure:
99
!end

!name: Program 11
!code:
fun f(x: string, y: string): string = x % y
!failure:
99
!end


!name: Program 12
!code:
fun f(x: logical, y: logical): string = x + y
!failure:
99
!end

!name: Program 13
!code:
fun f(x: logical, y: logical): string = x * y
!failure:
99
!end

!name: Program 14
!code:
fun f(x: logical, y: logical): string = x / y
!failure:
99
!end

!name: Program 15
!code:
fun f(x: logical, y: logical): string = x == y
!failure:
99
!end

!name: Program 16
!code:
fun f(x: logical, y: logical): string = x >= y
!failure:
99
!end

!name: Program 17
!code:
fun f(x: logical, y: logical): string = x <= y
!failure:
99
!end

!name: Program 18
!code:
fun f(x: logical, y: logical): string = x < y
!failure:
99
!end

!name: Program 19
!code:
fun f(x: logical, y: logical): string = x > y
!failure:
99
!end

!name: Program 20
!code:
fun f(x: logical, y: logical): string = x != y
!failure:
99
!end

!name: Program 21
!code:
fun f(x: logical, y: string): logical = x != y
!failure:
99
!end

!name: Program 22
!code:
fun f(x: logical, y: integer): logical = x != y
!failure:
99
!end

!name: Program 23
!code:
fun f(x: integer, y: integer): integer = x != y
!failure:
99
!end

!name: Program 24
!code:
fun f(x: integer, y: integer): integer = !x
!failure:
99
!end

!name: Program 25
!code:
fun f(x: integer, y: integer): logical = +x-y
!failure:
99
!end

!name: Program 26
!code:
fun f(x: logical, y: logical): logical = +x-y
!failure:
99
!end

!name: Program 27
!code:
fun f(x: logical, y: integer): logical = x&y
!failure:
99
!end

!name: Program 28
!code:
fun f(x: logical, y: logical): string = x&y
!failure:
99
!end

!name: Program 29
!code:
fun f(x: logical, y: logical): string = x|y
!failure:
99
!end

!name: Program 30
!code:
fun f(x: logical, y: logical): string = !x|y-y+x*y
!failure:
99
!end

!name: Program 31
!code:
typ k: integer;
fun f(x: logical, y: integer, z: arr[10] k): integer = y[10]
!failure:
99
!end

!name: Program 32
!code:
typ k: integer;
fun f(x: logical, y: integer, z: arr[10] k): integer = z[10] + x
!failure:
99
!end!

!name: Program 33
!code:
fun f(x:integer, y:integer): integer = g(x);
fun g(x:integer, y:integer): integer = f(x)
!failure:
99
!end

!name: Program 34
!code:
fun f(x:integer, y:integer): integer = g(x, y);
fun g(x:integer, y:string): integer = f(x,y)
!failure:
99
!end

!name: Program 35
!code:
fun f(x:integer, y:integer): logical = g(x, y);
fun g(x:integer, y:integer): integer = f(x,y)
!failure:
99
!end

!name: Program 36
!code:
fun f(x:integer, y:integer): integer = g(x, y);
fun g(x:integer, y:integer): integer = f(x,y);
fun h(z: integer, y:string): logical = f(z,z)
!failure:
99
!end

!name: Program 37
!code:
fun f(x: integer, y: integer): string = {x = y}
!failure:
99
!end

!name: Program 38
!code:
fun f(x: integer, y: logical): integer = {x = y}
!failure:
99
!end

!name: Program 39
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = { while x>10: k }
!failure:
99
!end

!name: Program 40
!code:
fun f(x: string, y: string): string = x / y
!failure:
99
!end

!name: Program 41
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({ while k : k }, x)
!failure:
99
!end

!name: Program 42
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({ while z : +z }, x)
!failure:
99
!end

!name: Program 43
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = (x, x+x+x-x, k)
!failure:
99
!end

!name: Program 44
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({if x==k then x},x)
!failure:
99
!end

!name: Program 45
!code:
fun f(x: string, y: string): string = x * y
!failure:
99
!end

!name: Program 46
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({if z==z then x else f(x,y,z)},x)
!failure:
99
!end

!name: Program 47
!code:
fun f(x: string, y: string): string = x % y
!failure:
99
!end

!name: Program 48
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({for y=x,x,x:x},x)
!failure:
99
!end

!name: Program 49
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({for x=z,x,x:x},x)
!failure:
99
!end

!name: Program 50
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({for x=x,k,x:x},x)
!failure:
99
!end

!name: Program 51
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({for x=x,x,y:x},x)
!failure:
99
!end

!name: Program 52
!code:
fun f(x: integer, y: logical, z: logical, k: string): integer = ({for x=x,x,x:x==x&k},x)
!failure:
99
!end

!name: Program 53
!code:
fun f(x: integer, y:integer, z: string): string = !z+x+x+x+x
!failure:
99
!end

!name: Program 54
!code:
fun f(x: integer, y:integer, z: string): string = z+z
!failure:
99
!end

!name: Program 55
!code:
fun f(x: string, y: string): string = x / y
!failure:
99
!end

!name: Program 56
!code:
fun f(x: integer, y:integer, z: string): string = x {
where fun g(x: integer, y: integer, z:string): string = z
}
!failure:
99
!end

!name: Program 57
!code:
fun f(x: integer, y:integer, z: string): string = g(x, y, z) {
where fun g(x: integer, y: integer, z:string): string = y
}
!failure:
99
!end

!name: Program 58
!code:
fun f(x: integer, y:integer, z: string): string = g(x, y, z) {
where fun g(x: integer, y: integer, z:string): string = (x, y, x+x+x+x+x&x,z)
}
!failure:
99
!end

!name: Program 59
!code:
fun f(x: integer, y:integer, z: string): string = g(x, y, z) {
where fun g(x: integer, y: integer, z:string): string = (x, y, x+x+x+x+x,z);
fun xyz(x: integer, y:integer):integer = f(x,y)
}
!failure:
99
!end

!name: Program 60
!code:
typ x: arr[10] integer;
fun f(x: x, y: integer): arr[9] integer = x
!failure:
99
!end

!name: Program 61: Basic VarDef
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


!name: Program 62: Basic TypeDef
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


!name: Program 63: Basic TypeDef and VarDef with Array
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


!name: Program 64: Basic FunDef
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


!name: Program 65: Basic FunDef with Name return
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


!name: Program 66: Basic FunDef with ADD Binary expression
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


!name: Program 67: Basic FunDef with wrong ADD Binary expression
!code:
typ int : integer;
fun f (x: int, y: string) : int = x + y
!failure:
99
!end


!name: Program 68: Basic FunDef with wrong SUB Binary expression
!code:
typ int : integer;
fun f (x: int, y: string) : int = x - y
!failure:
99
!end


!name: Program 69: Basic FunDef with SUB Binary expression
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


!name: Program 70: Basic FunDef with wrong return type
!code:
typ int : integer;
typ log : logical;
fun f (x: log, y: log) : int = x & y
!failure:
99
!end

!name: Program 71: Basic FunDef and function call with wrong argument types
!code:
typ int : integer;
typ log : logical;
fun f (x: log, y: log) : log = x & y;
fun g (x: int, y: log) : log = f(x, y)
!failure:
99
!end


!name: Program 72: Basic function recursion
!code:
typ int : integer;
typ log : logical;
fun f (x: int, y: int) : int = {if x != y then f(x + 1, y)}
!expected:
Defs [1:1-3:65]
  TypeDef [1:1-1:18]: int
    # typed as: int
    Atom [1:11-1:18]: INT
      # typed as: int
  TypeDef [2:1-2:18]: log
    # typed as: log
    Atom [2:11-2:18]: LOG
      # typed as: log
  FunDef [3:1-3:65]: f
    # typed as: (int, int) -> int
    Parameter [3:8-3:14]: x
      # typed as: int
      TypeName [3:11-3:14]: int
        # defined at: [1:1-1:18]
        # typed as: int
    Parameter [3:16-3:22]: y
      # typed as: int
      TypeName [3:19-3:22]: int
        # defined at: [1:1-1:18]
        # typed as: int
    TypeName [3:26-3:29]: int
      # defined at: [1:1-1:18]
      # typed as: int
    Block [3:32-3:65]
      # typed as: int
      IfThenElse [3:33-3:61]
        # typed as: void
        Binary [3:37-3:43]: NEQ
          # typed as: log
          Name [3:37-3:38]: x
            # defined at: [3:8-3:14]
            # typed as: int
          Name [3:42-3:43]: y
            # defined at: [3:16-3:22]
            # typed as: int
        Call [3:49-3:60]: f
          # defined at: [3:1-3:65]
          # typed as: (int, int) -> int
          Binary [3:51-3:56]: ADD
            # typed as: int
            Name [3:51-3:52]: x
              # defined at: [3:8-3:14]
              # typed as: int
            Literal [3:55-3:56]: INT(1)
              # typed as: int
          Name [3:58-3:59]: y
            # defined at: [3:16-3:22]
            # typed as: int
      Name [3:63-3:64]: x
        # defined at: [3:8-3:14]
        # typed as: int
!end