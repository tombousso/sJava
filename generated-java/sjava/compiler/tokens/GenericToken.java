package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.ParsedToken;
import sjava.compiler.tokens.Token;

public class GenericToken extends ParsedToken {
    public Token tok;

    public GenericToken(int line, Token tok, List<Token> toks) {
        super(line, toks);
        this.tok = tok;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.tok);
        sb.append("{");
        sb.append(this.toksString());
        sb.append("}");
        return sb.toString();
    }

    public GenericToken() {
    }
}
