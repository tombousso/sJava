package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class GenericToken extends ParsedToken {
    public LexedParsedToken tok;

    public GenericToken(int line, LexedParsedToken tok, List<LexedParsedToken> toks) {
        super(line, toks);
        this.tok = tok;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.tok);
        sb.append("{");
        sb.append(this.toksString());
        sb.append("}");
        return sb.toString();
    }

    public GenericToken() {
    }
}
