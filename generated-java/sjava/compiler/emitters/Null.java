package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

class Null extends Emitter {
    public Type emit(GenHandler h, CodeAttr code, Type needed) {
        if(code != null) {
            code.emitPushNull();
        }

        return Type.nullType;
    }
}
