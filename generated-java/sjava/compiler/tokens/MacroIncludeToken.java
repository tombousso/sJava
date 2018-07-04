package sjava.compiler.tokens;

import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class MacroIncludeToken extends Token {
    public Token ret;
    public String name;
    public ImList<LexedParsedToken> toks;

    public MacroIncludeToken(int line, String name, ImList<LexedParsedToken> toks) {
        super(line);
        this.name = name;
        this.toks = toks;
    }
}
