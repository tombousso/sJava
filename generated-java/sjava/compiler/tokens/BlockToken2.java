package sjava.compiler.tokens;

import java.util.HashMap;
import java.util.List;
import sjava.compiler.tokens.Token;

public class BlockToken2 extends Token {
    public HashMap labels;
    public List<Token> toks;
    public boolean isTransformed;

    public BlockToken2(int line, List<Token> toks) {
        super(line);
        this.toks = toks;
        this.labels = new HashMap();
    }
}
