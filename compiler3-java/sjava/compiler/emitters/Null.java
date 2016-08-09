package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AMethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.Handler;

class Null extends Emitter {
    public Type emit(Handler h, AMethodInfo mi, CodeAttr code, Type needed) {
        if(code != null) {
            code.emitPushNull();
        }

        return Type.nullType;
    }
}
