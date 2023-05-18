!compiler_flags: --dump FRM --exec FRM

!name: Program 1
!code:
fun main(args:arr[10]int):int = (
    # program, ki vrne sestevek fibonaccijevih stevil na mestih v args tabeli
    { sum = 0 },
    { for i = 0, 10, 1 :
        { sum = sum + fib(args[i]) }
    } { where var i:int },
    sum
) { where
    var sum:int
};

fun fib(n:int):int = (
    # funkcija, ki vrne n-to fibonaccijevo stevilo
    { return = 0 },
    { if n == 1 | n == 2 then
        {return = 1}
    else (
        { return = fib(n-1) + fib(n-2) }
    )
    },
    return
) { where
    var return:int
};

typ int:integer;
typ str:string;
typ bool:logical

# konec
!expected:
Defs [1:1-28:17]
  FunDef [1:1-10:2]: main
    # typed as: (ARR(10,int)) -> int
    # framed as: FRAME [main]: level=1,locals_size=8,arguments_size=8,parameters_size=8,size=20
    Parameter [1:10-1:25]: args
      # typed as: ARR(10,int)
      # accessed as: Parameter: size[4],offset[4],sl[1]
      Array [1:15-1:25]
        # typed as: ARR(10,int)
        [10]
        TypeName [1:22-1:25]: int
          # defined at: [26:1-26:16]
          # typed as: int
    TypeName [1:27-1:30]: int
      # defined at: [26:1-26:16]
      # typed as: int
    Where [1:33-10:2]
      # typed as: int
      Defs [9:5-9:16]
        VarDef [9:5-9:16]: sum
          # typed as: int
          # accessed as: Local: size[4],offset[-4],sl[1]
          TypeName [9:13-9:16]: int
            # defined at: [26:1-26:16]
            # typed as: int
      Block [1:33-8:2]
        # typed as: int
        Binary [3:5-3:16]: ASSIGN
          # typed as: int
          Name [3:7-3:10]: sum
            # defined at: [9:5-9:16]
            # typed as: int
          Literal [3:13-3:14]: INT(0)
            # typed as: int
        Where [4:5-6:26]
          # typed as: void
          Defs [6:15-6:24]
            VarDef [6:15-6:24]: i
              # typed as: int
              # accessed as: Local: size[4],offset[-8],sl[1]
              TypeName [6:21-6:24]: int
                # defined at: [26:1-26:16]
                # typed as: int
          For [4:5-6:6]
            # typed as: void
            Name [4:11-4:12]: i
              # defined at: [6:15-6:24]
              # typed as: int
            Literal [4:15-4:16]: INT(0)
              # typed as: int
            Literal [4:18-4:20]: INT(10)
              # typed as: int
            Literal [4:22-4:23]: INT(1)
              # typed as: int
            Binary [5:9-5:37]: ASSIGN
              # typed as: int
              Name [5:11-5:14]: sum
                # defined at: [9:5-9:16]
                # typed as: int
              Binary [5:17-5:35]: ADD
                # typed as: int
                Name [5:17-5:20]: sum
                  # defined at: [9:5-9:16]
                  # typed as: int
                Call [5:23-5:35]: fib
                  # defined at: [12:1-24:2]
                  # typed as: int
                  Binary [5:27-5:34]: ARR
                    # typed as: int
                    Name [5:27-5:31]: args
                      # defined at: [1:10-1:25]
                      # typed as: ARR(10,int)
                    Name [5:32-5:33]: i
                      # defined at: [6:15-6:24]
                      # typed as: int
        Name [7:5-7:8]: sum
          # defined at: [9:5-9:16]
          # typed as: int
  FunDef [12:1-24:2]: fib
    # typed as: (int) -> int
    # framed as: FRAME [fib]: level=1,locals_size=4,arguments_size=8,parameters_size=8,size=16
    Parameter [12:9-12:14]: n
      # typed as: int
      # accessed as: Parameter: size[4],offset[4],sl[1]
      TypeName [12:11-12:14]: int
        # defined at: [26:1-26:16]
        # typed as: int
    TypeName [12:16-12:19]: int
      # defined at: [26:1-26:16]
      # typed as: int
    Where [12:22-24:2]
      # typed as: int
      Defs [23:5-23:19]
        VarDef [23:5-23:19]: return
          # typed as: int
          # accessed as: Local: size[4],offset[-4],sl[1]
          TypeName [23:16-23:19]: int
            # defined at: [26:1-26:16]
            # typed as: int
      Block [12:22-22:2]
        # typed as: int
        Binary [14:5-14:19]: ASSIGN
          # typed as: int
          Name [14:7-14:13]: return
            # defined at: [23:5-23:19]
            # typed as: int
          Literal [14:16-14:17]: INT(0)
            # typed as: int
        IfThenElse [15:5-20:6]
          # typed as: void
          Binary [15:10-15:25]: OR
            # typed as: log
            Binary [15:10-15:16]: EQ
              # typed as: log
              Name [15:10-15:11]: n
                # defined at: [12:9-12:14]
                # typed as: int
              Literal [15:15-15:16]: INT(1)
                # typed as: int
            Binary [15:19-15:25]: EQ
              # typed as: log
              Name [15:19-15:20]: n
                # defined at: [12:9-12:14]
                # typed as: int
              Literal [15:24-15:25]: INT(2)
                # typed as: int
          Binary [16:9-16:21]: ASSIGN
            # typed as: int
            Name [16:10-16:16]: return
              # defined at: [23:5-23:19]
              # typed as: int
            Literal [16:19-16:20]: INT(1)
              # typed as: int
          Block [17:10-19:6]
            # typed as: int
            Binary [18:9-18:41]: ASSIGN
              # typed as: int
              Name [18:11-18:17]: return
                # defined at: [23:5-23:19]
                # typed as: int
              Binary [18:20-18:39]: ADD
                # typed as: int
                Call [18:20-18:28]: fib
                  # defined at: [12:1-24:2]
                  # typed as: int
                  Binary [18:24-18:27]: SUB
                    # typed as: int
                    Name [18:24-18:25]: n
                      # defined at: [12:9-12:14]
                      # typed as: int
                    Literal [18:26-18:27]: INT(1)
                      # typed as: int
                Call [18:31-18:39]: fib
                  # defined at: [12:1-24:2]
                  # typed as: int
                  Binary [18:35-18:38]: SUB
                    # typed as: int
                    Name [18:35-18:36]: n
                      # defined at: [12:9-12:14]
                      # typed as: int
                    Literal [18:37-18:38]: INT(2)
                      # typed as: int
        Name [21:5-21:11]: return
          # defined at: [23:5-23:19]
          # typed as: int
  TypeDef [26:1-26:16]: int
    # typed as: int
    Atom [26:9-26:16]: INT
      # typed as: int
  TypeDef [27:1-27:15]: str
    # typed as: str
    Atom [27:9-27:15]: STR
      # typed as: str
  TypeDef [28:1-28:17]: bool
    # typed as: log
    Atom [28:10-28:17]: LOG
      # typed as: log
!end

!name: Program 2
!code:
fun main(args:arr[10]int):int = (
    # sesteje vsa stevila v argumentih
    {sum = 0},
    {for i = 0, 10, 1:
        {sum = sum + args[i]}
    },
    sum
) {where
    var sum:int;
    var i:int
};

typ int:integer;
typ str:string;
typ bool:logical
!expected:
Defs [1:1-15:17]
  FunDef [1:1-11:2]: main
    # typed as: (ARR(10,int)) -> int
    # framed as: FRAME [main]: level=1,locals_size=8,arguments_size=0,parameters_size=8,size=16
    Parameter [1:10-1:25]: args
      # typed as: ARR(10,int)
      # accessed as: Parameter: size[4],offset[4],sl[1]
      Array [1:15-1:25]
        # typed as: ARR(10,int)
        [10]
        TypeName [1:22-1:25]: int
          # defined at: [13:1-13:16]
          # typed as: int
    TypeName [1:27-1:30]: int
      # defined at: [13:1-13:16]
      # typed as: int
    Where [1:33-11:2]
      # typed as: int
      Defs [9:5-10:14]
        VarDef [9:5-9:16]: sum
          # typed as: int
          # accessed as: Local: size[4],offset[-4],sl[1]
          TypeName [9:13-9:16]: int
            # defined at: [13:1-13:16]
            # typed as: int
        VarDef [10:5-10:14]: i
          # typed as: int
          # accessed as: Local: size[4],offset[-8],sl[1]
          TypeName [10:11-10:14]: int
            # defined at: [13:1-13:16]
            # typed as: int
      Block [1:33-8:2]
        # typed as: int
        Binary [3:5-3:14]: ASSIGN
          # typed as: int
          Name [3:6-3:9]: sum
            # defined at: [9:5-9:16]
            # typed as: int
          Literal [3:12-3:13]: INT(0)
            # typed as: int
        For [4:5-6:6]
          # typed as: void
          Name [4:10-4:11]: i
            # defined at: [10:5-10:14]
            # typed as: int
          Literal [4:14-4:15]: INT(0)
            # typed as: int
          Literal [4:17-4:19]: INT(10)
            # typed as: int
          Literal [4:21-4:22]: INT(1)
            # typed as: int
          Binary [5:9-5:30]: ASSIGN
            # typed as: int
            Name [5:10-5:13]: sum
              # defined at: [9:5-9:16]
              # typed as: int
            Binary [5:16-5:29]: ADD
              # typed as: int
              Name [5:16-5:19]: sum
                # defined at: [9:5-9:16]
                # typed as: int
              Binary [5:22-5:29]: ARR
                # typed as: int
                Name [5:22-5:26]: args
                  # defined at: [1:10-1:25]
                  # typed as: ARR(10,int)
                Name [5:27-5:28]: i
                  # defined at: [10:5-10:14]
                  # typed as: int
        Name [7:5-7:8]: sum
          # defined at: [9:5-9:16]
          # typed as: int
  TypeDef [13:1-13:16]: int
    # typed as: int
    Atom [13:9-13:16]: INT
      # typed as: int
  TypeDef [14:1-14:15]: str
    # typed as: str
    Atom [14:9-14:15]: STR
      # typed as: str
  TypeDef [15:1-15:17]: bool
    # typed as: log
    Atom [15:10-15:17]: LOG
      # typed as: log
!end
