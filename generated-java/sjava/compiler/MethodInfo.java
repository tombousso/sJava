package sjava.compiler;

import gnu.bytecode.Method;
import gnu.bytecode.Type;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;

public class MethodInfo extends AMethodInfo {
    public MethodInfo(ClassInfo ci, ImList<LexedParsedToken> toks, LinkedHashMap<String, Arg> firstScope, Method method) {
        super(ci, toks, firstScope, method);
    }

    MethodInfo(ClassInfo ci, ImList<LexedParsedToken> toks, LinkedHashMap<String, Arg> firstScope, String name, List<Type> params, Type ret, int mods) {
        super(ci, toks, firstScope, name, params, ret, mods);
    }
}
