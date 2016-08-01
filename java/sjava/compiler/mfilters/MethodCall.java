package sjava.compiler.mfilters;

import gnu.bytecode.Method;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.util.Map;

public class MethodCall {
    public Method m;
    public Type t;
    public Map<TypeVariable, Type> tvs;

    MethodCall(Method m, Type t, Map<TypeVariable, Type> tvs) {
        this.m = m;
        this.t = t;
        this.tvs = tvs;
    }
}
