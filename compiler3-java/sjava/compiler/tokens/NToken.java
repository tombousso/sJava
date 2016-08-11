package sjava.compiler.tokens;

import org.apache.commons.lang3.math.NumberUtils;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class NToken extends Token implements Transformed {
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
