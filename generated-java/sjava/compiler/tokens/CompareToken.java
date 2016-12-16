package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class CompareToken extends Token {
    public List<Token> toks;

    public CompareToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
