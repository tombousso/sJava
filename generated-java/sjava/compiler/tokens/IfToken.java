package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class IfToken extends Token {
    public IfToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
