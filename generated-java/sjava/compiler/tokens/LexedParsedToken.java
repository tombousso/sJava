package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class LexedParsedToken extends Token {
    public LexedParsedToken(int line) {
        super(line);
    }

    public LexedParsedToken(int line, List<Token> toks) {
        super(line, toks);
    }

    public LexedParsedToken() {
    }
}
