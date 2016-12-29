package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import java.util.Arrays;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

public class Emitters extends Emitter {
    public List<Emitter> emitters;

    public Emitters(Emitter[] emitters) {
        this.emitters = Arrays.asList(emitters);
    }

    public Emitters(List emitters) {
        this.emitters = emitters;
    }

    public Type[] emitAll(GenHandler h, AMethodInfo mi, CodeAttr code, Object needed) {
        return Emitter.emitAll(this.emitters, h, mi, code, needed);
    }
}
