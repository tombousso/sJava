package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class CommentToken extends Token implements Transformed {
    public String val;

    public CommentToken(int line, String val) {
        super(line);
        this.val = val;
    }
}
