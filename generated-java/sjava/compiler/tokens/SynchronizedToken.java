package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.Token;

public class SynchronizedToken extends BlockToken {
    public SynchronizedToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
