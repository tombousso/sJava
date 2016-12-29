package sjava.compiler.tokens;

import org.apache.commons.lang3.math.NumberUtils;
import sjava.compiler.tokens.LexedToken;

public class NToken extends LexedToken {
    public Number val;
    public String sval;

    public NToken(int line, String sval) {
        super(line);
        this.sval = sval;
        if(sval.contains(".") && !Character.isLetter(sval.charAt(sval.length() - 1))) {
            StringBuilder sb = new StringBuilder();
            sb.append(sval);
            sb.append("d");
            sval = sb.toString();
        }

        this.val = NumberUtils.createNumber(sval);
    }

    public String toString() {
        return this.sval;
    }

    public NToken() {
    }
}
