package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class NumOpToken extends Token {
    public List<Token> toks;

    public NumOpToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
