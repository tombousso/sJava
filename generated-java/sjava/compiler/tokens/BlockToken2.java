package sjava.compiler.tokens;

import java.util.HashMap;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class BlockToken2 extends Token {
    public ImList<Token> toks;
    public HashMap labels;
    public boolean isTransformed;

    public BlockToken2(int line, ImList<Token> toks) {
        super(line);
        this.toks = toks;
        this.labels = new HashMap();
    }
}
