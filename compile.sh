java -cp "*" kawa.repl compile.scm compile.sjava
cd out
java -cp ".:../*" Main ../compile.sjava
diff . out
cd ..
