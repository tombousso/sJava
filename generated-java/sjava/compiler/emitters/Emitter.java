package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import java.util.Iterator;
import java.util.List;
import sjava.compiler.handlers.GenHandler;

public class Emitter {
    public Type emit(GenHandler h, CodeAttr code, Type needed) {
        Type[] types = this.emitAll(h, code, needed);
        Object var10000;
        if(types == null) {
            var10000 = Type.voidType;
        } else {
            int l = types.length;
            var10000 = l == 0?Type.voidType:types[l - 1];
        }

        return (Type)var10000;
    }

    public Type[] emitAll(GenHandler h, CodeAttr code, Object needed) {
        return new Type[]{this.emit(h, code, (Type)needed)};
    }

    public static Type[] emitAll(List<Emitter> emitters, GenHandler h, CodeAttr code, Object needed) {
        Type[] types = new Type[emitters.size()];
        Iterator it = emitters.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            Emitter emitter = (Emitter)it.next();
            if(emitter != null) {
                types[i] = emitter.emit(h, code, needed instanceof Type[]?((Type[])needed)[i]:(Type)needed);
            }
        }

        return types;
    }
}
