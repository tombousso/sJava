package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.AVar;
import sjava.compiler.ClassInfo;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class ObjectToken extends Token implements Transformed {
    public ClassInfo ci;
    public AVar[] captured;
    public Type t;
    Type[] params;

    public ObjectToken(int line, List<Token> toks) {
        super(line, toks);
    }
}
