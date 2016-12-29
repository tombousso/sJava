package sjava.compiler.tokens;

import gnu.bytecode.Type;
import sjava.compiler.tokens.Token;

public class ClassToken extends Token {
    public Type type;

    public ClassToken(int line, Type type) {
        super(line);
        this.type = type;
    }
}
