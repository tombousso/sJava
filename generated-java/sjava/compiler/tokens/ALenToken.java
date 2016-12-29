package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class ALenToken extends Token {
    public Token tok;

    public ALenToken(int line, Token tok) {
        super(line);
        this.tok = tok;
    }
}
