package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;
import sjava.compiler.tokens.Token;

public class GenericToken extends ParsedToken {
    public LexedParsedToken tok;
    public List<LexedParsedToken> toks;

    public GenericToken(int line, LexedParsedToken tok, List<LexedParsedToken> toks) {
        super(line);
        this.tok = tok;
        this.toks = toks;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.tok);
        sb.append("{");
        sb.append(Token.toksString(this.toks));
        sb.append("}");
        return sb.toString();
    }

    public GenericToken() {
    }
}
