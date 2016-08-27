package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class MacroCallToken extends Token implements Transformed {
    public Token ret;

    public MacroCallToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
