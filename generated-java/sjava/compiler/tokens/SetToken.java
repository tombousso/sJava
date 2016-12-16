package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class SetToken extends Token {
    public List<Token> toks;

    public SetToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
