package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class CompareToken extends Token {
    public String compare;
    public List<Token> toks;

    public CompareToken(int line, String compare, List<Token> toks) {
        super(line);
        this.compare = compare;
        this.toks = toks;
    }
}
