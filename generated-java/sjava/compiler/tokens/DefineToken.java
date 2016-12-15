package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class DefineToken extends Token {
    public DefineToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
