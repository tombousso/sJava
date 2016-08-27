package sjava.compiler.tokens;

import sjava.compiler.MethodInfo;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class IncludeToken extends Token implements Transformed {
    public MethodInfo mi;
    public Token ret;

    public IncludeToken(int line, MethodInfo mi) {
        super(line);
        this.mi = mi;
    }
}
