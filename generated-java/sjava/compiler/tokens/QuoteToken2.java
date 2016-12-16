package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class QuoteToken2 extends Token {
    public Token tok;

    public QuoteToken2(int line, Token tok) {
        super(line);
        this.tok = tok;
    }
}
