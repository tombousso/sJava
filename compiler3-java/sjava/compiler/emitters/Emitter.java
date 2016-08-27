package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.AMethodInfo;
import sjava.compiler.handlers.GenHandler;

public class Emitter {
    public Type emit(GenHandler h, AMethodInfo mi, CodeAttr code, Type needed) {
        Type[] types = this.emitAll(h, mi, code, needed);
        Object var10000;
        if(types == null) {
            var10000 = Type.voidType;
        } else {
            int l = types.length;
            var10000 = l == 0?Type.voidType:types[l - 1];
        }

        return (Type)var10000;
    }

    public Type[] emitAll(GenHandler h, AMethodInfo mi, CodeAttr code, Object needed) {
        return new Type[]{this.emit(h, mi, code, (Type)needed)};
    }
}
