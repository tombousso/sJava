package sjava.compiler;

import gnu.bytecode.Type;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.tokens.ImList;
import sjava.compiler.tokens.LexedParsedToken;

public class ClassMacroMethodInfo extends AMethodInfo {
    ClassMacroMethodInfo(ClassInfo ci, ImList<LexedParsedToken> toks, LinkedHashMap<String, Arg> firstScope, String name, List<Type> params, Type ret, int mods) {
        super(ci, toks, firstScope, name, params, ret, mods);
    }
}
