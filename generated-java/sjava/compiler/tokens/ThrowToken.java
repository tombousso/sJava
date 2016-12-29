package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class ThrowToken extends Token {
    public Token tok;

    public ThrowToken(int line, Token tok) {
        super(line);
        this.tok = tok;
    }
}
