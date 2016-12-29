package sjava.compiler.tokens;

import java.util.List;
import sjava.compiler.tokens.Token;

public class NumOpToken extends Token {
    public String op;
    public List<Token> toks;

    public NumOpToken(int line, String op, List<Token> toks) {
        super(line);
        this.op = op;
        this.toks = toks;
    }
}
