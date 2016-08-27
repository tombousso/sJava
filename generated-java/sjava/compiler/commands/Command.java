package sjava.compiler.commands;

import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public abstract class Command {
    public DefaultParser parser = new DefaultParser();
    public Options options = new Options();

    Command() {
        this.options.addOption(Option.builder("h").longOpt("help").build());
    }

    public abstract String name();

    String helpHeader() {
        return "";
    }

    String helpFooter() {
        return "";
    }

    abstract String helpArgs();

    public void printHelp() {
        HelpFormatter var10000 = new HelpFormatter();
        StringBuilder sb = new StringBuilder();
        sb.append("sjava ");
        sb.append(this.name());
        sb.append(" ");
        sb.append(this.helpArgs());
        var10000.printHelp(sb.toString(), this.helpHeader(), this.options, this.helpFooter());
    }

    public abstract void run(CommandLine var1, List<String> var2);
}
