# sJava - A simple self hosting compiler for a Java dialect using S-expressions
Here's a hello world program in sJava (examples/helloworld.sjava):

	(define-class Main () public
		((main args String[]) void public static
			(System:out:println "Hello world")
		)
	)

and in Java:

	public class Main {
		public static void main(String[] args) {
			System.out.println("Hello world");
		}
	}

A more complex example of an sJava program including generics, autoboxing/unboxing, star imports, and type inference (examples/generics.sjava):

	(import java.util.*)
	(define-class Main () public
		((main args String[]) void public static
			(define al (ArrayList{Integer})) ;constructor
			(al:add 3)
			(al:add 4)
			(System:out:println (+ (al:get 0) (al:get 1))) ;prints 7
		)
	)


The sJava compiler outputs Java bytecode in classfiles just like the Java compiler.

### sJava features
* hygenic compile-time macros
* lambdas
* autoboxing/unboxing
* type inference
* if statements/conditionals (short circuit)
* arrays
* generics (full support except wildcards)
* loops
* classes/methods/fields
* a collection of useful "standard" macros (forEach, concat(enate), inc(rement), etc.)
* most other things you'd except from a Java-like langauge

## Walkthrough!

### Requirements
Java 8 - `java`

### Hierarchy (optional)

	├── build.gradle (Gradle build script for the compilers and examples)
	├── compiler/
	│   ├── commands.sjava (BuildCommand, RunCommand, FormatCommand, etc.)
	│   ├── emitters.sjava
	│   ├── handlers.sjava (Bulk of the compilation code. compile methods in a Handler act on Tokens using double dispatch)
	│   ├── Main.sjava (Lexer, Parser, various structures, CLI entry, statics)
	│   ├── mfilters.sjava (Figuring out which methods to call)
	│   └── tokens.sjava
	├── bin/
	├── examples/
	│   ├── generics.sjava (A test program)
	│   ├── generics.expected.txt (The expected output)
	│   └── ...
	├── lib/
	│   ├── commons-cli-1.3.1.jar
	│   ├── commons-io-2.5.jar
	│   ├── commons-lang-3.4.jar
	│   └── kawa-2.1.9.jar
	├── std/ (The standard library)
	│   ├── **.sjava
	│   └── macros.sjava (The standard macros)
	├── sjava.jar (JAR version of *compiler* and *std*)
	├── sjava (Unix script for *sjava.jar*)
	├── sjava.bat (Windows script for *sjava.jar*)
	└── generated-java/ (Auto generated Java version of *compiler* using fernflower)

### Steps
Once you've downloaded/cloned this repo open a terminal in its root directory.  
(On Windows use `sjava` instead of `./sjava`):

	> ./sjava
	usage: sjava [command] [arguments]

	Commands:

		build
		run
		fmt

	> ./sjava run
	usage: sjava run <MainClass> [files]

(Inspired by Golang)  
To run `examples/tictactoe.sjava`, which uses a JavaFX GUI:

	> ./sjava run examples.tictactoe.Main examples/tictactoe.sjava

Check out the code, it's about 200 lines.  
The `run` command compiles sJava code and runs it from memory.  
To build `compiler` from source and update your `sjava.jar` (On Windows use `gradlew` instead of `./gradlew`):

	> ./gradlew jar

If everything works you should notice a couple of new folders in your `bin/` directory including `bin/main/`, which contains the classfiles of `compiler` and `std`.  
Have a look in `bin/main/sjava/compiler/tokens/` if you're interested in the different Token types which are used during compilation.  
To run all of the tests in `examples/`:

	> ./gradlew tester
	doubledispatch: PASSED
	formatterTest: PASSED
	generics: PASSED
	...

This will compile all of the examples and check the ones which have expected outputs.  
You can run the `tictactoe` example again (this time the classes are already compiled in `bin/examples/tictactoe`):

	> ./gradlew run_tictactoe

To run `examples/macro.sjava`:

	> ./gradlew run_macro
	Compiled at 21:16:37 08/27/2016
	Compile time dice roll result: 3
	Runtime random number up to 1000: 745
	> ./gradlew run_macro
	Compiled at 21:16:37 08/27/2016
	Compile time dice roll result: 3
	Runtime random number up to 1000: 131

The date is the same in both runs because Gradle didn't recompile `macro.sjava`, since it didn't change.  
And finally to run `doubledispatch`:

	> ./gradlew run_doubledispatch
	Visitor1 received a String: a
	Visitor1 received a String: b
	Visitor1 received an Integer: 1
	Visitor1 received an Integer: 2

	Visitor2 received a String: a
	Visitor2 received a String: b
	Visitor2 received an Integer: 1
	Visitor2 received an Integer: 2

If you have a look at `examples/doubledispatch.sjava` you will notice that it uses several macros, including `println` and `forEach`.  
The most interesting macro is called on line 28, `doubleDispatch`. This macro roughly expands to:

	(cond
		((instance? o String)
			(this:visit (as String o))
		)
		((instance? o Integer)
			(this:visit (as Integer o))
		)
	)

`cond` itself is actually a macro.  
All of these macros are defined in `std/macros.sjava`.

### More on macros
In Java for-each loops and string concatenation are built in to the compiler. In sJava features like these are implemented as "standard macros" (`forEach` and `concat`) in `std/macros.sjava`, and they are available to all programs.

Compile-time macros in sJava are very powerful. For example the `forEach` macro is able to ask the compiler at compile-time about the type of the object which the user is trying to iterate over. It checks if the type is an `ArrayType`, and if so it will create code which loops by incrementing a counter. Otherwise it will assume the user is trying to iterate over an `Iterable` and try to create an `Iterator`.

The `concat` macro will automatically call `Arrays:toString` when passed in `ArrayType`s.

You can create a `printTest.sjava` file:

	(define-class Main () public
		((main args String[]) void public static
			(println "Counting to 3: " (int[] 1 2 3))
		)
	)

And run it:

	> ./sjava run Main printTest.sjava
	Counting to 3: [1, 2, 3]

Or build then run:

	> ./sjava build printTest.sjava
	> java Main
	Counting to 3: [1, 2, 3]

The `println` macro takes in a variable number of arguments and passes them all  to `concat`, so you can see the `Arrays:toString` functionality in action.

More features can be found in `examples/test.sjava`, in other examples, and in `compiler/**.sjava`.  Also if you're adventurous you can check out `vertx/` which is a little site I made using the Vertx library and sJava.

If you've followed the walkthrough all the way until here, congrats! Let me know if you have any thoughts. The compiler still has some bugs I'm sure but overall it works reasonably well. If there's an error in compilation it will at least tell you which line is problematic, but parsing errors aren't handled right now.

### Just for fun
`fernflower.jar` is a crazy decompiler which can actually convert the bytecode of `compiler` into proper Java code!

	> ./gradlew java_sources

And now that the Java files have been created:

	> ./gradlew java_compile

If there are no errors then the diff ran successfully meaning that the Java version of `compiler` successfully compiled `compiler`.

## More explanations

### Kawa
sJava borrows a lot from Kawa which is a Scheme implementation for the JVM. [Kawa](http://www.gnu.org/software/kawa/) is a really cool dynamic language (first class functions, macros, etc.) which can interact with traditional statically typed Java code. The first compiler for sJava was written in Kawa.

### Todo
* verify sJava code and print friendly error messages
* field initializers
* fix everything :P

### Compiler
#### gnu.bytecode
Kawa is a Scheme implementation, but it is also a great framework with lots of useful libraries. The sJava compiler uses [gnu.bytecode](http://www.gnu.org/software/kawa/api/gnu/bytecode/package-summary.html) which is a library for generating Java bytecode in the form of classfiles (the link includes an example). gnu.bytecode is very useful because it simplifies bytecode generation and throws exceptions when your compiler does stuff that doesn't make sense.

#### Stages
The compiler has roughly 3 stages for compiling sJava code:

* Create a ClassType object for each class which is defined in the sJava source file and handle imports
* Add methods to the ClassType objects which have been created
* Compile the method bodies

Steps 1 and 2 have to be separate because a method in a class defined at the top of the file might want to return an instance of a class defined at the bottom of the file, for example.

Most parts of the compilation are quite straightforward, however some parts are a bit tricky.

#### If statements/conditionals
In Java bytecode (and x86 assembler), conditionals like

	(define a (> 5 3))
	
will be compiled using branches:

	 0: iconst_5
	 1: iconst_3
	 2: if_icmple     9
	 5: iconst_1
	 6: goto          10
	 9: iconst_0
	10: istore_1
	11: return

Instructions 0 and 1 push 5 and 3 onto the stack. Then the if_icmple instruction will determine that 5 is not less than or equal to 3 and so it will **not** jump to instruction 9. Therefore a one (true) will be pushed onto the stack and the goto will skip over instruction 9, which pushes a zero (false), into instruction 10 which stores true in a, the variable.

The define above is equivalent to:

	(define a (if (> 5 3) true false))

Notice that the opposite comparison is used in the bytecode (less than or equal to instead of greater than). If you're wondering why, consider:

	(if (> 5 3)
		(System:out:println "Hello world")
	)

and the Java bytecode (shortened):

	 0: iconst_5
	 1: iconst_3
	 2: if_icmple     13
		System.out.println("Hello world");
	13: return

Now that the branch doesn't have an "else", using the greather than instruction (if_icmpgt) instead of less than or equal to (if_icmple) would be quite awkward:

	 0: iconst_5
	 1: iconst_3
	 2: if_icmpgt     8
	 5: goto          16
	 8: System.out.println("Hello world");
	16: return

##### Implementation

Ifs and conditionals are implemented using a recursive method (emitIf_). The method's parameters include a comparison operator, whether to inverse the comparison operator, the code for the true case, and the code for else (can be null).

A not (!) in a conditional triggers a recursive call with the inverse comparator argument inversed.

Short circuit ands (&&) and ors (||) are handled using De Morgan's laws, !(a && b) == (!a || !b) and !(a || b) == (!a && !b).

This means that if the inverse argument is true and the comparison operator is && or the inverse argument is false and the comparison operator is ||, then an **or** is emitted (returns true when the first comparison results in a true, else false).

If the inverse argument is true and the comparison operator is || or the inverse argument is false and the comparison operator is &&, then an **and** is emitted (returns false when the first comparison results in a false, else true).

If the inverse comparator argument is true when a conditional needs to be emitted, the condition is inversed.

Sounds a bit confusing but it works through the magic of recursion :)

### Final thoughts
gnu.bytecode makes it easy to create classfiles and the JVM will do many optimizations as it's JITing so the bytecode doesn't really need much optimization. Also there are bytecode optimizers like ProGuard which can help with performance. Unlike the CLR which was designed by Microsoft to be the platform for many languages, the JVM was originally designed only for Java (no standard assembly language or assembler, etc), and so it seems like there are less languages which target the JVM.

In case you're interested here is the decompiled bytecode of generics.sjava (javap -c) with added comments:

	public class Main {
	  public static void main(java.lang.String[]);
		Code:
		   0: new           #2                  // class java/util/ArrayList
		   3: dup
		   4: invokespecial #6                  // Method java/util/ArrayList."<init>":()V
		   7: astore_1             //Constructing the ArrayList
		   8: aload_1
		   9: iconst_3
		  10: invokestatic  #12                 // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
		  13: invokevirtual #16                 // Method java/util/ArrayList.add:(Ljava/lang/Object;)Z
		  16: pop                  //Adding the first number to the list
		  17: aload_1
		  18: iconst_4
		  19: invokestatic  #12                 // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
		  22: invokevirtual #16                 // Method java/util/ArrayList.add:(Ljava/lang/Object;)Z
		  25: pop                  //Adding the second number to the list
		  26: getstatic     #22                 // Field java/lang/System.out:Ljava/io/PrintStream;
		  29: aload_1
		  30: iconst_0
		  31: invokevirtual #26                 // Method java/util/ArrayList.get:(I)Ljava/lang/Object;
		  34: checkcast     #8                  // class java/lang/Integer
		  37: invokevirtual #32                 // Method java/lang/Number.intValue:()I
								   //Getting the first number
		  40: aload_1
		  41: iconst_1
		  42: invokevirtual #26                 // Method java/util/ArrayList.get:(I)Ljava/lang/Object;
		  45: checkcast     #8                  // class java/lang/Integer
		  48: invokevirtual #32                 // Method java/lang/Number.intValue:()I
								   //Getting the second number
		  51: iadd                 //Adding the numbers
		  52: invokevirtual #38                 // Method java/io/PrintStream.println:(I)V
								   //Printing
		  55: return
	}

Here's a version of genercs.sjava in Java:

	public class Main {
		public static void main(String[] args)
		{
			java.util.ArrayList<Integer> nums = new java.util.ArrayList<Integer>();
			nums.add(3);
			nums.add(4);
			System.out.println(nums.get(0) + nums.get(1));
		}
	}

The bytecode which the Java compiler outputs for that Java code is actually identical to the bytecode which the sJava compiler outputs for generics.sjava :)

Now that you've read all about this language I'm sure that you can't wait to write all your code in sJava ;)

