package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AMethodInfo;
import sjava.compiler.AVar;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.Handler;

public class LoadAVar extends Emitter {
    AVar avar;

    public LoadAVar(AVar avar) {
        this.avar = avar;
    }

    public Type emit(Handler h, AMethodInfo mi, CodeAttr code, Type needed) {
        return this.avar.load(code);
    }
}
