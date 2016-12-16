package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class ColonToken2 extends Token {
    public Token left;
    public Token right;

    public ColonToken2(int line, Token left, Token right) {
        super(line);
        this.left = left;
        this.right = right;
    }
}
