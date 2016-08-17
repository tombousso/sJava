package sjava.compiler.tokens;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import java.util.Iterator;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.Handler;

public class Token extends Emitter {
    public int line;
    public int endLine;
    public int prec;
    public String what;
    public List<Token> toks;
    public boolean alwaysTransform;
    public boolean neverTransform;
    public transient boolean transformed;

    public Token() {
        this.prec = -1;
        this.what = "";
        this.alwaysTransform = false;
        this.neverTransform = false;
        this.transformed = false;
    }

    Token(int line) {
        this();
        this.line = line;
    }

    Token(int line, int prec) {
        this(line);
        this.prec = prec;
    }

    public Token(int line, int prec, String what) {
        this(line, prec);
        this.what = what;
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

    public String toString() {
        return this.what;
    }

    public Type emit(Handler h, AMethodInfo mi, CodeAttr code, Type needed) {
        return h.compile(this, mi, code, needed);
    }
}
