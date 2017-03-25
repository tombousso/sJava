package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.BlockToken2;
import sjava.compiler.tokens.Token;

public class SpecialBeginToken extends BlockToken2 {
    public SpecialBeginToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
