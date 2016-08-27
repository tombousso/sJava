package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;

public class AVar {
    public Type type;

    AVar(Type type) {
        this.type = type;
    }

    public Type load(CodeAttr code) {
        return this.type;
    }

    public void store(CodeAttr var1) {
    }
}
