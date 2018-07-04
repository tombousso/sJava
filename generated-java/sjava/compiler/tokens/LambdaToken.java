package sjava.compiler.tokens;

import gnu.bytecode.Method;
import gnu.bytecode.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.ObjectToken;

public class LambdaToken extends ObjectToken {
    public Method sam;
    public LinkedHashMap scope;
    public List<Type> params;

    public LambdaToken(int line, Type t, LinkedHashMap scope, List<Type> params, ImList<LexedParsedToken> toks, Method sam) {
        super(line, t, new ImList(Collections.EMPTY_LIST), toks);
        this.scope = scope;
        this.params = params;
        this.sam = sam;
    }
}
