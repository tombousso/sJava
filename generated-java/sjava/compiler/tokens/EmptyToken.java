package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class EmptyToken extends Token implements Transformed {
    public EmptyToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
