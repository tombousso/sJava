package sjava.compiler;

import java.util.HashMap;
import sjava.compiler.MyClassLoader;

public class CompileScope {
    MyClassLoader mcl = new MyClassLoader();
    HashMap<String, Boolean> found = new HashMap();
    int macroIndex = 0;

    boolean classExists(String name) {
        boolean var10000;
        if(this.found.containsKey(name)) {
            var10000 = ((Boolean)this.found.get(name)).booleanValue();
        } else {
            boolean b;
            try {
                Class.forName(name);
                b = true;
            } catch (Throwable var4) {
                b = false;
            }

            this.found.put(name, Boolean.valueOf(b));
            var10000 = b;
        }

        return var10000;
    }
}
