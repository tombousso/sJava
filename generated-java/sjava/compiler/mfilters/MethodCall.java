package sjava.compiler.mfilters;

import gnu.bytecode.Method;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.util.Map;
import sjava.compiler.Main;

public class MethodCall {
    public Method m;
    public Type t;
    public Map<TypeVariable, Type> tvs;

    MethodCall(Method m, Type t, Map<TypeVariable, Type> tvs) {
        this.m = m;
        this.t = t;
        this.tvs = tvs;
    }

    boolean moreSpecific(MethodCall o) {
        Type[] as = this.m.getGenericParameterTypes();
        Type[] bs = o.m.getGenericParameterTypes();
        int n = Math.min(as.length, bs.length);
        boolean good = false;

        for(int i = 0; i < n; ++i) {
            Type a = Main.resolveType(this.tvs, this.t, as[i]);
            Type b = Main.resolveType(o.tvs, o.t, bs[i]);
            int comp = Main.compare(b, a);
            if(comp == 1) {
                good = true;
            } else if(comp < 0) {
                return false;
            }
        }

        return good || bs.length >= as.length;
    }

    public String toString() {
        return this.m.toString();
    }
}
