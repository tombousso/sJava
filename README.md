# sJava - A simple self hosting compiler for a Java dialect using S-expressions, bootstrapped on Kawa
For comparison, here's a hello world program in sJava:

    (define-class Main () 'public
        ((main args String[]) void 'public 'static
            (System:out:println "Hello world")
        )
    )

and in Java:

    public class Main {
        public static void main(String[] args) {
            System.out.println("Hello world");
        }
    }

A more complex example of an sJava program including generics, autoboxing/unboxing, star imports, and type inference (generics.sjava):

    (import java.util.*)
    (define-class Main () 'public
        ((main args String[]) void 'public 'static
            (define al (ArrayList{Integer}))
            (al:add 3)
            (al:add 4)
            (System:out:println (+ (al:get 0) (al:get 1))) ;prints 7
        )
    )


The bytecode for this example can be found at [the bottom of the page](#final-thoughts).

The sJava compiler outputs Java bytecode in classfiles just like the Java compiler.
At this point the best example of sJava code is the sJava compiler itself (compile.sjava).

### Kawa
sJava borrows a lot from Kawa which is a Scheme implementation for the JVM. [Kawa](http://www.gnu.org/software/kawa/) is a really cool dynamic language (first class functions, macros, etc.) which can interact with traditional statically typed Java code. The compiler for sJava was originally written in Kawa (compile.scm). Porting it from Kawa to sJava to make it self hosting was trivial because it isn't written using dynamic features (compile.sjava).

### Features
* imports
* autoboxing/unboxing
* if statements/conditionals (short circuit)
* arrays
* simple generics
* while loops
* simple try catch
* classes/methods/fields
* some other basic things

### Todo
* verify sJava code and print friendly error messages
* add a bunch of stuff (field initializers, compile time macros, anonymous classes, better command line interface, etc)
* fix everything :P

### Running an sJava program
Assuming you've downloaded sJava and have Java installed and in your PATH:

    sjava>java -cp "*" kawa.repl compile.scm helloworld.sjava
    sjava>cd out
    sjava\out>java Main
    Hello world
    sjava\out>cd ..
    sjava>java -cp "*" kawa.repl compile.scm generics.sjava
    sjava>cd out
    sjava/out>java Main
    7
    sjava/out>cd ..
    sjava>

Wow you just ran your first two sJava programs! The sJava compiler takes in an sjava source file and creates classfiles in the out directory. The -cp "*" option sets the JVM's classpath to include the jars in the current directory (Kawa in order to use kawa.repl and gnu.bytecode, and Apache Commons for string escape/unescape).

#### Compiling the compiler
Now for the fun part, compiling the sJava compiler written in sJava using the sJava compiler written in Kawa (bootstrapping):  
**Note: on Linux the classpath needs to be seperated with colons, not semicolons**

    sjava>java -cp "*" kawa.repl compile.scm compile.sjava
    sjava>cd out
    sjava/out>java -cp ".;../*" Main ../generics.sjava
    sjava/out>cd out
    sjava/out/out>java Main
    7
    sjava/out/out>cd ..
    sjava/out>

The classpath now includes . so that the JVM searches for classfiles in the current directory.


So now you've compiled the sJava compiler written in sJava using the sJava compiler written in Kawa, and you ran the generics.sjava example using the new compiler. To verify the new compiler you can tell it to compile itself:

    sjava/out>java -cp ".;../*" Main ../compile.sjava
    sjava/out>diff . out
    Only in .: out
    sjava/out>cd ..
    sjava>

The output of that diff command means that the classfiles which the sJava compiler written in Kawa created when compiling the sJava compiler written in sJava are the same as those which the sJava compiler written in sJava created when compiling itself.

Or in other words the compiler is self hosting. As of right now you can compile any sJava program using the compiler written in Kawa or the one written in sJava, but technically features don't have to be added to the Kawa version anymore, it could just be used for bootstrapping.

### Lexer/Parser
The lexer is very simple, it looks for special tokens (eg semicolons for comments) and literals (numbers, characters, strings), and uses spaces as separators. It also adds line number information to all the tokens for the compiler.  
The parser recursively goes through all the tokens and handles them in order of precedence. For example first it will look for semicolons (comments) and remove all tokens from the semicolon until a newline.

### Compiler
#### gnu.bytecode
Kawa is also a great framework with lots of useful libraries. The sJava compiler uses [gnu.bytecode](http://www.gnu.org/software/kawa/api/gnu/bytecode/package-summary.html) which is a library for generating Java bytecode in the form of classfiles (the link includes an example). gnu.bytecode is very useful because it simplifies bytecode generation and throws exceptions when your compiler does stuff that doesn't make sense.

#### Stages
The compiler has 3 stages for compiling sJava code:

* Create a ClassType object for each class which is defined in the sJava source file and handle imports (Compiler:compile_types)
* Add methods to the ClassType objects which have been created (Compiler:compile_method_defs)
* Compile the method bodies (Compiler:compile_root)

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

Ifs and conditionals are implemented using a recursive method (Compiler:emitIf_). The method's parameters include a comparison operator, whether to inverse the comparison operator, the code for the true case, and the code for else (can be null).

A not (!) in a conditional triggers a recursive call with the inverse comparator argument inversed.

Short circuit ands (&&) and ors (||) are handled using De Morgan's laws, !(a && b) == (!a || !b) and !(a || b) == (!a && !b).

This means that if the inverse argument is true and the comparison operator is && or the inverse argument is false and the comparison operator is ||, then an **or** is emitted (returns true when the first comparison results in a true, else false).

If the inverse argument is true and the comparison operator is || or the inverse argument is false and the comparison operator is &&, then an **and** is emitted (returns false when the first comparison results in a false, else true).

If the inverse comparator argument is true when a conditional needs to be emitted, the condition is inversed.

Sounds a bit confusing but it works through the magic of recursion :)

#### Types
The Compiler:compile_ method takes in a "needed" Type (among other things) and returns the Type which the operation results in. Consider:

    (define a double 5)

This sJava code defines a variable of type double and sets it to 5.0. Compiler:compile_ will be called on the token "5" with "needed" set to Type:doubleType and so it will convert the integer 5 to a double 5.0 to meet the "needed" requirement.

#### Generics
The JVM doesn't support generics and so they are implemented in Java using type erasure. Generics give the compiler extra information to work with during compile time, but to the JVM all Map objects are Map\<Object, Object>. 

In the generics.sjava example, the variable doesn't have a given type and so the token (ArrayList{Integer}) will run with "needed" set to unknownType by the code for define. The variable's type will become the return value of the Compiler:compile_, which will be ArrayList{Integer}. These defines are equivalent:

    (define al ArrayList{Integer} (ArrayList{Integer}))
    (define al (ArrayList{Integer}))

### Final thoughts
gnu.bytecode makes it easy to create classfiles and the JVM will do many optimizations as it's JITing so the bytecode doesn't really need much optimization. Also there are bytecode optimizers like ProGuard which can help with performance. Unlike the CLR which was designed by Microsoft to be the platform for many languages, the JVM was originally designed only for Java (no standard assembly language or assembler, etc), and so it seems like there are less languages which target the JVM.

The Kawa version of the sJava compiler (compile.scm) also includes commented out code which creates an SVG graph of the tokens using a dot file and reflection code which looks for a main method and runs it.

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
          16: pop                  //Adding the first number
          17: aload_1
          18: iconst_4
          19: invokestatic  #12                 // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
          22: invokevirtual #16                 // Method java/util/ArrayList.add:(Ljava/lang/Object;)Z
          25: pop                  //Adding the second number
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
          51: iadd                 //Adding
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

The bytecode which the Java compiler outputs for that Java code is actually identical to the bytecode which the sJava compiler outputs for generics.sjava :) (except that the constants pool is more compact in the Java version)

Now that you've read all about this language I'm sure that you can't wait to write all your code in sJava ;)  
