package sjava.compiler.mfilters;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.PrimType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.util.ArrayList;
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
                TypeVariable[] var22;
                if(this.name.equals("<init>")) {
                    TypeVariable[] ctparams = ((ClassType)generic.getRawType()).getTypeParameters();
                    TypeVariable[] mtparams = method.getTypeParameters();
                    if(ctparams == null) {
                        var22 = mtparams;
                    } else if(mtparams == null) {
                        var22 = ctparams;
                    } else {
                        TypeVariable[] o = new TypeVariable[ctparams.length + mtparams.length];
                        System.arraycopy(ctparams, 0, o, 0, ctparams.length);
                        System.arraycopy(mtparams, 0, o, ctparams.length, mtparams.length);
                        var22 = o;
                    }
                } else {
                    var22 = method.getTypeParameters();
                }

                TypeVariable[] tparams = var22;
                Map tvs = Main.unresolveTvs(tparams, params, reals);
                boolean stop = false;

                for(int i = 0; !stop && i != this.types.length; ++i) {
                    Type at = Main.resolveType(tvs, generic, arrayNeeded && i >= np - 1?((ArrayType)params[np - 1]).elements:params[i]);
                    int level = at.compare(this.types[i]);
                    if(level < 0 || this.types[i] == Type.nullType && at instanceof PrimType) {
                        stop = true;
                    }
                }

                MethodCall mc = new MethodCall(method, generic, tvs);
                if(!stop) {
                    this.methods.add(mc);
                }
            }
        }

    }

    public MethodCall getMethodCall() {
        int n = this.methods.size();
        if(n == 0) {
            return null;
        } else if(n == 1) {
            return (MethodCall)this.methods.get(0);
        } else {
            MethodCall found = (MethodCall)null;

            label38:
            for(int i = 0; i < n; ++i) {
                MethodCall a = (MethodCall)this.methods.get(i);

                for(int j = 0; j < n; ++j) {
                    MethodCall b = (MethodCall)this.methods.get(j);
                    if(i != j && !a.moreSpecific(b)) {
                        continue label38;
                    }
                }

                if(found != null) {
                    return null;
                }

                found = a;
            }

            return found;
        }
    }

    public Method getMethod() {
        MethodCall mc = this.getMethodCall();
        return mc == null?(Method)null:mc.m;
    }
}
