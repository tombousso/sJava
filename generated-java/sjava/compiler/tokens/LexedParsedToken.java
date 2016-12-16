package sjava.compiler.tokens;

import java.util.Iterator;
import java.util.List;
import sjava.compiler.tokens.Token;

public class LexedParsedToken extends Token {
    public List<LexedParsedToken> toks;

    public LexedParsedToken(int line) {
        super(line);
    }

    public LexedParsedToken(int line, List<LexedParsedToken> toks) {
        super(line);
        this.toks = toks;
    }

    public String toksString() {
        StringBuffer s = new StringBuffer();
        List iterable = this.toks;
        Iterator it = iterable.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            LexedParsedToken tok = (LexedParsedToken)it.next();
            s.append(tok);
            if(i != this.toks.size() - 1) {
                s.append(" ");
            }
        }

        return s.toString();
    }

    public int firstLine() {
        return super.line;
    }

    public int lastLine() {
        return super.endLine;
    }

    public LexedParsedToken() {
    }
}
