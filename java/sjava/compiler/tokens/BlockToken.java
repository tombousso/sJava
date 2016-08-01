package sjava.compiler.tokens;

import java.util.HashMap;
import java.util.List;
import sjava.compiler.tokens.Token;

public class BlockToken extends Token {
    public HashMap labels;

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

    public BlockToken() {
    }
}
