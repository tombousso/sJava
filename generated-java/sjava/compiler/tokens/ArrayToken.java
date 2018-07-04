package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;
import sjava.compiler.tokens.Token;

public class ArrayToken extends ParsedToken {
    public ImList<LexedParsedToken> toks;

    public ArrayToken(int line, ImList<LexedParsedToken> toks) {
        super(line);
        this.toks = new ImList(toks);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((LexedParsedToken)this.toks.get(0));
        sb.append("[");
        sb.append(Token.toksString(this.toks.skip(1)));
        sb.append("]");
        return sb.toString();
    }

    public ArrayToken() {
    }
}
