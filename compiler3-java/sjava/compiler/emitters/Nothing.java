package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AMethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

public class Nothing extends Emitter {
    public static Nothing inst = new Nothing();

    public Type emit(GenHandler h, AMethodInfo mi, CodeAttr code, Type needed) {
        return Type.voidType;
    }
}
