package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class UnquoteToken extends Token {
    public boolean var;

    public UnquoteToken(int line, List<Token> toks, boolean var) {
        super(line, toks);
        this.var = var;
        super.alwaysTransform = true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.var?",$":",");
        sb.append(((Token)super.toks.get(0)).toString());
        return sb.toString();
    }

    public UnquoteToken() {
    }
}
