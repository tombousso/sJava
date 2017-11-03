package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AVar;

public class Arg extends AVar {
    public int n;
    public int level;

    public Arg(Type type, int n, int level) {
        super(type);
        this.n = n;
        this.level = level;
    }

    public Type load(CodeAttr code) {
        boolean output = code != null;
        if(output) {
            code.emitLoad(code.getArg(this.n));
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
