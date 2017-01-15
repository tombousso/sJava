package sjava.compiler;

import sjava.compiler.Main;

public class MyClassLoader extends ClassLoader {
    public MyClassLoader() {
        super(Main.class.getClassLoader());
    }

    public Class addClass(String s, byte[] bytes) {
        return this.defineClass(s, bytes, 0, bytes.length);
    }
}
