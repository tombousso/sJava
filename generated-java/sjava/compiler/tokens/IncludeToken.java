package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class IncludeToken extends Token {
    public AMethodInfo mi;
    public Token ret;
    public List<LexedParsedToken> toks;

    public IncludeToken(int line, List<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }
}
