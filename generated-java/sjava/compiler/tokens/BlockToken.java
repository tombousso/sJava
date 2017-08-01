package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class BlockToken extends ParsedToken {
    public List<LexedParsedToken> toks;

    public BlockToken(int line, List<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.toksString(this.toks));
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
