package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.BlockToken2;
import sjava.compiler.tokens.Token;

public class WhileToken extends BlockToken2 {
    public WhileToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
