package sjava.compiler.tokens;

import java.util.Collections;
import sjava.compiler.tokens.BlockToken2;

public class EmptyToken extends BlockToken2 {
    public EmptyToken(int line) {
        super(line, Collections.EMPTY_LIST);
    }
}
