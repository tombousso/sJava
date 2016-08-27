package sjava.compiler.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import sjava.compiler.ClassInfo;
import sjava.compiler.FileScope;
import sjava.compiler.Main;
import sjava.compiler.commands.Command;

public class BuildCommand extends Command {
    public BuildCommand() {
        super.options.addOption(Option.builder("d").hasArg().desc("Output directory for classfiles").build());
    }

    public String name() {
        return "build";
    }

    String helpArgs() {
        return "[options] [files]";
    }

    public void run(CommandLine commandLine, List<String> args) {
        if(args.size() == 0) {
            this.printHelp();
        } else {
            String dir = commandLine.hasOption("d")?commandLine.getOptionValue("d"):"";
            Map fileScopes = Main.compile(args);
            Set iterable = fileScopes.entrySet();
            Iterator it = iterable.iterator();

            for(int notused = 0; it.hasNext(); ++notused) {
                Entry entry = (Entry)it.next();
                FileScope fs = (FileScope)entry.getValue();
                List iterable1 = fs.newClasses;
                Iterator it1 = iterable1.iterator();

                for(int notused1 = 0; it1.hasNext(); ++notused1) {
                    ClassInfo ci = (ClassInfo)it1.next();
                    ci.writeFiles(dir);
                }
            }
        }

    }
}
