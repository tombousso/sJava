package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AMethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

class Add1 extends Emitter {
    Emitter e;

    Add1(Emitter e) {
        this.e = e;
    }

    public Type emit(GenHandler h, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        this.e.emit(h, mi, code, needed);
        if(output) {
            code.emitPushInt(1);
        }

        if(output) {
            code.emitAdd(Type.intType);
        }

        return Type.intType;
    }
}
