package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class QuoteToken extends Token {
    public boolean transform;

    public QuoteToken(int line, List<Token> toks, boolean transform) {
        super(line, toks);
        super.neverTransform = true;
        this.transform = transform;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.transform?"`":"~");
        sb.append((Token)super.toks.get(0));
        return sb.toString();
    }

    public QuoteToken() {
    }
}
