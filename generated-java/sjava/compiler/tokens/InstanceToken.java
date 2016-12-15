package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class InstanceToken extends Token {
    public InstanceToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
