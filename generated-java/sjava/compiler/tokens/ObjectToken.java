package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.Collection;
import sjava.compiler.AVar;
import sjava.compiler.ClassInfo;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class ObjectToken extends Token {
    public ClassInfo ci;
    public Collection<AVar> captured;
    public Type t;
    public ImList<LexedParsedToken> toks;
    public ImList<LexedParsedToken> superArgs;

    public ObjectToken(int line, Type t, ImList<LexedParsedToken> superArgs, ImList<LexedParsedToken> toks) {
        super(line);
        this.t = t;
        this.superArgs = superArgs;
        this.toks = toks;
    }
}
