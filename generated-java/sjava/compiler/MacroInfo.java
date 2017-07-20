package sjava.compiler;

import gnu.bytecode.Type;
import sjava.compiler.ClassInfo;
import sjava.compiler.FileScope;
import sjava.compiler.tokens.Token;

public class MacroInfo extends ClassInfo {
    MacroInfo(String name, FileScope fs) {
        super(name, fs);
    }

    public Type getType(Token tok) {
        return super.getType(tok, false);
    }
}
