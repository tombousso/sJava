package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ObjectToken;

public class LambdaFnToken extends ObjectToken {
    public Type ret;
    public LinkedHashMap scope;
    public List<Type> params;

    public LambdaFnToken(int line, Type t, LinkedHashMap scope, List<Type> params, ImList<LexedParsedToken> toks) {
        super(line, t, ImList.EMPTY_LIST, toks);
        this.scope = scope;
        this.params = params;
    }
}
