package sjava.compiler.tokens;

import org.apache.commons.lang3.math.NumberUtils;
import sjava.compiler.tokens.LexedToken;

public class NToken extends LexedToken {
    public Number val;

    public NToken(int line, String sval) {
        super(line);
        this.val = NumberUtils.createNumber(sval);
    }

    public String toString() {
        return this.val.toString();
    }

    public NToken() {
    }
}
