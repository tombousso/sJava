package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class ReturnToken extends Token {
    public List<Token> toks;

    public ReturnToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
