package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class NumOpToken extends Token {
    public String op;
    public ImList<Token> toks;

    public NumOpToken(int line, String op, ImList<Token> toks) {
        super(line);
        this.op = op;
        this.toks = toks;
    }
}
