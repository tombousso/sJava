package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.AVar;
import sjava.compiler.ClassInfo;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class ObjectToken extends Token {
    public ClassInfo ci;
    public AVar[] captured;
    public Type t;
    Type[] params;
    public List<LexedParsedToken> toks;

    public ObjectToken(int line, List<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }
}
