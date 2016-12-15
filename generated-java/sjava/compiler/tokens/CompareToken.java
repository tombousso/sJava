package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class CompareToken extends Token {
    public CompareToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
