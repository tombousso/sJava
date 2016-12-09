package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class LexedToken extends Token {
    public int prec;
    public String what;

    public LexedToken(int line) {
        super(line);
        this.what = "";
    }

    public LexedToken(int line, int prec, String what) {
        this(line);
        this.prec = prec;
        this.what = what;
    }

    public String toString() {
        return this.what;
    }

    public LexedToken() {
    }
}
