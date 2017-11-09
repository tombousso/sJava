package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.AnnotationEntry;
import gnu.bytecode.ArrayClassLoader;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.ParameterizedType;
import gnu.bytecode.RuntimeAnnotationsAttr;
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
import sjava.compiler.MacroInfo;
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
import sjava.std.Tuple2;

public class ClassInfo {
    public FileScope fs;
    public ClassType c;
    BlockToken supers;
    List<LexedParsedToken> toks;
    public List<AMethodInfo> methods;
    public List<ClassInfo> anonClasses;
    Class rc;
    HashMap<String, TypeVariable> tvs;
    byte[] classfile;

    public ClassInfo(FileScope fs, ClassType c) {
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
                TypeVariable[] array = args;

                for(int notused = 0; notused != array.length; ++notused) {
                    TypeVariable tv = array[notused];
                    if(tv instanceof TypeVariable) {
                        this.tvs.put(tv.getName(), tv);
                    }
                }
            }
        }

    }

    public ClassInfo(FileScope fs, String name) {
        this(fs, new ClassType(name));
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
            ArrayClassLoader cl = Main.getClassLoader();
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

    Type getType(String name, boolean allowNew) {
        boolean abs = name.contains(".");
        Object var10000;
        if(this.tvs != null && this.tvs.containsKey(name)) {
            var10000 = (TypeVariable)this.tvs.get(name);
        } else if(Main.constTypes.containsKey(name)) {
            var10000 = (Type)Main.constTypes.get(name);
        } else if(allowNew && this.fs.cs.locals.containsKey(name)) {
            var10000 = (ClassType)this.fs.cs.locals.get(name);
        } else if(allowNew && !abs && this.fs.cs.locals.containsKey(this.fs.package_.concat(name))) {
            var10000 = (ClassType)this.fs.cs.locals.get(this.fs.package_.concat(name));
        } else if(this.fs.imports.containsKey(name)) {
            String fullName = (String)this.fs.imports.get(name);
            if(allowNew && this.fs.cs.locals.containsKey(fullName)) {
                var10000 = (ClassType)this.fs.cs.locals.get(fullName);
            } else {
                if(!this.fs.cs.classExists(fullName)) {
                    throw new RuntimeException();
                }

                var10000 = Type.getType(fullName);
            }
        } else {
            ArrayList matches = new ArrayList();
            ArrayList iterable = this.fs.starImports;
            Iterator it = iterable.iterator();

            for(int notused = 0; it.hasNext(); ++notused) {
                String starImport = (String)it.next();
                String fullName1 = starImport.concat(name);
                if(allowNew && this.fs.cs.locals.containsKey(fullName1)) {
                    matches.add((ClassType)this.fs.cs.locals.get(fullName1));
                } else if(this.fs.cs.classExists(fullName1)) {
                    matches.add(Type.getType(fullName1));
                }
            }

            if(matches.size() == 1) {
                var10000 = (Type)matches.get(0);
            } else {
                if(matches.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Too many types match: ");
                    sb.append(matches);
                    throw new RuntimeException(sb.toString());
                }

                var10000 = this.fs.cs.classExists(name)?Type.getType(name):(Type)null;
            }
        }

        return (Type)var10000;
    }

    public Type getType(Token tok, boolean allowNew) {
        Object var10000;
        if(tok instanceof GenericToken) {
            GenericToken tok1 = (GenericToken)tok;
            ClassType c = (ClassType)this.getType(((VToken)tok1.tok).val, allowNew).getRawType();
            List collection = tok1.toks;
            Type[] out = new Type[collection.size()];
            Iterator it = collection.iterator();

            for(int i = 0; it.hasNext(); ++i) {
                LexedParsedToken param = (LexedParsedToken)it.next();
                out[i] = this.getType((Token)param, allowNew);
            }

            var10000 = new ParameterizedType(c, out);
        } else if(tok instanceof ArrayToken) {
            ArrayToken tok2 = (ArrayToken)tok;
            var10000 = new ArrayType(this.getType((Token)((LexedParsedToken)tok2.toks.get(0)), allowNew));
        } else if(tok instanceof VToken) {
            VToken tok3 = (VToken)tok;
            var10000 = this.getType(tok3.val, allowNew);
        } else {
            var10000 = (Type)null;
        }

        return (Type)var10000;
    }

    public Type getType(Token tok) {
        return this.getType(tok, true);
    }

    public void compileDef(BlockToken tok) {
        if(tok instanceof BlockToken) {
            LexedParsedToken first = (LexedParsedToken)tok.toks.get(0);
            if(first instanceof BlockToken) {
                LinkedHashMap scope = new LinkedHashMap();
                Tuple2 tup = Main.extractModifiers(tok.toks, 2);
                Integer mods = (Integer)tup._1;
                Integer i = (Integer)tup._2;
                int n = (mods.intValue() & Access.STATIC) == 0?1:0;
                ClassType[] exceptions = (ClassType[])null;

                ArrayList annotations;
                for(annotations = new ArrayList(); i.intValue() != tok.toks.size() && (LexedParsedToken)tok.toks.get(i.intValue()) instanceof SingleQuoteToken; i = Integer.valueOf(i.intValue() + 1)) {
                    List toks = ((BlockToken)((LexedParsedToken)((SingleQuoteToken)((LexedParsedToken)tok.toks.get(i.intValue()))).toks.get(0))).toks;
                    VToken first1 = (VToken)((LexedParsedToken)toks.get(0));
                    if(!first1.val.equals("throws")) {
                        annotations.add(new AnnotationEntry((ClassType)this.getType(first1)));
                    } else {
                        List collection = toks.subList(1, toks.size());
                        ClassType[] out = new ClassType[collection.size()];
                        Iterator it = collection.iterator();

                        for(int i1 = 0; it.hasNext(); ++i1) {
                            LexedParsedToken tok1 = (LexedParsedToken)it.next();
                            out[i1] = (ClassType)this.getType(tok1);
                        }

                        exceptions = out;
                    }
                }

                List types = Main.getParams(this, (BlockToken)first, scope, 1, n);
                AMethodInfo mi = this.addMethod(((VToken)((LexedParsedToken)((BlockToken)first).toks.get(0))).val, types, this.getType((LexedParsedToken)tok.toks.get(1)), mods.intValue(), tok.toks.subList(i.intValue(), tok.toks.size()), scope);
                if(exceptions != null) {
                    mi.method.setExceptions(exceptions);
                }

                Iterator it1 = annotations.iterator();

                for(int notused = 0; it1.hasNext(); ++notused) {
                    AnnotationEntry annotation = (AnnotationEntry)it1.next();
                    RuntimeAnnotationsAttr.maybeAddAnnotation(mi.method, annotation);
                }
            } else {
                String name = ((VToken)first).val;
                if(!name.endsWith("!")) {
                    Tuple2 tup1 = Main.extractModifiers(tok.toks, 2);
                    Integer mods1 = (Integer)tup1._1;
                    Integer i2 = (Integer)tup1._2;
                    Type t = this.getType((LexedParsedToken)tok.toks.get(1));
                    this.c.addField(name, t, mods1.intValue());
                }
            }
        }

    }

    void compileDefs() {
        ClassType c = this.c;
        List iterable = this.supers.toks;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            LexedParsedToken var5 = (LexedParsedToken)it.next();
            Type related = this.getType(var5);
            if(related.isInterface()) {
                c.addInterface(related);
            } else {
                c.setSuper(related);
            }
        }

        List iterable1 = this.toks;
        Iterator it1 = iterable1.iterator();

        for(int notused1 = 0; it1.hasNext(); ++notused1) {
            LexedParsedToken tok = (LexedParsedToken)it1.next();
            this.compileDef((BlockToken)tok);
        }

    }

    public void compileMethods() {
        List iterable = this.methods;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            AMethodInfo mi = (AMethodInfo)it.next();
            mi.compileMethodBody(new GenHandler(mi));
        }

    }

    void runClassMacros() {
        List iterable = this.toks;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            LexedParsedToken tok = (LexedParsedToken)it.next();
            if(tok instanceof BlockToken && (LexedParsedToken)((BlockToken)tok).toks.get(0) instanceof VToken && ((VToken)((LexedParsedToken)((BlockToken)tok).toks.get(0))).val.endsWith("!")) {
                this.runClassMacro((BlockToken)tok);
            }
        }

    }

    void runClassMacro(BlockToken tok) {
        String name = ((VToken)((LexedParsedToken)tok.toks.get(0))).val;
        name = name.substring(0, name.length() - 1);
        byte o = 1;
        int l = tok.toks.size() - 1;
        Type[] var10000 = new Type[o + l];
        var10000[0] = Main.getCompilerType("ClassInfo");
        Type[] types = var10000;

        for(int j = 0; j != l; ++j) {
            types[o + j] = Main.getCompilerType("tokens.LexedParsedToken");
        }

        Method method = (Method)null;
        Object ci = (ClassInfo)null;

        for(int i = 0; method == null; ++i) {
            ci = (MacroInfo)((List)this.fs.cs.classMacroNames.get(name)).get(i);
            MFilter filter = new MFilter(name, types, ((ClassInfo)ci).c, true);
            filter.searchDeclared();
            method = filter.getMethod();
        }

        ((ClassInfo)ci).compileMethods();
        Type[] params = method.getGenericParameterTypes();
        Class[] out = new Class[params.length];
        Type[] array = params;

        for(int i1 = 0; i1 != array.length; ++i1) {
            Type t = array[i1];
            out[i1] = t.getReflectClass();
        }

        Class[] classes = out;
        ArrayList args = new ArrayList(Arrays.asList(new Object[]{this}));
        ArrayList var10001;
        if((method.getModifiers() & Access.TRANSIENT) != 0) {
            int var = params.length - o;
            ArrayList al = new ArrayList(tok.toks.subList(1, var));
            LexedParsedToken[] out1 = new LexedParsedToken[tok.toks.size() - var];
            tok.toks.subList(var, tok.toks.size()).toArray(out1);
            al.add(out1);
            var10001 = al;
        } else {
            var10001 = (ArrayList)tok.toks.subList(1, tok.toks.size());
        }

        args.addAll(var10001);

        try {
            ((ClassInfo)ci).getClazz().getMethod(name, classes).invoke((Object)null, args.toArray());
        } catch (NoSuchMethodException var25) {
            throw new RuntimeException(var25);
        } catch (IllegalAccessException var26) {
            throw new RuntimeException(var26);
        } catch (InvocationTargetException var27) {
            throw new RuntimeException(var27);
        }
    }

    public AMethodInfo addMethod(String name, List<Type> params, Type ret, int mods, List<LexedParsedToken> toks, LinkedHashMap scope, boolean addThis) {
        if(addThis && (mods & Access.STATIC) == 0) {
            scope.put("this", new Arg(this.c, 0, 0));
        }

        Type[] atypes = new Type[params.size()];
        params.toArray(atypes);
        Method method = this.c.addMethod(name, atypes, ret, mods);
        MethodInfo out = new MethodInfo(this, toks, method, scope);
        this.methods.add(out);
        return out;
    }

    public AMethodInfo addMethod(String name, List<Type> params, Type ret, int mods, List<LexedParsedToken> toks, LinkedHashMap scope) {
        return this.addMethod(name, params, ret, mods, toks, scope, true);
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
