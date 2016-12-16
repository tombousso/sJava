package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class DefaultToken extends Token {
    public List<Token> toks;

    public DefaultToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
