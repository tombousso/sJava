package sjava.compiler.tokens;

import java.util.Iterator;
import java.util.List;
import sjava.compiler.tokens.Token;

public class LexedParsedToken extends Token {
    public List<LexedParsedToken> toks;
    public int endLine;
    public transient Token transformed;

    public LexedParsedToken(int line) {
        super(line);
    }

    public LexedParsedToken(int line, List<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }

    String toksString(List<LexedParsedToken> l) {
        StringBuffer s = new StringBuffer();
        Iterator it = l.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            LexedParsedToken tok = (LexedParsedToken)it.next();
            s.append(tok);
            if(i != l.size() - 1) {
                s.append(" ");
            }
        }

        return s.toString();
    }

    String toksString() {
        return this.toksString(this.toks);
    }

    public int firstLine() {
        return super.line;
    }

    public int lastLine() {
        return this.endLine;
    }

    public LexedParsedToken() {
    }
}
