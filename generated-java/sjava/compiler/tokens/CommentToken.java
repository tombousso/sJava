package sjava.compiler.tokens;

import sjava.compiler.tokens.LexedToken;

public class CommentToken extends LexedToken {
    public String val;

    public CommentToken(int line, String val) {
        super(line);
        this.val = val;
    }
}
