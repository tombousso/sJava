package sjava.compiler;

import gnu.bytecode.Type;
import sjava.compiler.ClassInfo;
import sjava.compiler.FileScope;

public class MacroInfo extends ClassInfo {
    MacroInfo(String name, FileScope fs) {
        super(name, fs);
    }

    Type getType(String name) {
        return name.startsWith("sjava.compiler.")?Type.getType(name):super.getType(name);
    }
}
