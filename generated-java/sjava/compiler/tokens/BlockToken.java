package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class BlockToken extends ParsedToken {
    public BlockToken(int line, List<LexedParsedToken> toks) {
        super(line, toks);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.toksString());
        sb.append(")");
        return sb.toString();
    }

    public int firstLine() {
        return super.toks.size() == 0?super.line:((LexedParsedToken)super.toks.get(0)).firstLine();
    }

    public int lastLine() {
        return super.toks.size() == 0?super.line:((LexedParsedToken)super.toks.get(super.toks.size() - 1)).lastLine();
    }

    public BlockToken() {
    }
}
