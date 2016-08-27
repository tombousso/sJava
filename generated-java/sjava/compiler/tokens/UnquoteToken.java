package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class UnquoteToken extends Token {
    public boolean s;

    public UnquoteToken(int line, List<Token> toks, boolean s) {
        super(line, toks);
        this.s = s;
        super.alwaysTransform = true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.s?",$":",");
        sb.append(((Token)super.toks.get(0)).toString());
        return sb.toString();
    }

    public UnquoteToken() {
    }
}
