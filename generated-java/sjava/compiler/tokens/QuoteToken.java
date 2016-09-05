package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class QuoteToken extends Token {
    public QuoteToken(int line, List<Token> toks) {
        super(line, toks);
        super.neverTransform = true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("`");
        sb.append((Token)super.toks.get(0));
        return sb.toString();
    }

    public QuoteToken() {
    }
}
