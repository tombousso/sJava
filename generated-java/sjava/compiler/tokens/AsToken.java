package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class AsToken extends Token {
    public AsToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
