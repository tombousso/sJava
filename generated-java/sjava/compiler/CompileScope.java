package sjava.compiler;

import gnu.bytecode.ArrayClassLoader;
import gnu.bytecode.ClassType;
import java.util.HashMap;
import java.util.List;
import sjava.compiler.MacroInfo;
import sjava.compiler.Main;

public class CompileScope {
    HashMap<String, ClassType> locals = new HashMap();
    HashMap<String, List<MacroInfo>> macroNames = new HashMap();
    HashMap<String, List<MacroInfo>> methodMacroNames = new HashMap();
    ArrayClassLoader mcl = Main.getClassLoader();
    HashMap<String, Boolean> found = new HashMap();
    int macroIndex = 0;

    boolean classExists(String name) {
        boolean var10000;
        if(this.found.containsKey(name)) {
            var10000 = ((Boolean)this.found.get(name)).booleanValue();
        } else {
            boolean b;
            try {
                Main.class.getClassLoader().loadClass(name);
                b = true;
            } catch (ClassNotFoundException var5) {
                b = false;
            } catch (NoClassDefFoundError var6) {
                b = false;
            }

            this.found.put(name, Boolean.valueOf(b));
            var10000 = b;
        }

        return var10000;
    }
}
