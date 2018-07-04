package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class CompareToken extends Token {
    public String compare;
    public ImList<Token> toks;

    public CompareToken(int line, String compare, ImList<Token> toks) {
        super(line);
        this.compare = compare;
        this.toks = toks;
    }
}
