package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class GenericToken extends ParsedToken {
    public GenericToken(int line, List<LexedParsedToken> toks) {
        super(line, toks);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((LexedParsedToken)super.toks.get(0));
        sb.append("{");
        sb.append(this.toksString(super.toks.subList(1, super.toks.size())));
        sb.append("}");
        return sb.toString();
    }

    public GenericToken() {
    }
}
