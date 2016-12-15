package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class CallToken extends Token {
    public Token target;
    public String method;

    public CallToken(int line, Token target, String method, List<Token> toks) {
        super(line, toks);
        this.target = target;
        this.method = method;
    }
}
