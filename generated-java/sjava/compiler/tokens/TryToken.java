package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class TryToken extends Token {
    public List<Token> toks;

    public TryToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
