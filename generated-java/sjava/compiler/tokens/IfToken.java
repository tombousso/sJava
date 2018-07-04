package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class IfToken extends Token {
    public ImList<Token> toks;

    public IfToken(int line, ImList<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
