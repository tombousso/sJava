package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class LexedParsedToken extends Token {
    public int endLine;
    public transient Token transformed;

    public LexedParsedToken(int line) {
        super(line);
    }

    public int firstLine() {
        return super.line;
    }

    public int lastLine() {
        return this.endLine;
    }

    public String toStringParsed() {
        return this.toString();
    }

    public LexedParsedToken() {
    }
}
