package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class ThrowToken extends Token {
    public ThrowToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
