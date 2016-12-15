package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.Token;

public class BeginToken extends BlockToken {
    public BeginToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
