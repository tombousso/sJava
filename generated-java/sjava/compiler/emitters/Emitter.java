package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import java.util.Iterator;
import java.util.List;
import sjava.compiler.handlers.GenHandler;

public abstract class Emitter {
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

    public abstract Type emit(GenHandler var1, CodeAttr var2, Type var3);
}
