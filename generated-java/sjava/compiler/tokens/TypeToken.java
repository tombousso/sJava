package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class TypeToken extends Token {
    public List<Token> toks;

    public TypeToken(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
    }
}
