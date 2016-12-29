package sjava.compiler.tokens;

import gnu.bytecode.Type;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class DefineToken extends Token {
    public VToken name;
    public Type type;
    public Token tok;

    public DefineToken(int line, VToken name, Type type, Token tok) {
        super(line);
        this.name = name;
        this.type = type;
        this.tok = tok;
    }
}
