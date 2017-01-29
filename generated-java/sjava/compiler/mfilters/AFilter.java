package sjava.compiler.mfilters;

import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
            LinkedHashSet iterable = Main.superTypes(this.pt);
            Iterator it = iterable.iterator();

            for(int notused = 0; it.hasNext(); ++notused) {
                Type t = (Type)it.next();
                this.search(t);
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
