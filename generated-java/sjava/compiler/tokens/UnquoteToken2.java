package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class UnquoteToken2 extends Token {
    public Token tok;
    public boolean var;

    public UnquoteToken2(int line, Token tok, boolean var) {
        super(line);
        this.tok = tok;
        this.var = var;
    }
}
