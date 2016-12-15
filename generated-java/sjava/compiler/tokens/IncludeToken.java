package sjava.compiler.tokens;

import sjava.compiler.AMethodInfo;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class IncludeToken extends LexedParsedToken {
    public AMethodInfo mi;
    public Token ret;

    public IncludeToken(int line, AMethodInfo mi) {
        super(line);
        this.mi = mi;
    }
}
