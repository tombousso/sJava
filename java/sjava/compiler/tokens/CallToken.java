package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class CallToken extends Token implements Transformed {
    public CallToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
