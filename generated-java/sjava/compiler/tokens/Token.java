package sjava.compiler.tokens;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

public class Token extends Emitter {
    public int line;

    Token(int line) {
        this();
        this.line = line;
    }

    public Type emit(GenHandler h, CodeAttr code, Type needed) {
        return h.compile(this, code, needed);
    }

    public Token() {
    }
}
