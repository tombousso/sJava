package sjava.compiler.tokens;

import gnu.bytecode.Type;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;

public class ArrayConstructorToken extends Token {
    public Type type;
    public ImList<Token> lens;
    public ImList<Token> toks;

    public ArrayConstructorToken(int line, Type type, ImList<Token> lens, ImList<Token> toks) {
        super(line);
        this.type = type;
        this.lens = lens;
        this.toks = toks;
    }
}
