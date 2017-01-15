package sjava.compiler.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
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
            String dir = commandLine.hasOption("d")?commandLine.getOptionValue("d"):".";
            File[] out = new File[args.size()];
            Iterator it = args.iterator();

            for(int i = 0; it.hasNext(); ++i) {
                String path = (String)it.next();
                out[i] = new File(path);
            }

            List files = Arrays.asList(out);
            Main.compile(files, dir);
        }

    }
}
