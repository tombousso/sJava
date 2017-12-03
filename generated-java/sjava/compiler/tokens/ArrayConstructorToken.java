package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.tokens.Token;

public class ArrayConstructorToken extends Token {
    public Type type;
    public List<Token> lens;
    public List<Token> toks;

    public ArrayConstructorToken(int line, Type type, List<Token> lens, List<Token> toks) {
        super(line);
        this.type = type;
        this.lens = lens;
        this.toks = toks;
    }
}
