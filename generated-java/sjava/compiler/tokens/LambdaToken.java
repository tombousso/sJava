package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ObjectToken;

public class LambdaToken extends ObjectToken {
    public LambdaToken(int line, List<LexedParsedToken> toks) {
        super(line, toks);
    }
}
