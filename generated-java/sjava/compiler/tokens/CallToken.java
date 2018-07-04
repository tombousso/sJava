package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class CallToken extends Token {
    public Token target;
    public String method;
    public ImList<Token> toks;

    public CallToken(int line, Token target, String method, ImList<Token> toks) {
        super(line);
        this.target = target;
        this.method = method;
        this.toks = toks;
    }
}
