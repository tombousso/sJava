package sjava.compiler.tokens;

import gnu.bytecode.Type;
import sjava.compiler.tokens.Token;

public class AsToken extends Token {
    public Type type;
    public Token tok;

    public AsToken(int line, Type type, Token tok) {
        super(line);
        this.type = type;
        this.tok = tok;
    }
}
