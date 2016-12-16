package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;

public class ParsedToken extends LexedParsedToken {
    public ParsedToken(int line) {
        super(line);
    }

    public ParsedToken(int line, List<LexedParsedToken> toks) {
        super(line, toks);
    }

    public ParsedToken() {
    }
}
