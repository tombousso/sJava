package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class AGetToken extends Token {
    public ImList<Token> toks;

    public AGetToken(int line, ImList<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
