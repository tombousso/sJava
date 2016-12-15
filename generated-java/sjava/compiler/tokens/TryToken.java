package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class TryToken extends Token {
    public TryToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
