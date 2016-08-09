package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class ASetToken extends Token implements Transformed {
    public ASetToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
