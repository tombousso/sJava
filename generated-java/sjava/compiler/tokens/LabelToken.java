package sjava.compiler.tokens;

import sjava.compiler.tokens.Token;

public class LabelToken extends Token {
    public String label;

    public LabelToken(int line, String label) {
        super(line);
        this.label = label;
    }
}
