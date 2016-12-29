package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.BlockToken2;
import sjava.compiler.tokens.Token;

public class BeginToken extends BlockToken2 {
    public BeginToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
