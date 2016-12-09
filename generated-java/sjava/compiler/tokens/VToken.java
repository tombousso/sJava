package sjava.compiler.tokens;

import sjava.compiler.tokens.LexedToken;
import sjava.compiler.tokens.Transformed;

public class VToken extends LexedToken implements Transformed {
    public String val;
    public transient int macro;

    VToken(int line, String val, int macro) {
        super(line);
        this.val = val;
        this.macro = macro;
    }

    public VToken(int line, String val) {
        this(line, val, 0);
    }

    public String toString() {
        return this.val;
    }

    public VToken() {
    }
}
