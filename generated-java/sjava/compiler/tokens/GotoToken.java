package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class GotoToken extends Token {
    public String label;

    public GotoToken(int line, String label) {
        super(line);
        this.label = label;
    }
}
