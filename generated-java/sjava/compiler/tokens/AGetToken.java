package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class AGetToken extends Token {
    public List<Token> toks;

    public AGetToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
