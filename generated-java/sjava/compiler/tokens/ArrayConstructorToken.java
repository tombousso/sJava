package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.tokens.Token;

public class ArrayConstructorToken extends Token {
    public Type type;
    public Token len;
    public List<Token> toks;

    public ArrayConstructorToken(int line, Type type, Token len, List<Token> toks) {
        super(line);
        this.type = type;
        this.len = len;
        this.toks = toks;
    }
}
