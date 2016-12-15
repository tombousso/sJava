package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class ClassToken extends Token {
    public ClassToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
