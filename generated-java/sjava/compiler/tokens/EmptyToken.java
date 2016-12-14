package sjava.compiler.tokens;

import java.util.Collections;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.Transformed;

public class EmptyToken extends BlockToken implements Transformed {
    public EmptyToken(int line) {
        super(line, Collections.EMPTY_LIST);
    }
}
