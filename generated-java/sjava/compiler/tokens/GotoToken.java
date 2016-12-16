package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class GotoToken extends Token {
    public List<Token> toks;

    public GotoToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
