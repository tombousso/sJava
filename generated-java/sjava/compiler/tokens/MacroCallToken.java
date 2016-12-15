package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class MacroCallToken extends Token {
    public Token ret;

    public MacroCallToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
