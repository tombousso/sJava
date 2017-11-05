package sjava.compiler.commands;

import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class Command {
    public DefaultParser parser = new DefaultParser();
    public Options options = new Options();

    Command() {
        this.options.addOption(Option.builder("h").longOpt("help").build());
    }

    String helpHeader() {
        return "";
    }

    String helpFooter() {
        return "";
    }

    public void printHelp() {
        HelpFormatter var10000 = new HelpFormatter();
        StringBuilder sb = new StringBuilder();
        sb.append("sjava ");
        sb.append(this.name());
        sb.append(" ");
        sb.append(this.helpArgs());
        var10000.printHelp(sb.toString(), this.helpHeader(), this.options, this.helpFooter());
    }

    public CommandLine parse(String[] args) {
        try {
            CommandLine var2 = this.parser.parse(this.options, args);
            return var2;
        } catch (ParseException var4) {
            throw new RuntimeException(var4);
        }
    }

    public abstract String name();

    abstract String helpArgs();

    public abstract void run(CommandLine var1, List<String> var2);
}
