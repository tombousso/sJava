package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class ParsedToken extends LexedParsedToken {
    public ParsedToken(int line) {
        super(line);
    }

    public ParsedToken(int line, List<Token> toks) {
        super(line, toks);
    }

    public ParsedToken() {
    }
}
