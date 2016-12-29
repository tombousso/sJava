package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class ReturnToken extends Token {
    public Token tok;

    public ReturnToken(int line, Token tok) {
        super(line);
        this.tok = tok;
    }
}
