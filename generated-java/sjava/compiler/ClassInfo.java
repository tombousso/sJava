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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import sjava.compiler.mfilters.MFilter;
import sjava.compiler.tokens.ArrayToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class ClassInfo {
    public ClassType c;
    public FileScope fs;
    List<LexedParsedToken> toks;
    public List<AMethodInfo> methods;
    public List<ClassInfo> anonClasses;
    Class rc;
    HashMap<String, TypeVariable> tvs;
    byte[] classfile;

    public ClassInfo(ClassType c, FileScope fs) {
        this.fs = fs;
        this.c = c;
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
        } catch (ClassNotFoundException var4) {
            throw new RuntimeException(var4);
        }
    }

    public Class getClazz() {
        if(this.rc == null) {
            ArrayClassLoader cl = new ArrayClassLoader();
            this.addToClassLoader(cl);
            this.rc = this.getClazz(cl);
        }

        return this.rc;
    }

    public void writeFiles(String dir) {
        StringBuilder sb = new StringBuilder();
        sb.append(dir);
        sb.append("/");
        sb.append(this.fs.package_.replace(".", "/"));
        String pre = sb.toString();
        (new File(pre)).mkdirs();

        try {
            StringBuilder sb1 = new StringBuilder();
            sb1.append(pre);
            sb1.append(this.c.getSimpleName());
            sb1.append(".class");
            FileUtils.writeByteArrayToFile(new File(sb1.toString()), this.getClassfile());
        } catch (IOException var10) {
            throw new RuntimeException(var10);
        }

        List iterable = this.anonClasses;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ClassInfo anon = (ClassInfo)it.next();
            anon.writeFiles(dir);
        }

    }

    Type getType(String name) {
        boolean abs = name.contains(".");
        Object var10000;
        if(this.tvs != null && this.tvs.containsKey(name)) {
            var10000 = (TypeVariable)this.tvs.get(name);
        } else if(Main.constTypes.containsKey(name)) {
            var10000 = (Type)Main.constTypes.get(name);
        } else if(this.fs.locals.containsKey(name)) {
            var10000 = (ClassType)this.fs.locals.get(name);
        } else if(!abs && this.fs.locals.containsKey(this.fs.package_.concat(name))) {
            var10000 = (ClassType)this.fs.locals.get(this.fs.package_.concat(name));
        } else if(this.fs.imports.containsKey(name)) {
            String fullName = (String)this.fs.imports.get(name);
            if(this.fs.locals.containsKey(fullName)) {
                var10000 = (ClassType)this.fs.locals.get(fullName);
            } else {
                if(!this.fs.classExists(fullName)) {
                    throw new RuntimeException();
                }

                var10000 = Type.getType(fullName);
            }
        } else {
            Object type = (Type)null;

            for(int i = 0; !abs && type == null && i != this.fs.starImports.size(); ++i) {
                String fullName1 = ((String)this.fs.starImports.get(i)).concat(name);
                if(this.fs.locals.containsKey(fullName1)) {
                    type = (ClassType)this.fs.locals.get(fullName1);
                } else if(this.fs.classExists(fullName1)) {
                    type = Type.getType(fullName1);
                }
            }

            var10000 = type == null && this.fs.classExists(name)?Type.getType(name):type;
        }

        return (Type)var10000;
    }

    Type getType(Token tok) {
        Object var10000;
        if(tok instanceof GenericToken) {
            GenericToken tok1 = (GenericToken)tok;
            ClassType c = (ClassType)this.getType(((VToken)((LexedParsedToken)tok1.toks.get(0))).val).getRawType();
            List params = tok1.toks.subList(1, tok1.toks.size());
            Type[] tparams = new Type[params.size()];

            for(int i = 0; i != params.size(); ++i) {
                tparams[i] = this.getType((Token)((LexedParsedToken)params.get(i)));
            }

            var10000 = new ParameterizedType(c, tparams);
        } else if(tok instanceof ArrayToken) {
            ArrayToken tok2 = (ArrayToken)tok;
            var10000 = new ArrayType(this.getType((Token)((LexedParsedToken)tok2.toks.get(0))));
        } else if(tok instanceof VToken) {
            VToken tok3 = (VToken)tok;
            var10000 = this.getType(tok3.val);
        } else {
            var10000 = (Type)null;
        }

        return (Type)var10000;
    }

    public void compileDef(LexedParsedToken tok) {
        if(tok instanceof BlockToken) {
            LexedParsedToken first = (LexedParsedToken)tok.toks.get(0);
            if(first instanceof BlockToken) {
                LinkedHashMap scope = new LinkedHashMap();
                int mods = 0;
                boolean end = false;
                int i = 2;

                while(!end && i != tok.toks.size()) {
                    LexedParsedToken mod = (LexedParsedToken)tok.toks.get(i);
                    if(mod instanceof SingleQuoteToken) {
                        mods |= ((Short)Main.accessModifiers.get(((VToken)((LexedParsedToken)mod.toks.get(0))).val)).shortValue();
                        ++i;
                    } else {
                        end = true;
                    }
                }

                int n = (mods & Access.STATIC) == 0?1:0;
                List types = Main.getParams(this, first, scope, 1, n);
                this.addMethod(((VToken)((LexedParsedToken)first.toks.get(0))).val, types, this.getType((Token)((LexedParsedToken)tok.toks.get(1))), mods, tok.toks.subList(i, tok.toks.size()), scope);
            } else {
                String name = ((VToken)first).val;
                if(!name.endsWith("!")) {
                    int mods1 = 0;
                    boolean end1 = false;

                    for(int i1 = 2; i1 != tok.toks.size() && !end1; ++i1) {
                        LexedParsedToken mod1 = (LexedParsedToken)tok.toks.get(i1);
                        if(mod1 instanceof SingleQuoteToken) {
                            Short nmod = (Short)Main.accessModifiers.get(((VToken)((LexedParsedToken)mod1.toks.get(0))).val);
                            mods1 |= nmod.shortValue();
                        } else {
                            end1 = true;
                        }
                    }

                    Type t = this.getType((Token)((LexedParsedToken)tok.toks.get(1)));
                    this.c.addField(name, t, mods1);
                }
            }
        }

    }

    void compileDefs() {
        ClassType c = this.c;
        List supers = ((LexedParsedToken)this.toks.get(2)).toks;
        Iterator it = supers.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            LexedParsedToken var6 = (LexedParsedToken)it.next();
            Type related = this.getType((Token)var6);
            if(related.isInterface()) {
                c.addInterface(related);
            } else {
                c.setSuper(related);
            }
        }

        for(int i = 3; i != this.toks.size(); ++i) {
            this.compileDef((LexedParsedToken)this.toks.get(i));
        }

    }

    public void compileMethods(GenHandler h) {
        for(int i = 0; i != this.methods.size(); ++i) {
            AMethodInfo mi = (AMethodInfo)this.methods.get(i);
            mi.compileMethodBody(h);
        }

    }

    void runMethodMacros() {
        for(int i = 3; i != this.toks.size(); ++i) {
            LexedParsedToken tok = (LexedParsedToken)this.toks.get(i);
            if(tok instanceof BlockToken && (LexedParsedToken)tok.toks.get(0) instanceof VToken && ((VToken)((LexedParsedToken)tok.toks.get(0))).val.endsWith("!")) {
                this.runMethodMacro((BlockToken)tok);
            }
        }

    }

    void runMethodMacro(BlockToken tok) {
        String name = ((VToken)((LexedParsedToken)tok.toks.get(0))).val;
        name = name.substring(0, name.length() - 1);
        byte o = 1;
        int l = tok.toks.size() - 1;
        Type[] var10000 = new Type[o + l];
        var10000[0] = Main.getCompilerType("ClassInfo");
        Type[] types = var10000;

        int j;
        for(j = 0; j != l; ++j) {
            types[o + j] = Main.getCompilerType("tokens.LexedParsedToken");
        }

        Method method = (Method)null;
        ClassInfo ci = (ClassInfo)null;

        for(int i = 0; method == null; ++i) {
            ci = (ClassInfo)((List)this.fs.methodMacroNames.get(name)).get(i);
            MFilter filter = new MFilter(name, types, ci.c);
            filter.searchDeclared();
            method = filter.getMethod();
        }

        ci.compileMethods(GenHandler.inst);
        Type[] params = method.getGenericParameterTypes();
        Class[] classes = new Class[params.length];

        for(j = 0; j != params.length; ++j) {
            classes[j] = params[j].getReflectClass();
        }

        ArrayList args = new ArrayList(Arrays.asList(new Object[]{this}));
        Object var10001;
        if(params.length > 0 && params[params.length - 1] instanceof ArrayType) {
            int var = params.length - o;
            ArrayList al = new ArrayList(tok.toks.subList(1, var));
            LexedParsedToken[] out = new LexedParsedToken[tok.toks.size() - var];
            tok.toks.subList(var, tok.toks.size()).toArray(out);
            al.add(out);
            var10001 = al;
        } else {
            var10001 = tok.toks.subList(1, tok.toks.size());
        }

        args.addAll((Collection)var10001);

        try {
            ci.getClazz().getMethod(name, classes).invoke((Object)null, args.toArray());
        } catch (NoSuchMethodException var20) {
            throw new RuntimeException(var20);
        } catch (IllegalAccessException var21) {
            throw new RuntimeException(var21);
        } catch (InvocationTargetException var22) {
            throw new RuntimeException(var22);
        }
    }

    public AMethodInfo addMethod(String name, List<Type> params, Type ret, int mods, List<LexedParsedToken> toks, LinkedHashMap scope) {
        Type[] atypes = new Type[params.size()];
        params.toArray(atypes);
        Method method = this.c.addMethod(name, atypes, ret, mods);
        MethodInfo out = new MethodInfo(this, toks, method, scope);
        this.methods.add(out);
        return out;
    }

    public AMethodInfo addMethod(String name, Type ret, int mods, List<LexedParsedToken> toks, LinkedHashMap<String, Arg> scope) {
        ArrayList params = new ArrayList();
        Collection iterable = scope.values();
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Arg arg = (Arg)it.next();
            params.add(arg.type);
        }

        return this.addMethod(name, params, ret, mods, toks, scope);
    }
}
