package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class GotoToken extends Token {
    public GotoToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
