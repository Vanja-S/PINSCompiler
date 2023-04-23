!compiler_flags: --dump FRM --exec FRM

!code:
####### ULTIMATE TEST PINS 2k69 ######

# Inicializacija
# type_definition
typ x: integer;
typ y: string;
typ z: logical;
typ i: blablabla;
typ j: arr[10] arr[15] arr[20] blablabla;
typ blablabla: integer;
typ blabla: blablabla;
typ void: logical;

# variable_definition
var k: string; var l: integer; var q: logical; var m: blabla; var n: arr[69] arr [420] string;

# Funkcije
# function_definition
fun firstfunction(x: integer, y: string, z: logical): integer =
	( {for i = 1, 100, {i = i + 1}:
		{
			while j > (-10): {
				if j * (-10) + 20 + 30 + 10 - 40 / 50 % 100 -(-(-(-(-(+10))))) != 123 then
				    12
				else
					firstfunction(123, 'bruh', true)
			}
		# komentar komentar komentar ----------------------------------------------
		}	},
		true & false | z & true | (1 >= firstfunction(i, 'monke', true)),
		{
			if 16 > (14 - 10 + 10 * 10 / 10 - 10 + 10 + -10)
				then
					firstfunction(i, 'danes je lep dan', false)
		},

		{
		for i = 1, 10, 12 : (
			{
				while j<10: (j < 10)
			},
			{
			    if j * 10 >= 100 then {a = j - 10 == 12} else 0
			},
			{
                for j = 1, 12, {j=j*2}: firstfunction(12, 'blablabla', true)
            },
			{
                p[i+10-20*30][j-10-203] = 12
            })
		},
        12
	)
{ where
    var i:integer;
    var a: logical;
    var j: integer;
    var p: arr[69] arr[420] integer;
    fun secondfunction(x: integer):integer =
        (
            (10+20) >= (30-40) & 12 == 100, 0)
        { where
            fun thirdfunction(y: string):arr[10] string =
                (
                    {10*20=xyz},
                    ss
                )
                { where
                    var xyz: integer;
                    var ss : arr[10] string
                }
    };
    fun partition(stevila: arr[10] integer, begin: integer, end: integer): integer =
        (
	        { pivot = stevila[end] },
	        { i = (begin - 1) },
            {
                for j = begin, end, {j = j + 1}:
		            {
                        if stevila[j] <= pivot then
			                (
                                { i = i + 1 },
			                    { swapTemp = stevila[j] },
			                    { stevila[i] = stevila[j] },
			                    { stevila[j] = swapTemp }
                            )
		            }
            },
            {swapTemp = stevila[i+1]},
            {stevila[i+1] = stevila[end]},
            {stevila[end] = swapTemp},
            (i + 1)
        )
        { where
            var swapTemp: integer;
            var pivot: integer;
            var i: integer;
            var j: integer
    };
    fun izpis(besedilo: string): void =
        (
            print('Tvoj program je prestal celoten preizkus!'),
            true
        );

    fun print(besedilo: string): string = besedilo
}
!expected:
Defs [5:1-107:2]
  TypeDef [5:1-5:15]: x
    # typed as: int
    Atom [5:8-5:15]: INT
      # typed as: int
  TypeDef [6:1-6:14]: y
    # typed as: str
    Atom [6:8-6:14]: STR
      # typed as: str
  TypeDef [7:1-7:15]: z
    # typed as: log
    Atom [7:8-7:15]: LOG
      # typed as: log
  TypeDef [8:1-8:17]: i
    # typed as: int
    TypeName [8:8-8:17]: blablabla
      # defined at: [10:1-10:23]
      # typed as: int
  TypeDef [9:1-9:41]: j
    # typed as: ARR(10,ARR(15,ARR(20,int)))
    Array [9:8-9:41]
      # typed as: ARR(10,ARR(15,ARR(20,int)))
      [10]
      Array [9:16-9:41]
        # typed as: ARR(15,ARR(20,int))
        [15]
        Array [9:24-9:41]
          # typed as: ARR(20,int)
          [20]
          TypeName [9:32-9:41]: blablabla
            # defined at: [10:1-10:23]
            # typed as: int
  TypeDef [10:1-10:23]: blablabla
    # typed as: int
    Atom [10:16-10:23]: INT
      # typed as: int
  TypeDef [11:1-11:22]: blabla
    # typed as: int
    TypeName [11:13-11:22]: blablabla
      # defined at: [10:1-10:23]
      # typed as: int
  TypeDef [12:1-12:18]: void
    # typed as: log
    Atom [12:11-12:18]: LOG
      # typed as: log
  VarDef [15:1-15:14]: k
    # typed as: str
    # accessed as: Global: size[4],label[k]
    Atom [15:8-15:14]: STR
      # typed as: str
  VarDef [15:16-15:30]: l
    # typed as: int
    # accessed as: Global: size[4],label[l]
    Atom [15:23-15:30]: INT
      # typed as: int
  VarDef [15:32-15:46]: q
    # typed as: log
    # accessed as: Global: size[4],label[q]
    Atom [15:39-15:46]: LOG
      # typed as: log
  VarDef [15:48-15:61]: m
    # typed as: int
    # accessed as: Global: size[4],label[m]
    TypeName [15:55-15:61]: blabla
      # defined at: [11:1-11:22]
      # typed as: int
  VarDef [15:63-15:94]: n
    # typed as: ARR(69,ARR(420,str))
    # accessed as: Global: size[115920],label[n]
    Array [15:70-15:94]
      # typed as: ARR(69,ARR(420,str))
      [69]
      Array [15:78-15:94]
        # typed as: ARR(420,str)
        [420]
        Atom [15:88-15:94]: STR
          # typed as: str
  FunDef [19:1-107:2]: firstfunction
    # typed as: (int, str, log) -> int
    # framed as: FRAME [firstfunction]: level=1,locals_size=115932,arguments_size=16,parameters_size=16,size=115952
    Parameter [19:19-19:29]: x
      # typed as: int
      # accessed as: Parameter: size[4],offset[4],sl[1]
      Atom [19:22-19:29]: INT
        # typed as: int
    Parameter [19:31-19:40]: y
      # typed as: str
      # accessed as: Parameter: size[4],offset[8],sl[1]
      Atom [19:34-19:40]: STR
        # typed as: str
    Parameter [19:42-19:52]: z
      # typed as: log
      # accessed as: Parameter: size[4],offset[12],sl[1]
      Atom [19:45-19:52]: LOG
        # typed as: log
    Atom [19:55-19:62]: INT
      # typed as: int
    Where [20:5-107:2]
      # typed as: int
      Defs [55:5-106:51]
        VarDef [55:5-55:18]: i
          # typed as: int
          # accessed as: Local: size[4],offset[-4],sl[1]
          Atom [55:11-55:18]: INT
            # typed as: int
        VarDef [56:5-56:19]: a
          # typed as: log
          # accessed as: Local: size[4],offset[-8],sl[1]
          Atom [56:12-56:19]: LOG
            # typed as: log
        VarDef [57:5-57:19]: j
          # typed as: int
          # accessed as: Local: size[4],offset[-12],sl[1]
          Atom [57:12-57:19]: INT
            # typed as: int
        VarDef [58:5-58:36]: p
          # typed as: ARR(69,ARR(420,int))
          # accessed as: Local: size[115920],offset[-115932],sl[1]
          Array [58:12-58:36]
            # typed as: ARR(69,ARR(420,int))
            [69]
            Array [58:20-58:36]
              # typed as: ARR(420,int)
              [420]
              Atom [58:29-58:36]: INT
                # typed as: int
        FunDef [59:5-72:6]: secondfunction
          # typed as: (int) -> int
          # framed as: FRAME [L[0]]: level=2,locals_size=0,arguments_size=0,parameters_size=8,size=8
          Parameter [59:24-59:34]: x
            # typed as: int
            # accessed as: Parameter: size[4],offset[4],sl[2]
            Atom [59:27-59:34]: INT
              # typed as: int
          Atom [59:36-59:43]: INT
            # typed as: int
          Where [60:9-72:6]
            # typed as: int
            Defs [63:13-71:18]
              FunDef [63:13-71:18]: thirdfunction
                # typed as: (str) -> ARR(10,str)
                # framed as: FRAME [L[1]]: level=3,locals_size=44,arguments_size=0,parameters_size=8,size=52
                Parameter [63:31-63:40]: y
                  # typed as: str
                  # accessed as: Parameter: size[4],offset[4],sl[3]
                  Atom [63:34-63:40]: STR
                    # typed as: str
                Array [63:42-63:56]
                  # typed as: ARR(10,str)
                  [10]
                  Atom [63:50-63:56]: STR
                    # typed as: str
                Where [64:17-71:18]
                  # typed as: ARR(10,str)
                  Defs [69:21-70:44]
                    VarDef [69:21-69:37]: xyz
                      # typed as: int
                      # accessed as: Local: size[4],offset[-4],sl[3]
                      Atom [69:30-69:37]: INT
                        # typed as: int
                    VarDef [70:21-70:44]: ss
                      # typed as: ARR(10,str)
                      # accessed as: Local: size[40],offset[-44],sl[3]
                      Array [70:30-70:44]
                        # typed as: ARR(10,str)
                        [10]
                        Atom [70:38-70:44]: STR
                          # typed as: str
                  Block [64:17-67:18]
                    # typed as: ARR(10,str)
                    Binary [65:21-65:32]: ASSIGN
                      # typed as: int
                      Binary [65:22-65:27]: MUL
                        # typed as: int
                        Literal [65:22-65:24]: INT(10)
                          # typed as: int
                        Literal [65:25-65:27]: INT(20)
                          # typed as: int
                      Name [65:28-65:31]: xyz
                        # defined at: [69:21-69:37]
                        # typed as: int
                    Name [66:21-66:23]: ss
                      # defined at: [70:21-70:44]
                      # typed as: ARR(10,str)
            Block [60:9-61:47]
              # typed as: int
              Binary [61:13-61:43]: AND
                # typed as: log
                Binary [61:13-61:31]: GEQ
                  # typed as: log
                  Block [61:13-61:20]
                    # typed as: int
                    Binary [61:14-61:19]: ADD
                      # typed as: int
                      Literal [61:14-61:16]: INT(10)
                        # typed as: int
                      Literal [61:17-61:19]: INT(20)
                        # typed as: int
                  Block [61:24-61:31]
                    # typed as: int
                    Binary [61:25-61:30]: SUB
                      # typed as: int
                      Literal [61:25-61:27]: INT(30)
                        # typed as: int
                      Literal [61:28-61:30]: INT(40)
                        # typed as: int
                Binary [61:34-61:43]: EQ
                  # typed as: log
                  Literal [61:34-61:36]: INT(12)
                    # typed as: int
                  Literal [61:40-61:43]: INT(100)
                    # typed as: int
              Literal [61:45-61:46]: INT(0)
                # typed as: int
        FunDef [73:5-99:6]: partition
          # typed as: (ARR(10,int), int, int) -> int
          # framed as: FRAME [L[2]]: level=2,locals_size=16,arguments_size=0,parameters_size=16,size=24
          Parameter [73:19-73:43]: stevila
            # typed as: ARR(10,int)
            # accessed as: Parameter: size[4],offset[4],sl[2]
            Array [73:28-73:43]
              # typed as: ARR(10,int)
              [10]
              Atom [73:36-73:43]: INT
                # typed as: int
          Parameter [73:45-73:59]: begin
            # typed as: int
            # accessed as: Parameter: size[4],offset[8],sl[2]
            Atom [73:52-73:59]: INT
              # typed as: int
          Parameter [73:61-73:73]: end
            # typed as: int
            # accessed as: Parameter: size[4],offset[12],sl[2]
            Atom [73:66-73:73]: INT
              # typed as: int
          Atom [73:76-73:83]: INT
            # typed as: int
          Where [74:9-99:6]
            # typed as: int
            Defs [95:13-98:27]
              VarDef [95:13-95:34]: swapTemp
                # typed as: int
                # accessed as: Local: size[4],offset[-4],sl[2]
                Atom [95:27-95:34]: INT
                  # typed as: int
              VarDef [96:13-96:31]: pivot
                # typed as: int
                # accessed as: Local: size[4],offset[-8],sl[2]
                Atom [96:24-96:31]: INT
                  # typed as: int
              VarDef [97:13-97:27]: i
                # typed as: int
                # accessed as: Local: size[4],offset[-12],sl[2]
                Atom [97:20-97:27]: INT
                  # typed as: int
              VarDef [98:13-98:27]: j
                # typed as: int
                # accessed as: Local: size[4],offset[-16],sl[2]
                Atom [98:20-98:27]: INT
                  # typed as: int
            Block [74:9-93:10]
              # typed as: int
              Binary [75:13-75:37]: ASSIGN
                # typed as: int
                Name [75:15-75:20]: pivot
                  # defined at: [96:13-96:31]
                  # typed as: int
                Binary [75:23-75:35]: ARR
                  # typed as: int
                  Name [75:23-75:30]: stevila
                    # defined at: [73:19-73:43]
                    # typed as: ARR(10,int)
                  Name [75:31-75:34]: end
                    # defined at: [73:61-73:73]
                    # typed as: int
              Binary [76:13-76:32]: ASSIGN
                # typed as: int
                Name [76:15-76:16]: i
                  # defined at: [97:13-97:27]
                  # typed as: int
                Block [76:19-76:30]
                  # typed as: int
                  Binary [76:20-76:29]: SUB
                    # typed as: int
                    Name [76:20-76:25]: begin
                      # defined at: [73:45-73:59]
                      # typed as: int
                    Literal [76:28-76:29]: INT(1)
                      # typed as: int
              For [77:13-88:14]
                # typed as: void
                Name [78:21-78:22]: j
                  # defined at: [98:13-98:27]
                  # typed as: int
                Name [78:25-78:30]: begin
                  # defined at: [73:45-73:59]
                  # typed as: int
                Name [78:32-78:35]: end
                  # defined at: [73:61-73:73]
                  # typed as: int
                Binary [78:37-78:48]: ASSIGN
                  # typed as: int
                  Name [78:38-78:39]: j
                    # defined at: [98:13-98:27]
                    # typed as: int
                  Binary [78:42-78:47]: ADD
                    # typed as: int
                    Name [78:42-78:43]: j
                      # defined at: [98:13-98:27]
                      # typed as: int
                    Literal [78:46-78:47]: INT(1)
                      # typed as: int
                IfThenElse [79:21-87:22]
                  # typed as: void
                  Binary [80:28-80:47]: LEQ
                    # typed as: log
                    Binary [80:28-80:38]: ARR
                      # typed as: int
                      Name [80:28-80:35]: stevila
                        # defined at: [73:19-73:43]
                        # typed as: ARR(10,int)
                      Name [80:36-80:37]: j
                        # defined at: [98:13-98:27]
                        # typed as: int
                    Name [80:42-80:47]: pivot
                      # defined at: [96:13-96:31]
                      # typed as: int
                  Block [81:29-86:30]
                    # typed as: int
                    Binary [82:33-82:46]: ASSIGN
                      # typed as: int
                      Name [82:35-82:36]: i
                        # defined at: [97:13-97:27]
                        # typed as: int
                      Binary [82:39-82:44]: ADD
                        # typed as: int
                        Name [82:39-82:40]: i
                          # defined at: [97:13-97:27]
                          # typed as: int
                        Literal [82:43-82:44]: INT(1)
                          # typed as: int
                    Binary [83:33-83:58]: ASSIGN
                      # typed as: int
                      Name [83:35-83:43]: swapTemp
                        # defined at: [95:13-95:34]
                        # typed as: int
                      Binary [83:46-83:56]: ARR
                        # typed as: int
                        Name [83:46-83:53]: stevila
                          # defined at: [73:19-73:43]
                          # typed as: ARR(10,int)
                        Name [83:54-83:55]: j
                          # defined at: [98:13-98:27]
                          # typed as: int
                    Binary [84:33-84:60]: ASSIGN
                      # typed as: int
                      Binary [84:35-84:45]: ARR
                        # typed as: int
                        Name [84:35-84:42]: stevila
                          # defined at: [73:19-73:43]
                          # typed as: ARR(10,int)
                        Name [84:43-84:44]: i
                          # defined at: [97:13-97:27]
                          # typed as: int
                      Binary [84:48-84:58]: ARR
                        # typed as: int
                        Name [84:48-84:55]: stevila
                          # defined at: [73:19-73:43]
                          # typed as: ARR(10,int)
                        Name [84:56-84:57]: j
                          # defined at: [98:13-98:27]
                          # typed as: int
                    Binary [85:33-85:58]: ASSIGN
                      # typed as: int
                      Binary [85:35-85:45]: ARR
                        # typed as: int
                        Name [85:35-85:42]: stevila
                          # defined at: [73:19-73:43]
                          # typed as: ARR(10,int)
                        Name [85:43-85:44]: j
                          # defined at: [98:13-98:27]
                          # typed as: int
                      Name [85:48-85:56]: swapTemp
                        # defined at: [95:13-95:34]
                        # typed as: int
              Binary [89:13-89:38]: ASSIGN
                # typed as: int
                Name [89:14-89:22]: swapTemp
                  # defined at: [95:13-95:34]
                  # typed as: int
                Binary [89:25-89:37]: ARR
                  # typed as: int
                  Name [89:25-89:32]: stevila
                    # defined at: [73:19-73:43]
                    # typed as: ARR(10,int)
                  Binary [89:33-89:36]: ADD
                    # typed as: int
                    Name [89:33-89:34]: i
                      # defined at: [97:13-97:27]
                      # typed as: int
                    Literal [89:35-89:36]: INT(1)
                      # typed as: int
              Binary [90:13-90:42]: ASSIGN
                # typed as: int
                Binary [90:14-90:26]: ARR
                  # typed as: int
                  Name [90:14-90:21]: stevila
                    # defined at: [73:19-73:43]
                    # typed as: ARR(10,int)
                  Binary [90:22-90:25]: ADD
                    # typed as: int
                    Name [90:22-90:23]: i
                      # defined at: [97:13-97:27]
                      # typed as: int
                    Literal [90:24-90:25]: INT(1)
                      # typed as: int
                Binary [90:29-90:41]: ARR
                  # typed as: int
                  Name [90:29-90:36]: stevila
                    # defined at: [73:19-73:43]
                    # typed as: ARR(10,int)
                  Name [90:37-90:40]: end
                    # defined at: [73:61-73:73]
                    # typed as: int
              Binary [91:13-91:38]: ASSIGN
                # typed as: int
                Binary [91:14-91:26]: ARR
                  # typed as: int
                  Name [91:14-91:21]: stevila
                    # defined at: [73:19-73:43]
                    # typed as: ARR(10,int)
                  Name [91:22-91:25]: end
                    # defined at: [73:61-73:73]
                    # typed as: int
                Name [91:29-91:37]: swapTemp
                  # defined at: [95:13-95:34]
                  # typed as: int
              Block [92:13-92:20]
                # typed as: int
                Binary [92:14-92:19]: ADD
                  # typed as: int
                  Name [92:14-92:15]: i
                    # defined at: [97:13-97:27]
                    # typed as: int
                  Literal [92:18-92:19]: INT(1)
                    # typed as: int
        FunDef [100:5-104:10]: izpis
          # typed as: (str) -> log
          # framed as: FRAME [L[3]]: level=2,locals_size=0,arguments_size=8,parameters_size=8,size=12
          Parameter [100:15-100:31]: besedilo
            # typed as: str
            # accessed as: Parameter: size[4],offset[4],sl[2]
            Atom [100:25-100:31]: STR
              # typed as: str
          TypeName [100:34-100:38]: void
            # defined at: [12:1-12:18]
            # typed as: log
          Block [101:9-104:10]
            # typed as: log
            Call [102:13-102:63]: print
              # defined at: [106:5-106:51]
              # typed as: str
              Literal [102:19-102:62]: STR(Tvoj program je prestal celoten preizkus!)
                # typed as: str
            Literal [103:13-103:17]: LOG(true)
              # typed as: log
        FunDef [106:5-106:51]: print
          # typed as: (str) -> str
          # framed as: FRAME [L[4]]: level=2,locals_size=0,arguments_size=0,parameters_size=8,size=8
          Parameter [106:15-106:31]: besedilo
            # typed as: str
            # accessed as: Parameter: size[4],offset[4],sl[2]
            Atom [106:25-106:31]: STR
              # typed as: str
          Atom [106:34-106:40]: STR
            # typed as: str
          Name [106:43-106:51]: besedilo
            # defined at: [106:15-106:31]
            # typed as: str
      Block [20:5-53:6]
        # typed as: int
        For [20:7-29:15]
          # typed as: void
          Name [20:12-20:13]: i
            # defined at: [55:5-55:18]
            # typed as: int
          Literal [20:16-20:17]: INT(1)
            # typed as: int
          Literal [20:19-20:22]: INT(100)
            # typed as: int
          Binary [20:24-20:35]: ASSIGN
            # typed as: int
            Name [20:25-20:26]: i
              # defined at: [55:5-55:18]
              # typed as: int
            Binary [20:29-20:34]: ADD
              # typed as: int
              Name [20:29-20:30]: i
                # defined at: [55:5-55:18]
                # typed as: int
              Literal [20:33-20:34]: INT(1)
                # typed as: int
          While [21:9-29:10]
            # typed as: void
            Binary [22:19-22:28]: GT
              # typed as: log
              Name [22:19-22:20]: j
                # defined at: [57:5-57:19]
                # typed as: int
              Block [22:23-22:28]
                # typed as: int
                Unary [22:24-22:27]: SUB
                  # typed as: int
                  Literal [22:25-22:27]: INT(10)
                    # typed as: int
            IfThenElse [22:30-27:14]
              # typed as: void
              Binary [23:20-23:86]: NEQ
                # typed as: log
                Binary [23:20-23:79]: SUB
                  # typed as: int
                  Binary [23:20-23:60]: SUB
                    # typed as: int
                    Binary [23:20-23:44]: ADD
                      # typed as: int
                      Binary [23:20-23:39]: ADD
                        # typed as: int
                        Binary [23:20-23:34]: ADD
                          # typed as: int
                          Binary [23:20-23:29]: MUL
                            # typed as: int
                            Name [23:20-23:21]: j
                              # defined at: [57:5-57:19]
                              # typed as: int
                            Block [23:24-23:29]
                              # typed as: int
                              Unary [23:25-23:28]: SUB
                                # typed as: int
                                Literal [23:26-23:28]: INT(10)
                                  # typed as: int
                          Literal [23:32-23:34]: INT(20)
                            # typed as: int
                        Literal [23:37-23:39]: INT(30)
                          # typed as: int
                      Literal [23:42-23:44]: INT(10)
                        # typed as: int
                    Binary [23:47-23:60]: MOD
                      # typed as: int
                      Binary [23:47-23:54]: DIV
                        # typed as: int
                        Literal [23:47-23:49]: INT(40)
                          # typed as: int
                        Literal [23:52-23:54]: INT(50)
                          # typed as: int
                      Literal [23:57-23:60]: INT(100)
                        # typed as: int
                  Block [23:62-23:79]
                    # typed as: int
                    Unary [23:63-23:78]: SUB
                      # typed as: int
                      Block [23:64-23:78]
                        # typed as: int
                        Unary [23:65-23:77]: SUB
                          # typed as: int
                          Block [23:66-23:77]
                            # typed as: int
                            Unary [23:67-23:76]: SUB
                              # typed as: int
                              Block [23:68-23:76]
                                # typed as: int
                                Unary [23:69-23:75]: SUB
                                  # typed as: int
                                  Block [23:70-23:75]
                                    # typed as: int
                                    Unary [23:71-23:74]: ADD
                                      # typed as: int
                                      Literal [23:72-23:74]: INT(10)
                                        # typed as: int
                Literal [23:83-23:86]: INT(123)
                  # typed as: int
              Literal [24:21-24:23]: INT(12)
                # typed as: int
              Call [26:21-26:53]: firstfunction
                # defined at: [19:1-107:2]
                # typed as: int
                Literal [26:35-26:38]: INT(123)
                  # typed as: int
                Literal [26:40-26:46]: STR(bruh)
                  # typed as: str
                Literal [26:48-26:52]: LOG(true)
                  # typed as: log
        Binary [30:9-30:73]: OR
          # typed as: log
          Binary [30:9-30:32]: OR
            # typed as: log
            Binary [30:9-30:21]: AND
              # typed as: log
              Literal [30:9-30:13]: LOG(true)
                # typed as: log
              Literal [30:16-30:21]: LOG(false)
                # typed as: log
            Binary [30:24-30:32]: AND
              # typed as: log
              Name [30:24-30:25]: z
                # defined at: [19:42-19:52]
                # typed as: log
              Literal [30:28-30:32]: LOG(true)
                # typed as: log
          Block [30:35-30:73]
            # typed as: log
            Binary [30:36-30:72]: GEQ
              # typed as: log
              Literal [30:36-30:37]: INT(1)
                # typed as: int
              Call [30:41-30:72]: firstfunction
                # defined at: [19:1-107:2]
                # typed as: int
                Name [30:55-30:56]: i
                  # defined at: [55:5-55:18]
                  # typed as: int
                Literal [30:58-30:65]: STR(monke)
                  # typed as: str
                Literal [30:67-30:71]: LOG(true)
                  # typed as: log
        IfThenElse [31:9-35:10]
          # typed as: void
          Binary [32:16-32:61]: GT
            # typed as: log
            Literal [32:16-32:18]: INT(16)
              # typed as: int
            Block [32:21-32:61]
              # typed as: int
              Binary [32:22-32:60]: ADD
                # typed as: int
                Binary [32:22-32:54]: ADD
                  # typed as: int
                  Binary [32:22-32:49]: SUB
                    # typed as: int
                    Binary [32:22-32:44]: ADD
                      # typed as: int
                      Binary [32:22-32:29]: SUB
                        # typed as: int
                        Literal [32:22-32:24]: INT(14)
                          # typed as: int
                        Literal [32:27-32:29]: INT(10)
                          # typed as: int
                      Binary [32:32-32:44]: DIV
                        # typed as: int
                        Binary [32:32-32:39]: MUL
                          # typed as: int
                          Literal [32:32-32:34]: INT(10)
                            # typed as: int
                          Literal [32:37-32:39]: INT(10)
                            # typed as: int
                        Literal [32:42-32:44]: INT(10)
                          # typed as: int
                    Literal [32:47-32:49]: INT(10)
                      # typed as: int
                  Literal [32:52-32:54]: INT(10)
                    # typed as: int
                Unary [32:57-32:60]: SUB
                  # typed as: int
                  Literal [32:58-32:60]: INT(10)
                    # typed as: int
          Call [34:21-34:64]: firstfunction
            # defined at: [19:1-107:2]
            # typed as: int
            Name [34:35-34:36]: i
              # defined at: [55:5-55:18]
              # typed as: int
            Literal [34:38-34:56]: STR(danes je lep dan)
              # typed as: str
            Literal [34:58-34:63]: LOG(false)
              # typed as: log
        For [37:9-51:10]
          # typed as: void
          Name [38:13-38:14]: i
            # defined at: [55:5-55:18]
            # typed as: int
          Literal [38:17-38:18]: INT(1)
            # typed as: int
          Literal [38:20-38:22]: INT(10)
            # typed as: int
          Literal [38:24-38:26]: INT(12)
            # typed as: int
          Block [38:29-50:15]
            # typed as: int
            While [39:13-41:14]
              # typed as: void
              Binary [40:23-40:27]: LT
                # typed as: log
                Name [40:23-40:24]: j
                  # defined at: [57:5-57:19]
                  # typed as: int
                Literal [40:25-40:27]: INT(10)
                  # typed as: int
              Block [40:29-40:37]
                # typed as: log
                Binary [40:30-40:36]: LT
                  # typed as: log
                  Name [40:30-40:31]: j
                    # defined at: [57:5-57:19]
                    # typed as: int
                  Literal [40:34-40:36]: INT(10)
                    # typed as: int
            IfThenElse [42:13-44:14]
              # typed as: void
              Binary [43:20-43:33]: GEQ
                # typed as: log
                Binary [43:20-43:26]: MUL
                  # typed as: int
                  Name [43:20-43:21]: j
                    # defined at: [57:5-57:19]
                    # typed as: int
                  Literal [43:24-43:26]: INT(10)
                    # typed as: int
                Literal [43:30-43:33]: INT(100)
                  # typed as: int
              Binary [43:39-43:57]: ASSIGN
                # typed as: log
                Name [43:40-43:41]: a
                  # defined at: [56:5-56:19]
                  # typed as: log
                Binary [43:44-43:56]: EQ
                  # typed as: log
                  Binary [43:44-43:50]: SUB
                    # typed as: int
                    Name [43:44-43:45]: j
                      # defined at: [57:5-57:19]
                      # typed as: int
                    Literal [43:48-43:50]: INT(10)
                      # typed as: int
                  Literal [43:54-43:56]: INT(12)
                    # typed as: int
              Literal [43:63-43:64]: INT(0)
                # typed as: int
            For [45:13-47:14]
              # typed as: void
              Name [46:21-46:22]: j
                # defined at: [57:5-57:19]
                # typed as: int
              Literal [46:25-46:26]: INT(1)
                # typed as: int
              Literal [46:28-46:30]: INT(12)
                # typed as: int
              Binary [46:32-46:39]: ASSIGN
                # typed as: int
                Name [46:33-46:34]: j
                  # defined at: [57:5-57:19]
                  # typed as: int
                Binary [46:35-46:38]: MUL
                  # typed as: int
                  Name [46:35-46:36]: j
                    # defined at: [57:5-57:19]
                    # typed as: int
                  Literal [46:37-46:38]: INT(2)
                    # typed as: int
              Call [46:41-46:77]: firstfunction
                # defined at: [19:1-107:2]
                # typed as: int
                Literal [46:55-46:57]: INT(12)
                  # typed as: int
                Literal [46:59-46:70]: STR(blablabla)
                  # typed as: str
                Literal [46:72-46:76]: LOG(true)
                  # typed as: log
            Binary [48:13-50:14]: ASSIGN
              # typed as: int
              Binary [49:17-49:40]: ARR
                # typed as: int
                Binary [49:17-49:30]: ARR
                  # typed as: ARR(420,int)
                  Name [49:17-49:18]: p
                    # defined at: [58:5-58:36]
                    # typed as: ARR(69,ARR(420,int))
                  Binary [49:19-49:29]: SUB
                    # typed as: int
                    Binary [49:19-49:23]: ADD
                      # typed as: int
                      Name [49:19-49:20]: i
                        # defined at: [55:5-55:18]
                        # typed as: int
                      Literal [49:21-49:23]: INT(10)
                        # typed as: int
                    Binary [49:24-49:29]: MUL
                      # typed as: int
                      Literal [49:24-49:26]: INT(20)
                        # typed as: int
                      Literal [49:27-49:29]: INT(30)
                        # typed as: int
                Binary [49:31-49:39]: SUB
                  # typed as: int
                  Binary [49:31-49:35]: SUB
                    # typed as: int
                    Name [49:31-49:32]: j
                      # defined at: [57:5-57:19]
                      # typed as: int
                    Literal [49:33-49:35]: INT(10)
                      # typed as: int
                  Literal [49:36-49:39]: INT(203)
                    # typed as: int
              Literal [49:43-49:45]: INT(12)
                # typed as: int
        Literal [52:9-52:11]: INT(12)
          # typed as: int
!end