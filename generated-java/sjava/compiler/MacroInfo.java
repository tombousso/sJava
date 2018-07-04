package sjava.compiler;

import gnu.bytecode.Type;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.ClassInfo;
import sjava.compiler.ClassMacroMethodInfo;
import sjava.compiler.FileScope;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;

public class MacroInfo extends ClassInfo {
    MacroInfo(FileScope fs, String name) {
        super(fs, name);
    }

    public Type getType(Token tok) {
        return super.getType(tok, false);
    }

    public AMethodInfo addClassMacroMethod(String name, List<Type> params, Type ret, int mods, ImList<LexedParsedToken> toks, LinkedHashMap scope) {
        ClassMacroMethodInfo out = new ClassMacroMethodInfo(this, toks, scope, name, params, ret, mods);
        super.methods.add(out);
        return out;
    }
}
