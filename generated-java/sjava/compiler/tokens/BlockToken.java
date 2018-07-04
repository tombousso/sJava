package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;
import sjava.compiler.tokens.Token;

public class BlockToken extends ParsedToken {
    public ImList<LexedParsedToken> toks;

    public BlockToken(int line, ImList<LexedParsedToken> toks) {
        super(line);
        this.toks = new ImList(toks);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(Token.toksString(this.toks));
        sb.append(")");
        return sb.toString();
    }

    public int firstLine() {
        return this.toks.size() == 0?super.line:((LexedParsedToken)this.toks.get(0)).firstLine();
    }

    public int lastLine() {
        return this.toks.size() == 0?super.line:((LexedParsedToken)this.toks.get(this.toks.size() - 1)).lastLine();
    }

    public BlockToken() {
    }
}
