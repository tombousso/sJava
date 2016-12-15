package sjava.compiler.tokens;

import java.util.Collections;
import sjava.compiler.tokens.BlockToken;

public class EmptyToken extends BlockToken {
    public EmptyToken(int line) {
        super(line, Collections.EMPTY_LIST);
    }
}
