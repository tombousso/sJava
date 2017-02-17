package sjava.compiler.commands;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import sjava.compiler.Formatter;
import sjava.compiler.commands.Command;

public class FormatCommand extends Command {
    public String name() {
        return "fmt";
    }

    String helpArgs() {
        return "[files]";
    }

    public void run(CommandLine commandLine, List<String> args) {
        List fileNames = args;
        if(args.size() == 0) {
            this.printHelp();
        } else {
            try {
                Iterator it = fileNames.iterator();

                for(int notused = 0; it.hasNext(); ++notused) {
                    String name = (String)it.next();
                    File f = new File(name);
                    String in = FileUtils.readFileToString(f);
                    String out = Formatter.formatCode(in);
                    if(!in.equals(out)) {
                        PrintStream var10000 = System.out;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Overwriting ");
                        sb.append(name);
                        var10000.println(sb.toString());
                        FileUtils.writeStringToFile(f, out);
                    }
                }
            } catch (Throwable var13) {
                var13.printStackTrace();
            }
        }

    }
}
