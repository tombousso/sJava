package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class VToken extends Token implements Transformed {
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

    public String javaName() {
        String var10000;
        StringBuilder sb;
        if(this.macro == 0) {
            sb = new StringBuilder();
            sb.append(this.val);
            var10000 = sb.toString();
        } else {
            sb = new StringBuilder();
            sb.append(this.val);
            sb.append("$");
            sb.append(this.macro);
            var10000 = sb.toString();
        }

        return var10000;
    }

    public String toString() {
        return this.val;
    }

    public VToken() {
    }
}
