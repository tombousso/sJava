package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.Token;

public class WhileToken extends BlockToken {
    public WhileToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
