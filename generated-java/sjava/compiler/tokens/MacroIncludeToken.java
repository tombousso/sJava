package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class MacroIncludeToken extends Token {
    public Token ret;
    public String name;
    public List<Token> toks;

    public MacroIncludeToken(int line, String name, List<Token> toks) {
        super(line);
        this.name = name;
        this.toks = toks;
    }
}
