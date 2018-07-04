package sjava.compiler.tokens;

import gnu.bytecode.Type;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class ConstructorToken extends Token {
    public Type type;
    public ImList<Token> toks;

    public ConstructorToken(int line, Type type, ImList<Token> toks) {
        super(line);
        this.type = type;
        this.toks = toks;
    }
}
