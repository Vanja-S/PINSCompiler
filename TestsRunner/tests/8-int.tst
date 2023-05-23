!compiler_flags: --dump INT --exec INT

!name: First pass 1D array as parameter to a function
!code:
fun main(x : integer) : integer = (
    {for i = 0, 12, 1: (
            {ar[i] = i}
    )},
    {for i = 0, 12, 1: (
        {if i % 2 == 0 then (
            print_int(g(ar[i]))
        )}
    )},
    0
) {where var i: integer; var  ar : arr[12] integer};

fun g(y:integer) : integer = y
!expected:
0
2
4
6
8
10
!end

!name: Second pass MultiD array as parameter to a function
!code:
fun main(x : integer) : integer = (
    {for i = 0, 12, 1: (
            {ar[i][0][0] = i}
    )},
    {for i = 0, 12, 1: (
        {if i % 2 == 1 then (
            print_int(ar[i][0][0])
        )}
    )},
    0
) {where var i: integer; var  ar : arr[12] arr[12] arr[12] integer};

fun g(y:integer) : integer = y
!expected:
1
3
5
7
9
11
!end

!name: Thrird print result of a chained function call
!code:
fun main(x: integer) : integer = (
    {for i = 0, 12, 1: (
        {if i % 2 == 0 then (
            print_int(g(i))
        )}
    )},
    0
) {where var i: integer};

fun g ( y : integer ) : integer = h(y + 1)
    {where fun h (z : integer) : integer = {z = z + 1}}
!expected:
2
4
6
8
10
12
!end

!name: Fourth build in functions
!code:
fun main(x: integer) : integer = (
    {i = 2},
    print_int(i),
    {str = 'hello'},
    print_str(str),
    {bol = true},
    print_log(bol),
    seed(2),
    print_int(rand_int(1,2)),
    print_int(rand_int(1,100)),
    print_int(rand_int(1,100)),
    0
) {where var i: integer; var str : string; var bol : logical}
!expected:
2
"hello"
true
1
25
21
!end

!name: Fifth for, if else
!code:
fun main(x : integer) : integer = (
    {for i = 0, 12, 1: (
        {if i % 2 == 0 then (
            print_log(true)
        ) else (
            print_log(false)
        )}
    )},
    0
) {where var i: integer}
!expected:
true
false
true
false
true
false
true
false
true
false
true
false
!end

!name: Sixth recursion, fibonacci
!code:
fun fib(n:integer) : integer = (
    {
        if n <= 1 then
            { result = n }
        else
            { result = fib(n-1) + fib(n-2) }
    },
    result
) { where var result : integer };

fun main(argc : integer) : integer = (
print_int(fib(1)),
print_int(fib(2)),
print_int(fib(3)),
print_int(fib(4)),
print_int(fib(5)),
print_int(fib(6)),
print_int(fib(7)),
print_int(fib(8))
)
!expected:
1
1
2
3
5
8
13
21
!end

!name: Nested functions and integers
!code:
fun main(x: integer): integer = (
  { x = 1 },
  { w = 4 },
  g(2),
  print_str(str_test('aaaaa')),
  0
) { where
  var w: integer;
  fun g(y: integer): integer = (
    { ww = 5 },
    f(3),
    0
  ) { where
    var ww: integer;
    fun f(z: integer): integer = (
      print_int(x),
      print_int(y),
      print_int(z),
      print_int(w),
      print_int(ww),
      0
    )
  }
};


fun str_test(x: string): string = (
  { s = 'lolmao' },
  print_str(s),
  print_str(x),
  print_str('hello'),
  'bruh'
) { where
  var s: string
}
!expected:
1
2
3
4
5
"lolmao"
"aaaaa"
"hello"
"bruh"
!end

!name: Strings
!code:
fun main(x: integer): integer = (
    { s = 'str1' },
    print_str(s),
    print_str('str2'),
    print_str(f('str3')),
    0
) { where
    var s: string;
    fun f(str: string): string = (
        print_str(str),
        'str4'
    )
}
!expected:
"str1"
"str2"
"str3"
"str4"
!end

!name: Boolean
!code:
var b1: logical;
var b2: logical;
var b3: logical;
var b4: logical;
var b5: logical;
var b6: logical;

fun main(x: integer): integer = (
    { b1 = true },
    { b2 = false },
    { b3 = b1 == b2 },
    { b4 = b1 & b2 },
    { b5 = b1 | b2 },
    { b6 = !b1 },
    print_log(b3),
    print_log(b4),
    print_log(b5),
    print_log(b6),
    print_log(b1 == !b2),
    f1(b1, !b2),
    x
);

fun f1(b1: logical, b2: logical): logical = (
    { b3 = b1 == b2 },
    { b4 = b1 & b2 },
    { b5 = b1 | b2 },
    { b6 = !b1 },
    print_log(b3),
    print_log(b4),
    print_log(b5),
    print_log(b6),
    b1
) { where
    var b3: logical;
    var b4: logical;
    var b5: logical;
    var b6: logical
}
!expected:
false
false
true
false
true
true
true
true
false
!end

!name: While loop
!code:
typ int: integer;

fun main(x: integer): integer = (
    { i = 0 },
    { while i < 15: (
        print_int(i),
        { i = increment(i) }
    )},
    i
) { where
    var i: int;
    fun increment(x: integer): integer = (
        { x = x + 1 },
        x
    )
}
!expected:
0
1
2
3
4
5
6
7
8
9
10
11
12
13
14
!end

!name: For loop 1
!code:
fun main(x: integer): integer = (
    { for x = 0, 15, 2: (
        print_int(x)
    )},
    0
)
!expected:
0
2
4
6
8
10
12
14
!end

!name: For loop 2
!code:
fun main(x: integer): integer = (
    { for x = 1, 130, x: (
        print_int(x)
    )},
    0
)
!expected:
1
2
4
8
16
32
64
128
!end

!name: For loop 3
!code:
fun main(x: integer): integer = (
    { for x = 1, 130, x * 2: (
        print_int(x)
    )},
    0
)
!expected:
1
3
9
27
81
!end

!name: If statement
!code:
fun main(x: integer): integer = (
    { if larger_than_10(5) then
        print_str('5 is larger than 10')
      else
        print_str('5 is not larger than 10')
    },
    { if larger_than_10(20) then
        print_str('20 is larger than 10')
      else
        print_str('20 is not larger than 10')
    },
    print_log(f(5, 10)),
    print_log(f(10, 5)),
    0
) { where
    typ boolean: logical;
    fun f(x1: integer, x2: integer): boolean = (
        { if x1 < x2 then
            print_str('x1 < x2')
        else
            print_str('x1 >= x2')
        },
        true
    );
    fun larger_than_10(x1: integer): boolean = (
        { larger = false },
        { if x1 > 10 then
            { larger = true }
        },
        larger
    ) { where
        var larger: logical
    }
}
!expected:
"5 is not larger than 10"
"20 is larger than 10"
"x1 < x2"
true
"x1 >= x2"
true
!end

!name: Global arrays
!code:
typ list: arr[10] integer;
var l: integer;
var array: list;

fun main(x: integer): integer = (
  { l = 10 },
  fill_array(array, l, 69),
  print_array(array, l),
  fill_array2(array, l, 0),
  print_array(array, l),
  0
);

fun fill_array(a: list, l: integer, x: integer): integer = (
  { i = 0 },
  { while i < l: (
      { a[i] = x },
      { i = i + 1 }
  )},
  0
) { where
  var i: integer
};

fun fill_array2(a: list, l: integer, x: integer): integer = (
  { for i = x, l, 1:
      { a[i] = i }
  },
  0
) { where
  var i: integer
};

fun print_array(a: list, l:integer): integer = (
  { for i = 0, l, 1:
      print_int(a[i])
  },
  0
) { where
  var i: integer
}
!expected:
69
69
69
69
69
69
69
69
69
69
0
1
2
3
4
5
6
7
8
9
!end

!name: Local arrays
!code:
typ list: arr[10] integer;
var l: integer;

fun main(x: integer): integer = (
  { l = 10 },
  fill_array(array, l, 69),
  print_array(array, l),
  fill_array2(array, l, 0),
  print_array(array, l),
  0
) { where
  var array: list
};

fun fill_array(a: list, l: integer, x: integer): integer = (
  { i = 0 },
  { while i < l: (
      { a[i] = x },
      { i = i + 1 }
  )},
  0
) { where
  var i: integer
};

fun fill_array2(a: list, l: integer, x: integer): integer = (
  { for i = x, l, 1:
      { a[i] = i }
  },
  0
) { where
  var i: integer
};

fun print_array(a: list, l:integer): integer = (
  { for i = 0, l, 1:
      print_int(a[i])
  },
  0
) { where
  var i: integer
}
!expected:
69
69
69
69
69
69
69
69
69
69
0
1
2
3
4
5
6
7
8
9
!end

!name: Local Arrays 2
!code:
typ list: arr[10] integer;
var l: integer;

fun main(x: integer): integer = (
  fill_array(10, 0),
  print_array(a, 10),
  0
) { where
  var a: list;
  fun fill_array(l: integer, x: integer): integer = (
    { for i = x, l, 1:
        { a[i] = i * 2 }
    },
    0
  ) { where
    var i: integer
  }
};

fun print_array(a: list, l:integer): integer = (
  { for i = 0, l, 1:
      print_int(a[i])
  },
  0
) { where
  var i: integer
}
!expected:
0
2
4
6
8
10
12
14
16
18
!end


!name: BubbleSort (1D Array Operations)
!code:
var array : arr[10] integer;

fun print_array(n: integer) : integer = (
	{ for i = 0, n, 1 : print_int(array[i]) } { where var i : integer },
	1
);

fun generate_numbers(n: integer) : integer = (
	seed(0),
	{ for i = 0, n, 1 : {array[i] = rand_int(0, 100)} } { where var i : integer },
	1
);

fun swap(index1 : integer, index2 : integer) : integer = (
	{ temp = array[index1] },
	{ array[index1] = array[index2] },
	{ array[index2] = temp },
	1
) { where var temp : integer };

fun bubble_sort(n: integer) : integer = (
	{
		for i = 0, n - 1, 1 : {
			for j = 0, n - 1, 1 : {
				if array[j] < array[j+1] then
					swap(j, j+1)
			}
		}
	} { where var i : integer; var j : integer },
	1
);

fun main(x: integer) : integer = (
   generate_numbers(10),
   bubble_sort(10),
   print_array(10)
)
!expected:
91
61
60
54
53
48
47
29
19
15
!end



!name: Recursive Fibonacci (1D Array Access & Recursive Calls)
!code:
fun fib(n: integer) : integer = (
	{
		if n <= 1 then
			{ result = n }
		else (
			{ fib1 = fib(n-1) },
			{ fib2 = fib(n-2) },
			{ result = fib1 + fib2 }
		)
	},
	result
) { where var result : integer; var fib1 : integer; var fib2 : integer };

fun main(x: integer) : integer = (
	print_int(fib(0)),
	print_int(fib(2)),
	print_int(fib(3)),
	print_int(fib(9)),
	print_int(fib(15))
)
!expected:
0
1
2
34
610
!end



!name: Basic Array Operations (1D Array Access & Array Reference Passing)
!code:
typ arrayTyp : arr[3] integer;

fun fill_array(array : arrayTyp, n : integer) : logical = (
	{ for i = 0, n, 1 : { array[i] = rand_int(1, 100) } } { where var i : integer },
	true
);

fun arrays_sum(array1 : arrayTyp, array2 : arrayTyp, output : arrayTyp, n : integer) : logical = (
	{ for i = 0, n, 1 : { output[i] = array1[i] + array2[i] } } { where var i : integer },
	true
);

fun array_sum(array : arrayTyp, n : integer) : integer = (
	{ sum = 0 },
	{ for i = 0, n, 1 : { sum = sum + array[i] }} { where var i : integer },
	sum
) { where var sum : integer };

fun print_array(array : arrayTyp, n : integer) : logical = (
	{ for i = 0, n, 1 : print_int(array[i]) } { where var i : integer },
	true
);

fun main(argc : integer) : integer = (
	{ n = 3 },

	seed(0),
	fill_array(array1, n),
	fill_array(array2, n),
	arrays_sum(array1, array2, output, n),

	print_str('Array 1:'),
	print_array(array1, n),

	print_str('Array 2:'),
	print_array(array2, n),

	print_str('Output:'),
	print_array(output, n),

	print_str('Output Sum:'),
	print_int(array_sum(output, n))
) { where var array1 : arrayTyp; var array2 : arrayTyp; var output : arrayTyp; var n : integer }
!expected:
"Array 1:"
34
62
86
"Array 2:"
39
18
3
"Output:"
73
80
89
"Output Sum:"
242
!end



!name: Basic Nested Array Operations
!code:
typ arrayTyp : arr[3] integer;

fun start_array_operations(array1 : arrayTyp, array2 : arrayTyp, output : arrayTyp, n : integer) : integer = (
	seed(0),
	fill_array(array1),
	fill_array(array2),
	arrays_sum(array1, array2),

	print_str('Array 1:'),
	print_array(array1),

	print_str('Array 2:'),
	print_array(array2),

	print_str('Output:'),
	print_array(output),

	print_str('Output Sum:'),
	print_int(array_sum(output))
) { where
	fun fill_array(array : arrayTyp) : logical = (
		{ for i = 0, n, 1 : { array[i] = rand_int(1, 100) } } { where var i : integer },
		true
	);

	fun arrays_sum(array1 : arrayTyp, array2 : arrayTyp) : logical = (
		{ for i = 0, n, 1 : { output[i] = array1[i] + array2[i] } } { where var i : integer },
		true
	);

	fun array_sum(array : arrayTyp) : integer = (
		{ sum = 0 },
		{ for i = 0, n, 1 : { sum = sum + array[i] }} { where var i : integer },
		sum
	) { where var sum : integer };

	fun print_array(array : arrayTyp) : logical = (
		{ for i = 0, n, 1 : print_int(array[i]) } { where var i : integer },
		true
	)
};

fun main(argc : integer) : integer = (
	{ n = 3 },
	start_array_operations(array1, array2, output, n)
) { where var array1 : arrayTyp; var array2 : arrayTyp; var output : arrayTyp; var n : integer }
!expected:
"Array 1:"
34
62
86
"Array 2:"
39
18
3
"Output:"
73
80
89
"Output Sum:"
242
!end



!name: Matrix Operations (2D Array Access & Array Reference Passing)
!code:
typ matrixTyp : arr[3] arr[3] integer;

fun matrix_generate(matrix : matrixTyp, n : integer) : logical = (
	{ for i = 0, n, 1 :
		{ for j = 0, n, 1 :
			{ matrix[i][j] = rand_int(0, 100) }
		} { where var j : integer }
	} { where var i : integer },
	true
);

fun matrix_zero(matrix : matrixTyp, n : integer) : logical = (
	{ for i = 0, n, 1 :
		{ for j = 0, n, 1 :
			{ matrix[i][j] = 0 }
		} { where var j : integer }
	} { where var i : integer },
	true
);

fun matrix_multiply(matrix1 : matrixTyp, matrix2 : matrixTyp, output : matrixTyp, n : integer) : logical = (
	{ for i = 0, n, 1 :
		{ for j = 0, n, 1 :
			{ for k = 0, n, 1 :
				{ output[i][j] = output[i][j] + matrix1[i][k] * matrix2[k][j] }
			} { where var k : integer }
		} { where var j : integer }
	} { where var i : integer },
	true
);

fun matrix_print(matrix : matrixTyp, n : integer) : logical = (
	{ for i = 0, n, 1 :
		{ for j = 0, n, 1 :
			print_int(matrix[i][j])
		} { where var j : integer }
	} { where var i : integer },
	true
);

fun main(x: integer) : logical = (
	{ n = 3 },

	seed(0),
	matrix_generate(matrix1, n),
	matrix_generate(matrix2, n),
	matrix_zero(output, n),
	matrix_multiply(matrix1, matrix2, output, n),

	print_str('Matrix 1:'),
	matrix_print(matrix1, n),

	print_str('Matrix 2:'),
	matrix_print(matrix2, n),

	print_str('Multiplication Output:'),
	matrix_print(output, n)
) { where var matrix1 : matrixTyp; var matrix2 : matrixTyp; var output : matrixTyp; var n : integer }
!expected:
"Matrix 1:"
60
48
29
47
15
53
91
61
19
"Matrix 2:"
54
77
77
73
62
95
44
84
75
"Multiplication Output:"
8020
10032
11355
5965
9001
9019
10203
12385
14227
!end



!name: Nested Functions (Simple Root Function Parameter Access)
!code:
fun nested_functions_print(n : integer) : integer = print_variable(0)
{ where
	fun print_variable(x : integer) : integer = print_variable2(0)
	{ where
		fun print_variable2(y : integer) : integer = print_int(n)
	}
};

fun main(argc : integer) : integer = (
	{ n = 8 },
	nested_functions_print(n)
) { where var n : integer }
!expected:
8
!end

!name: 2D Arrays
!code:
typ arr2dim: arr[2] arr[10] integer;
var a: arr2dim;

fun main(x: integer): integer = (
    fill_array(a[0], 0),
    fill_array(a[1], 10),
    print_arr(a),
    0
);

fun fill_array(a: arr[10] integer, x: integer): integer = (
    { for i = 0, 10, 1:
        { a[i] = x + i }
    },
    0
) { where
    var i: integer
};

fun print_arr(a: arr2dim): integer = (
    { for i = 0, 2, 1:
        print_array(a[i])
    },
    0
) { where
    var i: integer;
    fun print_array(a: arr[10] integer): integer = (
        { for i = 0, 10, 1:
            print_int(a[i])
        },
        0
    ) { where
        var i: integer
    }
}
!expected:
0
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
!end

!name: 2D Arrays 2
!code:
typ arr2dim: arr[2] arr[10] integer;

fun main(x: integer): integer = (
    fill_array(a[0], 0),
    fill_array(a[1], 10),
    print_arr(a),
    0
) { where
    var a: arr2dim
};

fun fill_array(a: arr[10] integer, x: integer): integer = (
    { for i = 0, 10, 1:
        { a[i] = x + i }
    },
    0
) { where
    var i: integer
};

fun print_arr(a: arr2dim): integer = (
    { for i = 0, 2, 1:
        print_array(a[i])
    },
    0
) { where
    var i: integer;
    fun print_array(a: arr[10] integer): integer = (
        { for i = 0, 10, 1:
            print_int(a[i])
        },
        0
    ) { where
        var i: integer
    }
}
!expected:
0
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
!end

!name: 3D Arrays
!code:
typ arr3dim: arr[2] arr2dim;
typ arr2dim: arr[2] arr[10] integer;
var a: arr3dim;

fun main(x: integer): integer = (
    fill_array(a[0], 0),
    fill_array(a[1], 10),
    print_arr(a),
    0
);

fun fill_array(a: arr2dim, x: integer): integer = (
    { for i = 0, 10, 1:
      { for j = 0, 2, 1:
        { a[j][i] = i + j }
      }
    },
    0
) { where
    var i: integer;
    var j: integer
};

fun print_arr(a: arr3dim): integer = (
    { for i = 0, 2, 1:
      { for j = 0, 2, 1:
          print_array(a[i][j])
      }
    },
    0
) { where
    var i: integer;
    var j: integer;
    fun print_array(a: arr[10] integer): integer = (
        { for i = 0, 10, 1:
            print_int(a[i])
        },
        0
    ) { where
        var i: integer
    }
}
!expected:
0
1
2
3
4
5
6
7
8
9
1
2
3
4
5
6
7
8
9
10
0
1
2
3
4
5
6
7
8
9
1
2
3
4
5
6
7
8
9
10
!end