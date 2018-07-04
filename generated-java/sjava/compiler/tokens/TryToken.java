package sjava.compiler.tokens;

import gnu.bytecode.Type;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;
import sjava.std.Tuple3;

public class TryToken extends Token {
    public Token tok;
    public ImList<Tuple3<VToken, Type, ImList<Token>>> catches;
    public ImList<Token> finallyToks;

    public TryToken(int line, Token tok, ImList catches, ImList<Token> finallyToks) {
        super(line);
        this.tok = tok;
        this.catches = catches;
        this.finallyToks = finallyToks;
    }
}
