package sjava.compiler.commands;

import gnu.bytecode.ArrayClassLoader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.cli.CommandLine;
import sjava.compiler.ClassInfo;
import sjava.compiler.FileScope;
import sjava.compiler.Main;
import sjava.compiler.commands.Command;

public class RunCommand extends Command {
    public String name() {
        return "run";
    }

    String helpArgs() {
        return "<MainClass> [files]";
    }

    public void run(CommandLine commandLine, List<String> args) {
        if(args.size() < 2) {
            this.printHelp();
        } else {
            try {
                List fileNames = args.subList(1, args.size());
                Map fileScopes = Main.compile(fileNames);
                ArrayClassLoader cl = new ArrayClassLoader();
                ClassInfo found = (ClassInfo)null;
                Set iterable = fileScopes.entrySet();
                Iterator it = iterable.iterator();

                for(int notused = 0; it.hasNext(); ++notused) {
                    Entry entry = (Entry)it.next();
                    FileScope fs = (FileScope)entry.getValue();
                    List iterable1 = fs.newClasses;
                    Iterator it1 = iterable1.iterator();

                    for(int notused1 = 0; it1.hasNext(); ++notused1) {
                        ClassInfo ci = (ClassInfo)it1.next();
                        if(ci.c.getName().equals((String)args.get(0))) {
                            found = ci;
                        }

                        ci.addToClassLoader(cl);
                    }
                }

                if(found == null) {
                    PrintStream var10000 = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append((String)args.get(0));
                    sb.append(" not found");
                    var10000.println(sb.toString());
                } else {
                    found.getClazz(cl).getMethod("main", new Class[]{String[].class}).invoke((Object)null, new Object[]{null});
                }
            } catch (Throwable var18) {
                var18.printStackTrace();
            }
        }

    }
}
