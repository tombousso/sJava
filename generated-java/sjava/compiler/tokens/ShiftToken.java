package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class ShiftToken extends Token {
    public boolean right;
    public List<Token> toks;

    public ShiftToken(int line, List<Token> toks, boolean right) {
        super(line);
        this.right = right;
        this.toks = toks;
    }
}
