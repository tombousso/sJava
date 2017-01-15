package sjava.compiler.commands;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import sjava.compiler.ClassInfo;
import sjava.compiler.FileScope;
import sjava.compiler.Main;
import sjava.compiler.MyClassLoader;
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
                File[] out = new File[fileNames.size()];
                Iterator it = fileNames.iterator();

                for(int i = 0; it.hasNext(); ++i) {
                    String path = (String)it.next();
                    out[i] = new File(path);
                }

                List files = Arrays.asList(out);
                List fileScopes = Main.compile((Collection)files);
                MyClassLoader cl = new MyClassLoader();
                ClassInfo found = (ClassInfo)null;
                Iterator it1 = fileScopes.iterator();

                for(int notused = 0; it1.hasNext(); ++notused) {
                    FileScope fs = (FileScope)it1.next();
                    List iterable = fs.newClasses;
                    Iterator it2 = iterable.iterator();

                    for(int notused1 = 0; it2.hasNext(); ++notused1) {
                        ClassInfo ci = (ClassInfo)it2.next();
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
            } catch (Throwable var24) {
                var24.printStackTrace();
            }
        }

    }
}
