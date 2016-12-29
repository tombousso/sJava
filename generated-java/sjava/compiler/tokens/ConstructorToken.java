package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.tokens.Token;

public class ConstructorToken extends Token {
    public Type type;
    public List<Token> toks;

    public ConstructorToken(int line, Type type, List<Token> toks) {
        super(line);
        this.type = type;
        this.toks = toks;
    }
}
