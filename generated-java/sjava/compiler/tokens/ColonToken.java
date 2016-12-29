package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class ColonToken extends ParsedToken {
    public ColonToken(int line, List<LexedParsedToken> toks) {
        super(line, toks);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((LexedParsedToken)super.toks.get(0));
        sb.append(":");
        sb.append((LexedParsedToken)super.toks.get(1));
        return sb.toString();
    }

    public int firstLine() {
        return ((LexedParsedToken)super.toks.get(0)).firstLine();
    }

    public ColonToken() {
    }
}
