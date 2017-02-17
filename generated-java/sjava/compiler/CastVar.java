package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AVar;
import sjava.compiler.handlers.GenHandler;

public class CastVar extends AVar {
    public AVar v;

    public CastVar(AVar v, Type t) {
        super(t);
        this.v = v;
    }

    public Type load(CodeAttr code) {
        return GenHandler.castMaybe(code, this.v.load(code), super.type);
    }

    public void store(CodeAttr code) {
        this.v.store(code);
    }
}
