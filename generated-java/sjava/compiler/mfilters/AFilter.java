package sjava.compiler.mfilters;

import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.Type;
import java.util.ArrayList;
import java.util.HashSet;
import sjava.compiler.Main;

public abstract class AFilter {
    Type pt;
    HashSet<String> foundSigs;

    AFilter(Type pt) {
        this.pt = pt;
        this.foundSigs = new HashSet();
    }

    abstract void select(Method var1, Type var2);

    void search(Type t) {
        for(Method m = ((ClassType)t.getRawType()).getDeclaredMethods(); m != null; m = m.getNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append(m.getName());
            sb.append(m.getSignature());
            String msig = sb.toString();
            if(!this.foundSigs.contains(msig)) {
                this.foundSigs.add(msig);
                this.select(m, t);
            }
        }

    }

    public void searchAll() {
        if(this.pt instanceof ArrayType) {
            this.searchArray();
        } else {
            Type t = this.pt;

            ArrayList supers;
            for(supers = new ArrayList(); t != null; t = Main.resolveType(t, ((ClassType)t.getRawType()).getGenericSuperclass())) {
                this.search(t);
                supers.add(t);
            }

            for(int i = 0; i != supers.size(); ++i) {
                Type superC = (Type)supers.get(i);
                this.searchIntfs(superC, ((ClassType)superC.getRawType()).getGenericInterfaces());
            }
        }

    }

    void searchIntfs(Type sub, Type[] intfs) {
        int j = 0;
        if(intfs != null) {
            while(j != intfs.length) {
                Type gintf = Main.resolveType(sub, intfs[j]);
                this.search(gintf);
                this.searchIntfs(gintf, ((ClassType)gintf.getRawType()).getGenericInterfaces());
                ++j;
            }
        }

    }

    public void searchDeclared() {
        this.search(this.pt);
    }

    void searchArray() {
        this.search(Type.objectType);
    }
}
