package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class SetToken extends Token {
    public ImList<Token> toks;

    public SetToken(int line, ImList<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
