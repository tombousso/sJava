package sjava.compiler.tokens;

import sjava.compiler.tokens.BlockToken2;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class SpecialBeginToken extends BlockToken2 {
    public SpecialBeginToken(int line, ImList<Token> toks) {
        super(line, toks);
    }
}
