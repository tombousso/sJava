package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class TypeToken extends Token {
    public TypeToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
