package sjava.compiler.mfilters;

import gnu.bytecode.Method;
import gnu.bytecode.Type;
import sjava.compiler.Main;
import sjava.compiler.mfilters.AFilter;

public class BridgeFilter extends AFilter {
    Method m;

    public BridgeFilter(Method m) {
        super(m.getDeclaringClass());
        this.m = m;
    }

    void select(Method method, Type generic) {
        Type[] p1 = this.m.getGenericParameterTypes();
        Type[] p2 = method.getGenericParameterTypes();
        Type r1 = this.m.getReturnType();
        Type r2 = method.getReturnType();
        if(method.getName().equals(this.m.getName()) && p1.length == p2.length && !Type.isSame(generic, super.pt)) {
            int n = p1.length;
            boolean diff = !Type.isSame(r1.getRawType(), r2.getRawType());
            boolean overrides = true;

            for(int i = 0; overrides && i != n; ++i) {
                if(!diff && !Type.isSame(p1[i].getRawType(), p2[i].getRawType())) {
                    diff = true;
                }

                overrides = Type.isSame(Main.resolveType(generic, p1[i]), Main.resolveType(generic, p2[i]));
            }

            if(diff && overrides) {
                Main.generateBridgeMethod(this.m, p2, r2);
            }
        }

    }
}
