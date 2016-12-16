package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class LabelToken extends Token {
    public List<Token> toks;

    public LabelToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
