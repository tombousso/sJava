java -cp "*" kawa.repl compile.scm compile.sjava
cd out
java -cp ".;../*" Main ../macro.sjava
cd out
java Main
cd ..
cd ..