package sjava.compiler.tokens;

import sjava.compiler.tokens.LexedToken;
import sjava.compiler.tokens.Transformed;

public class ConstToken extends LexedToken implements Transformed {
    public String val;

    public ConstToken(int line, String val) {
        super(line);
        this.val = val;
    }

    public String toString() {
        return this.val;
    }

    public ConstToken() {
    }
}
