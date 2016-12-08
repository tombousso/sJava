package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class ShiftToken extends Token implements Transformed {
    public boolean right;

    public ShiftToken(int line, List<Token> toks, boolean right) {
        super(line, toks);
        this.right = right;
    }
}
