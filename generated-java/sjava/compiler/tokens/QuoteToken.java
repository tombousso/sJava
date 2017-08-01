package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class QuoteToken extends ParsedToken {
    public List<LexedParsedToken> toks;

    public QuoteToken(int line, List<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("`");
        sb.append((LexedParsedToken)this.toks.get(0));
        return sb.toString();
    }

    public QuoteToken() {
    }
}
