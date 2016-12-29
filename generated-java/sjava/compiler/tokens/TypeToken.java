package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class TypeToken extends Token {
    public Token tok;

    public TypeToken(int line, Token tok) {
        super(line);
        this.tok = tok;
    }
}
