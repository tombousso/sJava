package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class MacroCallToken extends Token {
    public Token ret;
    public String name;
    public List<Token> toks;

    public MacroCallToken(int line, String name, List<Token> toks) {
        super(line);
        this.name = name;
        this.toks = toks;
    }
}
