package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import java.util.Arrays;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.Handler;

public class Emitters extends Emitter {
    public List<Emitter> emitters;

    public Emitters(Emitter[] emitters) {
        this.emitters = Arrays.asList(emitters);
    }

    public Emitters(List emitters) {
        this.emitters = emitters;
    }

    public Type[] emitAll(Handler h, AMethodInfo mi, CodeAttr code, Object needed) {
        Type[] types = new Type[this.emitters.size()];

        for(int i = 0; i < types.length; ++i) {
            Emitter emitter = (Emitter)this.emitters.get(i);
            if(emitter != null) {
                types[i] = emitter.emit(h, mi, code, needed instanceof Type[]?((Type[])needed)[i]:(Type)needed);
            }
        }

        return types;
    }
}
