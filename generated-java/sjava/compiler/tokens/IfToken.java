package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class IfToken extends Token {
    public List<Token> toks;

    public IfToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
