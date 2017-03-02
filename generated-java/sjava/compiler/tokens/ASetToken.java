package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class ASetToken extends Token {
    public Token array;
    public Token index;
    public Token el;

    public ASetToken(int line, Token array, Token index, Token el) {
        super(line);
        this.array = array;
        this.index = index;
        this.el = el;
    }
}
