package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class ConstToken extends Token implements Transformed {
    public ConstToken(int line, String what) {
        super(line, 0, what);
    }

    public ConstToken() {
    }
}
