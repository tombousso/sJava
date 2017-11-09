package sjava.compiler;

import gnu.bytecode.Type;
import sjava.compiler.ClassInfo;
import sjava.compiler.FileScope;
import sjava.compiler.tokens.Token;

public class MacroInfo extends ClassInfo {
    MacroInfo(FileScope fs, String name) {
        super(fs, name);
    }

    public Type getType(Token tok) {
        return super.getType(tok, false);
    }
}
