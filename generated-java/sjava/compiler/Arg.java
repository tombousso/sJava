package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AVar;

public class Arg extends AVar {
    int n;

    public Arg(int n, Type type) {
        super(type);
        this.n = n;
    }

    public Type load(CodeAttr code) {
        boolean output = code != null;
        if(output) {
            code.emitLoad(code.getArg(this.n));
        }

        if(output) {
            code.emitCheckcast(super.type.getRawType());
        }

        return super.type;
    }

    public void store(CodeAttr code) {
        boolean output = code != null;
        if(output) {
            code.emitStore(code.getArg(this.n));
        }

    }
}
