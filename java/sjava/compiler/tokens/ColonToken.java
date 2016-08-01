package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class ColonToken extends Token {
    public ColonToken(int line, List<Token> toks) {
        super(line, toks);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((Token)super.toks.get(0));
        sb.append(":");
        sb.append((Token)super.toks.get(1));
        return sb.toString();
    }

    public ColonToken() {
    }
}
