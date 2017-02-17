package sjava.compiler.mfilters;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.PrimType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import sjava.compiler.Main;
import sjava.compiler.mfilters.AFilter;
import sjava.compiler.mfilters.MethodCall;

public class MFilter extends AFilter {
    ArrayList<MethodCall> methods = new ArrayList();
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
            MethodCall mc = isCompatible(method, generic, this.types);
            if(mc != null) {
                this.methods.add(mc);
            }
        }

    }

    public MethodCall getMethodCall() {
        ArrayList iterable = this.methods;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            MethodCall method = (MethodCall)it.next();
            if(method.mostSpecific(this.methods)) {
                return method;
            }
        }

        return (MethodCall)null;
    }

    public Method getMethod() {
        MethodCall mc = this.getMethodCall();
        return mc == null?(Method)null:mc.m;
    }

    public static MethodCall isCompatible(Method method, Type generic, Type[] types) {
        boolean varargs = (method.getModifiers() & Access.TRANSIENT) != 0;
        int na = types.length;
        Type[] params = method.getGenericParameterTypes();
        int np = params.length;
        MethodCall var21;
        if(na != np && (!varargs || na < np - 1)) {
            var21 = (MethodCall)null;
        } else {
            boolean arrayNeeded = varargs && (na == np - 1 || Main.arrayDim(params[np - 1]) != Main.arrayDim(types[np - 1]));
            Type[] var10000;
            if(arrayNeeded) {
                Type[] ntypes = new Type[np];
                boolean var9 = na == np - 1;
                System.arraycopy(types, 0, ntypes, 0, np - 1);
                ntypes[np - 1] = (Type)(var9?params[np - 1]:new ArrayType(types[np - 1]));
                var10000 = ntypes;
            } else {
                var10000 = types;
            }

            Type[] reals = var10000;
            TypeVariable[] var20;
            if(method.getName().equals("<init>")) {
                TypeVariable[] ctparams = ((ClassType)generic.getRawType()).getTypeParameters();
                TypeVariable[] mtparams = method.getTypeParameters();
                if(ctparams == null) {
                    var20 = mtparams;
                } else if(mtparams == null) {
                    var20 = ctparams;
                } else {
                    TypeVariable[] o = new TypeVariable[ctparams.length + mtparams.length];
                    System.arraycopy(ctparams, 0, o, 0, ctparams.length);
                    System.arraycopy(mtparams, 0, o, ctparams.length, mtparams.length);
                    var20 = o;
                }
            } else {
                var20 = method.getTypeParameters();
            }

            TypeVariable[] tparams = var20;
            Map tvs = Main.unresolveTvs(tparams, params, reals);
            boolean stop = false;

            for(int i = 0; !stop && i != types.length; ++i) {
                Type at = Main.resolveType(tvs, generic, arrayNeeded && i >= np - 1?((ArrayType)params[np - 1]).elements:params[i]);
                int level = Main.compare(at, types[i]);
                if(level < 0 || types[i] == Type.nullType && at instanceof PrimType) {
                    stop = true;
                }
            }

            var21 = !stop?new MethodCall(method, generic, tvs, types):(MethodCall)null;
        }

        return var21;
    }
}
