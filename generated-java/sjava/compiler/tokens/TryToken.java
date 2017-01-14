package sjava.compiler.tokens;

import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;
import sjava.std.Tuple3;

public class TryToken extends Token {
    public Token tok;
    public List<Tuple3<VToken, Type, List<Token>>> catches;
    public List<Token> finallyToks;

    public TryToken(int line, Token tok, List catches, List<Token> finallyToks) {
        super(line);
        this.tok = tok;
        this.catches = catches;
        this.finallyToks = finallyToks;
    }
}
