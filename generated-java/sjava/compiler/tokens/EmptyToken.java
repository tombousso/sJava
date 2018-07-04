package sjava.compiler.tokens;

import java.util.Collections;
import sjava.compiler.tokens.BlockToken2;
import sjava.compiler.tokens.ImList;

public class EmptyToken extends BlockToken2 {
    public EmptyToken(int line) {
        super(line, new ImList(Collections.EMPTY_LIST));
    }
}
