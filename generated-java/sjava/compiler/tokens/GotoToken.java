package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class GotoToken extends Token implements Transformed {
    public GotoToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
