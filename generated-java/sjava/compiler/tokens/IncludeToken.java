package sjava.compiler.tokens;

import sjava.compiler.AMethodInfo;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class IncludeToken extends Token implements Transformed {
    public AMethodInfo mi;
    public Token ret;

    public IncludeToken(int line, AMethodInfo mi) {
        super(line);
        this.mi = mi;
    }
}
