package sjava.compiler.tokens;

import java.util.HashMap;
import java.util.List;
import sjava.compiler.tokens.Token;

public class BlockToken extends Token {
    public transient HashMap labels = new HashMap();

    public BlockToken(int line, List<Token> toks) {
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
        return super.toks.size() == 0?super.line:((Token)super.toks.get(0)).firstLine();
    }

    public int lastLine() {
        return super.toks.size() == 0?super.line:((Token)super.toks.get(super.toks.size() - 1)).lastLine();
    }

    public BlockToken() {
    }
}
