package sjava.compiler.tokens;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import java.util.Iterator;
import java.util.List;
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

    static String toksString(List l) {
        StringBuffer s = new StringBuffer();
        Iterator it = l.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            Object tok = it.next();
            s.append(tok);
            if(i != l.size() - 1) {
                s.append(" ");
            }
        }

        return s.toString();
    }

    public Token() {
    }
}
