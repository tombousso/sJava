package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.Collection;
import java.util.List;
import sjava.compiler.AVar;
import sjava.compiler.ClassInfo;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class ObjectToken extends Token {
    public ClassInfo ci;
    public Collection<AVar> captured;
    public Type t;
    public List<LexedParsedToken> toks;
    public List<LexedParsedToken> superArgs;

    public ObjectToken(int line, Type t, List<LexedParsedToken> superArgs, List<LexedParsedToken> toks) {
        super(line);
        this.t = t;
        this.superArgs = superArgs;
        this.toks = toks;
    }
}
