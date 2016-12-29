package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class ShiftToken extends Token {
    public boolean right;
    public Token tok;
    public Token amt;

    public ShiftToken(int line, Token tok, Token amt, boolean right) {
        super(line);
        this.tok = tok;
        this.amt = amt;
        this.right = right;
    }
}
