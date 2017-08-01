package sjava.compiler.tokens;

import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ParsedToken;

public class ColonToken extends ParsedToken {
    public LexedParsedToken left;
    public LexedParsedToken right;

    public ColonToken(int line, LexedParsedToken left, LexedParsedToken right) {
        super(line);
        this.left = left;
        this.right = right;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.left);
        sb.append(":");
        sb.append(this.right);
        return sb.toString();
    }

    public int firstLine() {
        return this.left.firstLine();
    }

    public ColonToken() {
    }
}
