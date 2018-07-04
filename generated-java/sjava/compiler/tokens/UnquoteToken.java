package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class UnquoteToken extends ParsedToken {
    public boolean var;
    public ImList<LexedParsedToken> toks;

    public UnquoteToken(int line, ImList<LexedParsedToken> toks, boolean var) {
        super(line);
        this.toks = toks;
        this.var = var;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.var?",$":",");
        sb.append((LexedParsedToken)this.toks.get(0));
        return sb.toString();
    }

    public UnquoteToken() {
    }
}
