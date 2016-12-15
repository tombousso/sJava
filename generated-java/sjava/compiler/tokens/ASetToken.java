package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class ASetToken extends Token {
    public ASetToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
