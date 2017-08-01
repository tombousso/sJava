package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class ArrayToken extends ParsedToken {
    public List<LexedParsedToken> toks;

    public ArrayToken(int line, List<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((LexedParsedToken)this.toks.get(0));
        sb.append("[");
        sb.append(this.toksString(this.toks.subList(1, this.toks.size())));
        sb.append("]");
        return sb.toString();
    }

    public ArrayToken() {
    }
}
