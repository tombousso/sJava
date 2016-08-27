package sjava.compiler.mfilters;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.Type;
import java.util.ArrayList;
import java.util.Map;
import sjava.compiler.Main;
import sjava.compiler.mfilters.AFilter;
import sjava.compiler.mfilters.MethodCall;

public class MFilter extends AFilter {
    ArrayList<MethodCall> methods0 = new ArrayList();
    ArrayList<MethodCall> methods1 = new ArrayList();
    ArrayList<MethodCall> varmethods = new ArrayList();
    String name;
    Type[] types;

    public MFilter(String name, Type[] types, Type pt) {
        super(pt);
        this.name = name;
        this.types = types;
    }

    void select(Method method, Type generic) {
        ClassType c = method.getDeclaringClass();
        if(method.getName().equals(this.name) && (!c.isInterface() || ((ClassType)generic.getRawType()).isInterface() || !method.isAbstract()) && 0 == (method.getModifiers() & Access.SYNTHETIC)) {
            boolean varargs = (method.getModifiers() & Access.TRANSIENT) != 0;
            int na = this.types.length;
            Type[] params = method.getGenericParameterTypes();
            int np = params.length;
            if(na == np || varargs && na >= np - 1) {
                boolean arrayNeeded = varargs && (na == np - 1 || Main.arrayDim(params[np - 1]) != Main.arrayDim(this.types[np - 1]));
                Type[] var10000;
                if(arrayNeeded) {
                    Type[] types = new Type[np];
                    boolean var10 = na == np - 1;
                    System.arraycopy(this.types, 0, types, 0, np - 1);
                    types[np - 1] = (Type)(var10?params[np - 1]:new ArrayType(this.types[np - 1]));
                    var10000 = types;
                } else {
                    var10000 = this.types;
                }

                Type[] reals = var10000;
                Map tvs = Main.unresolveTvs(method.getTypeParameters(), params, reals);
                boolean stop = false;
                int maxLevel = 0;

                for(int i = 0; !stop && i != this.types.length; ++i) {
                    Type at = Main.resolveType(tvs, generic, arrayNeeded && i >= np - 1?((ArrayType)params[np - 1]).elements:params[i]);
                    int level = at.compare(this.types[i]);
                    if(level > maxLevel) {
                        maxLevel = level;
                    }

                    if(level < 0) {
                        stop = true;
                    }
                }

                MethodCall mc = new MethodCall(method, generic, tvs);
                if(!stop) {
                    if(varargs) {
                        this.varmethods.add(mc);
                    } else if(maxLevel == 0) {
                        this.methods0.add(mc);
                    } else {
                        this.methods1.add(mc);
                    }
                }
            }
        }

    }

    public MethodCall getMethodCall() {
        return this.methods0.size() == 0?(this.methods1.size() == 0?(this.varmethods.size() == 0?(MethodCall)null:(MethodCall)this.varmethods.get(0)):(MethodCall)this.methods1.get(0)):(MethodCall)this.methods0.get(0);
    }

    public Method getMethod() {
        MethodCall mc = this.getMethodCall();
        return mc == null?(Method)null:mc.m;
    }
}
