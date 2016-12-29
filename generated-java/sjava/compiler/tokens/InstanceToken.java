package sjava.compiler.tokens;

import gnu.bytecode.Type;
import sjava.compiler.tokens.Token;

public class InstanceToken extends Token {
    public Token tok;
    public Type type;

    public InstanceToken(int line, Token tok, Type type) {
        super(line);
        this.tok = tok;
        this.type = type;
    }
}
