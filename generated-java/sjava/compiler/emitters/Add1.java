package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

public class Add1 extends Emitter {
    Emitter e;

    public Add1(Emitter e) {
        this.e = e;
    }

    public Type emit(GenHandler h, CodeAttr code, Type needed) {
        boolean output = code != null;
        this.e.emit(h, code, Type.intType);
        if(output) {
            code.emitPushInt(1);
        }

        if(output) {
            code.emitAdd(Type.intType);
        }

        return Type.intType;
    }
}
