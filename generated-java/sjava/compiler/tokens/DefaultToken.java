package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class DefaultToken extends Token {
    public ImList<Token> toks;

    public DefaultToken(int line, ImList<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
