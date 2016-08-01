package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class DefaultToken extends Token implements Transformed {
    public DefaultToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
