package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.ObjectToken;
import sjava.compiler.tokens.Token;

public class LambdaToken extends ObjectToken {
    public LambdaToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
