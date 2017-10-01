package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AVar;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

public class LoadAVar extends Emitter {
    AVar avar;

    public LoadAVar(AVar avar) {
        this.avar = avar;
    }

    public Type emit(GenHandler h, CodeAttr code, Type needed) {
        return this.avar.load(code);
    }
}
