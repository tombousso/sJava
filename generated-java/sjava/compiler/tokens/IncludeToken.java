package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class IncludeToken extends LexedParsedToken {
    public AMethodInfo mi;
    public Token ret;

    public IncludeToken(int line, List<LexedParsedToken> toks) {
        super(line, toks);
    }
}
