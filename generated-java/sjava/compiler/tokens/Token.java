package sjava.compiler.tokens;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import java.util.Iterator;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

public class Token extends Emitter {
    public int line;
    public int endLine;
    public List<Token> toks;

    Token(int line) {
        this();
        this.line = line;
    }

    public Token(int line, List<Token> toks) {
        this(line);
        this.toks = toks;
    }

    String toksString() {
        StringBuffer s = new StringBuffer();
        List iterable = this.toks;
        Iterator it = iterable.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            Token tok = (Token)it.next();
            s.append(tok);
            if(i != this.toks.size() - 1) {
                s.append(" ");
            }
        }

        return s.toString();
    }

    public Type emit(GenHandler h, AMethodInfo mi, CodeAttr code, Type needed) {
        return h.compile(this, mi, code, needed);
    }

    public int firstLine() {
        return this.line;
    }

    public int lastLine() {
        return this.endLine;
    }

    public Token() {
    }
}
