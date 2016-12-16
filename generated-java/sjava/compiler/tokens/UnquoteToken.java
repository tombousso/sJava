package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class UnquoteToken extends ParsedToken {
    public boolean var;

    public UnquoteToken(int line, List<LexedParsedToken> toks, boolean var) {
        super(line, toks);
        this.var = var;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.var?",$":",");
        sb.append((LexedParsedToken)super.toks.get(0));
        return sb.toString();
    }

    public UnquoteToken() {
    }
}
