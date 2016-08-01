package sjava.compiler;

import gnu.bytecode.Method;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.ClassInfo;
import sjava.compiler.tokens.Token;

public class MethodInfo extends AMethodInfo {
    public MethodInfo(ClassInfo ci, List<Token> toks, Method method, LinkedHashMap scope) {
        super(ci, toks, method, scope);
    }
}
