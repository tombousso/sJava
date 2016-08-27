package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import gnu.bytecode.Variable;
import sjava.compiler.AVar;

class Var extends AVar {
    Variable var;

    Var(Variable var, Type type) {
        super(type);
        this.var = var;
    }

    public Type load(CodeAttr code) {
        boolean output = code != null;
        if(output) {
            code.emitLoad(this.var);
        }

        return super.type;
    }

    public void store(CodeAttr code) {
        boolean output = code != null;
        if(output) {
            code.emitStore(this.var);
        }

    }
}
