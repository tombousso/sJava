package sjava.compiler.tokens;

import sjava.compiler.AMethodInfo;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class IncludeToken extends Token {
    public AMethodInfo mi;
    public Token ret;
    public ImList<LexedParsedToken> toks;

    public IncludeToken(int line, ImList<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }
}
