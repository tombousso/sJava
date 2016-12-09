package sjava.compiler.tokens;

import sjava.compiler.tokens.LexedToken;
import sjava.compiler.tokens.Transformed;

public class CommentToken extends LexedToken implements Transformed {
    public String val;

    public CommentToken(int line, String val) {
        super(line);
        this.val = val;
    }
}
