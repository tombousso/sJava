package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.ParsedToken;
import sjava.compiler.tokens.Token;

public class QuoteToken extends ParsedToken {
    public QuoteToken(int line, List<Token> toks) {
        super(line, toks);
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
