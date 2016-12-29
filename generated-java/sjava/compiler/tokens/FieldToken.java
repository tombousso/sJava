package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class FieldToken extends Token {
    public Token left;
    public String right;

    public FieldToken(int line, Token left, String right) {
        super(line);
        this.left = left;
        this.right = right;
    }
}
