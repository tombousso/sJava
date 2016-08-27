package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayClassLoader;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.ParameterizedType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Arg;
import sjava.compiler.FileScope;
import sjava.compiler.Main;
import sjava.compiler.MethodInfo;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class ClassInfo {
    public ClassType c;
    public FileScope fs;
    List<Token> toks;
    public List<AMethodInfo> methods;
    public List<ClassInfo> anonClasses;
    public Class rc;
    HashMap<String, TypeVariable> tvs;
    byte[] classfile;

    public ClassInfo(ClassType c, FileScope fs) {
        this.fs = fs;
        this.c = c;
        this.toks = new ArrayList();
        this.methods = new ArrayList();
        this.anonClasses = new ArrayList();
        if(c != null) {
            this.c.setClassfileVersion(ClassType.JDK_1_8_VERSION);
            this.c.setSuper(Type.javalangObjectType);
            TypeVariable[] args = c.getTypeParameters();
            if(args != null) {
                this.tvs = new HashMap();

                for(int i = 0; i != args.length; ++i) {
                    TypeVariable tv = args[i];
                    if(tv instanceof TypeVariable) {
                        this.tvs.put(tv.getName(), (TypeVariable)tv);
                    }
                }
            }
        }

    }

    public ClassInfo(String name, FileScope fs) {
        this(new ClassType(name), fs);
    }

    byte[] getClassfile() {
        if(this.classfile == null) {
            this.classfile = this.c.writeToArray();
        }

        return this.classfile;
    }

    public void addToClassLoader(ArrayClassLoader cl) {
        cl.addClass(this.c.getName(), this.getClassfile());
        List iterable = this.anonClasses;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ClassInfo anon = (ClassInfo)it.next();
            anon.addToClassLoader(cl);
        }

    }

    public Class getClazz(ArrayClassLoader cl) {
        Class c = (Class)null;

        try {
            c = cl.loadClass(this.c.getName());
            return c;
        } catch (Throwable var4) {
            throw new RuntimeException(var4);
        }
    }

    public Class getClazz() {
        ArrayClassLoader cl = new ArrayClassLoader();
        this.addToClassLoader(cl);
        return this.getClazz(cl);
    }

    public void writeFiles(String dir) {
        String pre = dir.concat(this.fs.package_.replace(".", "/"));
        (new File(pre)).mkdirs();

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(pre);
            sb.append(this.c.getSimpleName());
            sb.append(".class");
            FileUtils.writeByteArrayToFile(new File(sb.toString()), this.getClassfile());
        } catch (Throwable var9) {
            throw new RuntimeException(var9);
        }

        List iterable = this.anonClasses;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ClassInfo anon = (ClassInfo)it.next();
            anon.writeFiles(dir);
        }

    }

    Type getType(String name) {
        int i = 0;

        int dims;
        for(dims = -1; i != -1; ++dims) {
            i = name.indexOf("[]", i + 1);
        }

        name = name.replace("[", "");
        name = name.replace("]", "");
        boolean abs = name.contains(".");
        Object var10000;
        if(this.tvs != null && this.tvs.containsKey(name)) {
            var10000 = (TypeVariable)this.tvs.get(name);
        } else if(Main.constTypes.containsKey(name)) {
            var10000 = (Type)Main.constTypes.get(name);
        } else if(this.fs.locals.containsKey(name)) {
            var10000 = (Type)this.fs.locals.get(name);
        } else if(!abs && this.fs.locals.containsKey(this.fs.package_.concat(name))) {
            var10000 = (Type)this.fs.locals.get(this.fs.package_.concat(name));
        } else if(this.fs.imports.containsKey(name)) {
            String fullName = (String)this.fs.imports.get(name);
            if(this.fs.locals.containsKey(fullName)) {
                var10000 = (Type)this.fs.locals.get(fullName);
            } else {
                if(!this.fs.classExists(fullName)) {
                    throw new RuntimeException();
                }

                var10000 = Type.getType(fullName);
            }
        } else {
            Type type = (Type)null;

            for(int i1 = 0; !abs && type == null && i1 != this.fs.starImports.size(); ++i1) {
                String fullName1 = ((String)this.fs.starImports.get(i1)).concat(name);
                if(this.fs.locals.containsKey(fullName1)) {
                    type = (Type)this.fs.locals.get(fullName1);
                } else if(this.fs.classExists(fullName1)) {
                    type = Type.getType(fullName1);
                }
            }

            var10000 = type == null && this.fs.classExists(name)?Type.getType(name):type;
        }

        Object out = var10000;

        for(i = 0; i != dims; ++i) {
            out = new ArrayType((Type)out);
        }

        return (Type)out;
    }

    Type getType(Token tok) {
        Object var10000;
        if(tok instanceof GenericToken) {
            GenericToken tok1 = (GenericToken)tok;
            ClassType c = (ClassType)this.getType(((VToken)tok1.tok).val).getRawType();
            List params = tok1.toks;
            Type[] tparams = new Type[params.size()];

            for(int i = 0; i != params.size(); ++i) {
                tparams[i] = this.getType((Token)params.get(i));
            }

            var10000 = new ParameterizedType(c, tparams);
        } else {
            var10000 = tok instanceof VToken?this.getType(((VToken)tok).val):(Type)null;
        }

        return (Type)var10000;
    }

    public void compileDef(Token tok) {
        if(tok instanceof BlockToken) {
            Token first = (Token)tok.toks.get(0);
            if(first instanceof BlockToken) {
                LinkedHashMap scope = new LinkedHashMap();
                int mods = 0;
                boolean end = false;
                int i = 2;

                while(!end && i != tok.toks.size()) {
                    Token mod = (Token)tok.toks.get(i);
                    if(mod instanceof SingleQuoteToken) {
                        mods |= ((Short)Main.accessModifiers.get(((VToken)((Token)mod.toks.get(0))).val)).shortValue();
                        ++i;
                    } else {
                        end = true;
                    }
                }

                byte n = 0;
                if((mods & Access.STATIC) == 0) {
                    scope.put("this", new Arg(0, this.c));
                    n = 1;
                }

                Type[] types = Main.getParams(this, first, scope, 1, n);
                Method m = this.c.addMethod(((VToken)((Token)first.toks.get(0))).val, types, this.getType((Token)tok.toks.get(1)), mods);
                this.methods.add(new MethodInfo(this, tok.toks.subList(i, tok.toks.size()), m, scope));
            } else {
                int mods1 = 0;
                boolean end1 = false;

                for(int i1 = 2; i1 != tok.toks.size() && !end1; ++i1) {
                    Token mod1 = (Token)tok.toks.get(i1);
                    if(mod1 instanceof SingleQuoteToken) {
                        Short nmod = (Short)Main.accessModifiers.get(((VToken)((Token)mod1.toks.get(0))).val);
                        mods1 |= nmod.shortValue();
                    } else {
                        end1 = true;
                    }
                }

                Type t = this.getType((Token)tok.toks.get(1));
                this.c.addField(((VToken)first).val, t, mods1);
            }
        }

    }

    void compileDefs() {
        ClassType c = this.c;
        List supers = ((Token)this.toks.get(2)).toks;

        int i;
        for(i = 0; i != supers.size(); ++i) {
            Type related = this.getType((Token)supers.get(i));
            if(related.isInterface()) {
                c.addInterface(related);
            } else {
                c.setSuper(related);
            }
        }

        for(i = 3; i != this.toks.size(); ++i) {
            this.compileDef((Token)this.toks.get(i));
        }

    }

    public void compileMethods(GenHandler h) {
        for(int i = 0; i != this.methods.size(); ++i) {
            AMethodInfo mi = (AMethodInfo)this.methods.get(i);
            mi.compileMethodBody(h);
        }

    }
}
