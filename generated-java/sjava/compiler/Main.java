package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayClassLoader;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Method;
import gnu.bytecode.ParameterizedType;
import gnu.bytecode.PrimType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.CompileScope;
import sjava.compiler.FileScope;
import sjava.compiler.Formatter;
import sjava.compiler.Lexer;
import sjava.compiler.Parser;
import sjava.compiler.commands.BuildCommand;
import sjava.compiler.commands.Command;
import sjava.compiler.commands.FormatCommand;
import sjava.compiler.commands.RunCommand;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.mfilters.MFilter;
import sjava.compiler.mfilters.MethodCall;
import sjava.compiler.tokens.ArrayToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.VToken;
import sjava.std.Tuple2;

public class Main {
    public static Type unknownType = Type.getType("unknownType");
    public static Type returnType = Type.getType("returnType");
    public static Type throwType = Type.getType("throwType");
    public static HashMap<Type, Method> unboxMethods = new HashMap();
    static HashMap<String, Type> constTypes;
    static HashMap<String, Short> accessModifiers;
    public static HashMap<String, Integer> binOps;
    public static HashMap<String, Integer> compare2Ops;
    public static HashMap<String, Integer> compare1Ops;
    static HashMap<String, String> oppositeOps;
    static String[][] precs;
    public static HashMap<String, Character> specialChars;
    static int MP;
    static int ML;
    static HashMap<String, Integer> s2prec;
    static Map<String, Command> commands;

    static {
        unboxMethods.put(Type.shortType.boxedType(), Type.javalangNumberType.getDeclaredMethod("shortValue", 0));
        unboxMethods.put(Type.intType.boxedType(), Type.javalangNumberType.getDeclaredMethod("intValue", 0));
        unboxMethods.put(Type.longType.boxedType(), Type.javalangNumberType.getDeclaredMethod("longValue", 0));
        unboxMethods.put(Type.floatType.boxedType(), Type.javalangNumberType.getDeclaredMethod("floatValue", 0));
        unboxMethods.put(Type.doubleType.boxedType(), Type.javalangNumberType.getDeclaredMethod("doubleValue", 0));
        unboxMethods.put(Type.booleanType.boxedType(), Type.javalangBooleanType.getDeclaredMethod("booleanValue", 0));
        unboxMethods.put(Type.charType.boxedType(), Type.javalangCharacterType.getDeclaredMethod("charValue", 0));
        constTypes = new HashMap();
        constTypes.put("bool", Type.booleanType);
        constTypes.put("byte", Type.byteType);
        constTypes.put("char", Type.charType);
        constTypes.put("double", Type.doubleType);
        constTypes.put("float", Type.floatType);
        constTypes.put("int", Type.intType);
        constTypes.put("long", Type.longType);
        constTypes.put("short", Type.shortType);
        constTypes.put("void", Type.voidType);
        accessModifiers = new HashMap();
        accessModifiers.put("static", Short.valueOf(Access.STATIC));
        accessModifiers.put("public", Short.valueOf(Access.PUBLIC));
        accessModifiers.put("abstract", Short.valueOf(Access.ABSTRACT));
        accessModifiers.put("final", Short.valueOf(Access.FINAL));
        accessModifiers.put("private", Short.valueOf(Access.PRIVATE));
        accessModifiers.put("protected", Short.valueOf(Access.PROTECTED));
        accessModifiers.put("synchronized", Short.valueOf(Access.SYNCHRONIZED));
        accessModifiers.put("transient", Short.valueOf(Access.TRANSIENT));
        accessModifiers.put("volatile", Short.valueOf(Access.VOLATILE));
        accessModifiers.put("native", Short.valueOf(Access.NATIVE));
        accessModifiers.put("interface", Short.valueOf(Access.INTERFACE));
        accessModifiers.put("super", Short.valueOf(Access.SUPER));
        binOps = new HashMap();
        binOps.put("+", Integer.valueOf(96));
        binOps.put("-", Integer.valueOf(100));
        binOps.put("*", Integer.valueOf(104));
        binOps.put("/", Integer.valueOf(108));
        binOps.put("%", Integer.valueOf(112));
        binOps.put("&", Integer.valueOf(126));
        binOps.put("|", Integer.valueOf(128));
        binOps.put("^", Integer.valueOf(130));
        compare2Ops = new HashMap();
        compare2Ops.put("!=", Integer.valueOf(153));
        compare2Ops.put("=", Integer.valueOf(154));
        compare2Ops.put(">=", Integer.valueOf(155));
        compare2Ops.put("<", Integer.valueOf(156));
        compare2Ops.put("<=", Integer.valueOf(157));
        compare2Ops.put(">", Integer.valueOf(158));
        compare1Ops = new HashMap();
        compare1Ops.put("!=0", Integer.valueOf(153));
        compare1Ops.put("==0", Integer.valueOf(154));
        compare1Ops.put(">=0", Integer.valueOf(155));
        compare1Ops.put("<0", Integer.valueOf(156));
        compare1Ops.put("<=0", Integer.valueOf(157));
        compare1Ops.put(">0", Integer.valueOf(158));
        compare1Ops.put("!=null", Integer.valueOf(198));
        compare1Ops.put("==null", Integer.valueOf(199));
        oppositeOps = new HashMap();
        oppositeOps.put("!=", "=");
        oppositeOps.put("=", "!=");
        oppositeOps.put(">=", "<");
        oppositeOps.put("<", ">=");
        oppositeOps.put("<=", ">");
        oppositeOps.put(">", "<=");
        oppositeOps.put("!=0", "==0");
        oppositeOps.put("==0", "!=0");
        precs = new String[][]{{"\"\"\"", "\"", ")", "}", "]", ";"}, {"\'", ",", ",$", "`", ":", "(", "{", "["}};
        specialChars = new HashMap();
        specialChars.put("space", Character.valueOf(' '));
        specialChars.put("newline", Character.valueOf('\n'));
        specialChars.put("lparen", Character.valueOf('('));
        specialChars.put("rparen", Character.valueOf(')'));
        MP = precs.length - 1;
        ML = 3;
        s2prec = new HashMap();
        String[][] array = precs;

        for(int i = 0; i != array.length; ++i) {
            String[] a = array[i];
            String[] array1 = a;

            for(int notused = 0; notused != array1.length; ++notused) {
                String b = array1[notused];
                s2prec.put(b, Integer.valueOf(i));
            }
        }

    }

    static void addCommandToMap(Command c) {
        commands.put(c.name(), c);
    }

    public static void main(String[] args) {
        commands = new LinkedHashMap();
        addCommandToMap(new BuildCommand());
        addCommandToMap(new RunCommand());
        addCommandToMap(new FormatCommand());
        if(args.length == 0) {
            printHelp();
        } else {
            String arg = args[0];
            if(commands.containsKey(arg)) {
                Command cmd = (Command)commands.get(arg);
                args = (String[])Arrays.copyOfRange(args, 1, args.length);
                CommandLine commandLine = cmd.parse(args);
                if(commandLine.hasOption("h")) {
                    cmd.printHelp();
                } else {
                    cmd.run(commandLine, commandLine.getArgList());
                }
            } else {
                printHelp();
            }

        }
    }

    static void printHelp() {
        PrintStream var10000 = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("usage: sjava [command] [arguments]");
        var10000.println(sb.toString());
        var10000 = System.out;
        StringBuilder sb1 = new StringBuilder();
        var10000.println(sb1.toString());
        var10000 = System.out;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Commands:");
        var10000.println(sb2.toString());
        var10000 = System.out;
        StringBuilder sb3 = new StringBuilder();
        var10000.println(sb3.toString());
        Set iterable = commands.keySet();
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            String command = (String)it.next();
            var10000 = System.out;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("\t");
            sb4.append(command);
            var10000.println(sb4.toString());
        }

        var10000 = System.out;
        StringBuilder sb5 = new StringBuilder();
        var10000.println(sb5.toString());
    }

    public static List<LexedParsedToken> parse(String code, Lexer lexer, Parser parser) {
        return parser.parseAll(lexer.lex(code));
    }

    public static void compile(Collection<File> files, String dir) {
        List iterable = compile(files);
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            FileScope fs = (FileScope)it.next();
            List iterable1 = fs.newClasses;
            Iterator it1 = iterable1.iterator();

            for(int notused1 = 0; it1.hasNext(); ++notused1) {
                ClassInfo ci = (ClassInfo)it1.next();
                ci.writeFiles(dir);
            }
        }

    }

    public static List<FileScope> compile(Collection<File> files) {
        ArrayList mFiles = new ArrayList();
        String var10000;
        if(System.getProperty("sjava.home") != null) {
            var10000 = System.getProperty("sjava.home");
        } else {
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            var10000 = path.endsWith(".jar")?(new File(path)).getParent():".";
        }

        String pre = var10000;
        StringBuilder sb = new StringBuilder();
        sb.append(pre);
        sb.append("/std/macros.sjava");
        mFiles.add(new File(sb.toString()));
        mFiles.addAll(files);
        LinkedHashMap files1 = new LinkedHashMap();
        Iterator it = mFiles.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            File file = (File)it.next();
            String code = (String)null;

            try {
                code = FileUtils.readFileToString(file);
            } catch (IOException var14) {
                throw new RuntimeException(var14);
            }

            int line = Formatter.checkFormatted(code);
            if(line != -1) {
                PrintStream var15 = System.out;
                StringBuilder sb1 = new StringBuilder();
                sb1.append("Warning: ");
                sb1.append(file);
                sb1.append(" isn\'t formatted (line ");
                sb1.append(line);
                sb1.append(")");
                var15.println(sb1.toString());
            }

            files1.put(file, parse(code, new Lexer(), new Parser()));
        }

        return compile((HashMap)files1);
    }

    public static List<FileScope> compile(HashMap<File, List<LexedParsedToken>> files) {
        CompileScope cs = new CompileScope();
        ArrayList fileScopes = new ArrayList();
        Set iterable = files.entrySet();
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Entry entry = (Entry)it.next();
            List toks = (List)entry.getValue();
            FileScope fs = new FileScope(cs, ((File)entry.getKey()).toString(), toks);
            fileScopes.add(fs);
            fs.compileRoot();
        }

        Iterator it1 = fileScopes.iterator();

        for(int notused1 = 0; it1.hasNext(); ++notused1) {
            FileScope fs1 = (FileScope)it1.next();
            fs1.compileDefs();
        }

        Iterator it2 = fileScopes.iterator();

        for(int notused2 = 0; it2.hasNext(); ++notused2) {
            FileScope fs2 = (FileScope)it2.next();
            fs2.compileMacros();
        }

        Iterator it3 = fileScopes.iterator();

        for(int notused3 = 0; it3.hasNext(); ++notused3) {
            FileScope fs3 = (FileScope)it3.next();
            fs3.runMethodMacros();
        }

        Iterator it4 = fileScopes.iterator();

        for(int notused4 = 0; it4.hasNext(); ++notused4) {
            FileScope fs4 = (FileScope)it4.next();
            List iterable1 = fs4.newClasses;
            Iterator it5 = iterable1.iterator();

            for(int notused5 = 0; it5.hasNext(); ++notused5) {
                ClassInfo ci = (ClassInfo)it5.next();
                ci.compileMethods();
            }
        }

        return fileScopes;
    }

    public static Type resolveType(Map<TypeVariable, Type> map, Type pt, Type t, boolean defaultToRaw) {
        Object var10000;
        if(t instanceof TypeVariable) {
            if(pt instanceof ParameterizedType) {
                TypeVariable[] tvs = ((ParameterizedType)pt).getRawType().getTypeParameters();
                String s = ((TypeVariable)t).getName();
                TypeVariable[] array = tvs;

                for(int i = 0; i != array.length; ++i) {
                    TypeVariable tv = array[i];
                    if(tv.getName().equals(s)) {
                        return ((ParameterizedType)pt).getTypeArgumentType(i);
                    }
                }
            }

            var10000 = map != null && map.containsKey((TypeVariable)t)?(Type)map.get((TypeVariable)t):(defaultToRaw?((TypeVariable)t).getRawType():(Type)null);
        } else if(t instanceof ArrayType) {
            var10000 = new ArrayType(resolveType(map, pt, ((ArrayType)t).elements, defaultToRaw));
        } else if(!(t instanceof ParameterizedType)) {
            var10000 = t;
        } else {
            Type[] types = ((ParameterizedType)t).getTypeArgumentTypes();
            Type[] parameterized = new Type[types.length];
            Type[] array1 = types;
            int i1 = 0;

            while(true) {
                if(i1 == array1.length) {
                    var10000 = new ParameterizedType(((ParameterizedType)t).getRawType(), parameterized);
                    break;
                }

                Type type = array1[i1];
                Type r = resolveType(map, pt, type, false);
                if(r == null || 0 != ((ParameterizedType)t).getTypeArgumentBound(i1)) {
                    return ((ParameterizedType)t).getRawType();
                }

                parameterized[i1] = r;
                ++i1;
            }
        }

        return (Type)var10000;
    }

    public static Type resolveType(Map<TypeVariable, Type> map, Type pt, Type t) {
        return resolveType(map, pt, t, true);
    }

    public static Type resolveType(Type pt, Type t) {
        return resolveType((Map)null, pt, t, true);
    }

    public static Type resolveType(Map<TypeVariable, Type> map, Type t) {
        return resolveType(map, (Type)null, t, true);
    }

    static Type unresolveTv(TypeVariable tv, Type generic, Type real) {
        Type var10000;
        if(generic instanceof TypeVariable) {
            var10000 = tv.equals((TypeVariable)generic)?tryBox(real):(Type)null;
        } else if(generic instanceof ParameterizedType && real instanceof ParameterizedType && ((ParameterizedType)generic).getRawType().equals(((ParameterizedType)real).getRawType())) {
            Type[] gtypes = ((ParameterizedType)generic).getTypeArgumentTypes();
            Type[] rtypes = ((ParameterizedType)real).getTypeArgumentTypes();
            Type[] array = gtypes;

            for(int i = 0; i != array.length; ++i) {
                Type gtype = array[i];
                Type ret = unresolveTv(tv, gtype, rtypes[i]);
                if(ret != null) {
                    return ret;
                }
            }

            var10000 = (Type)null;
        } else {
            var10000 = generic instanceof ArrayType && real instanceof ArrayType?unresolveTv(tv, ((ArrayType)generic).elements, ((ArrayType)real).elements):(Type)null;
        }

        return var10000;
    }

    public static Map<TypeVariable, Type> unresolveTvs(TypeVariable[] tvs, Type[] generics, Type[] reals) {
        Object var10000;
        if(tvs != null && generics.length == reals.length) {
            HashMap out = new HashMap();
            TypeVariable[] array = tvs;

            for(int notused = 0; notused != array.length; ++notused) {
                TypeVariable tv = array[notused];
                Type t = (Type)null;

                for(int j = 0; t == null && j != generics.length; ++j) {
                    t = unresolveTv(tv, generics[j], reals[j]);
                }

                if(t != null) {
                    out.put(tv, t);
                }
            }

            var10000 = out;
        } else {
            var10000 = (Map)null;
        }

        return (Map)var10000;
    }

    public static int arrayDim(Type t) {
        int out;
        for(out = 0; t instanceof ArrayType; ++out) {
            t = ((ArrayType)t).elements;
        }

        return out;
    }

    static boolean compileClassMod(LexedParsedToken tok, ClassType c) {
        boolean var10000;
        if(tok instanceof VToken) {
            Short nmod = (Short)accessModifiers.get(((VToken)tok).val);
            c.addModifiers(nmod.shortValue());
            var10000 = true;
        } else {
            var10000 = false;
        }

        return var10000;
    }

    public static List<Type> getParams(ClassInfo ci, BlockToken tok, LinkedHashMap scope, int i, int o) {
        int n = (tok.toks.size() - i) / 2;
        ArrayList types = new ArrayList(n);

        for(int j = 0; j != n; ++j) {
            VToken arg = (VToken)((LexedParsedToken)tok.toks.get(j * 2 + i));
            Type type = ci.getType((LexedParsedToken)tok.toks.get(j * 2 + i + 1));
            types.add(type);
            scope.put(arg.val, new Arg(type, o + j, arg.macro));
        }

        return types;
    }

    public static Type numericOpType(Type[] types) {
        List l = Arrays.asList(types);
        return !l.contains(Type.doubleType) && !l.contains(Type.doubleType.boxedType())?(!l.contains(Type.floatType) && !l.contains(Type.floatType.boxedType())?(!l.contains(Type.longType) && !l.contains(Type.longType.boxedType())?Type.intType:Type.longType):Type.floatType):Type.doubleType;
    }

    public static boolean isNumeric(Type type) {
        Type unbox = tryUnbox(type);
        return unbox instanceof PrimType && (PrimType)unbox != Type.voidType && (PrimType)unbox != Type.booleanType;
    }

    public static boolean allNumeric(Type[] types) {
        Type[] array = types;

        for(int notused = 0; notused != array.length; ++notused) {
            Type type = array[notused];
            if(!isNumeric(type)) {
                return false;
            }
        }

        return true;
    }

    public static Type tryBox(Type t) {
        return (Type)(t instanceof PrimType?((PrimType)t).boxedType():t);
    }

    public static Type tryUnbox(Type t) {
        PrimType o = PrimType.unboxedType(t);
        return (Type)(o == null?t:o);
    }

    public static boolean isCompare(String s) {
        return s.equals("!") || s.equals("&&") || s.equals("||") || compare2Ops.containsKey(s);
    }

    public static String invertComp(boolean inv, String comp) {
        return inv?(String)oppositeOps.get(comp):comp;
    }

    public static Tuple2<Type, MethodCall> emitInvoke(GenHandler h, String name, Type type, List<Emitter> emitters, Type needed, boolean special, boolean static_) {
        Type[] types = Emitter.emitAll(emitters, h, (CodeAttr)null, unknownType);
        MFilter filter = new MFilter(name, types, type, static_);
        if(special) {
            filter.searchDeclared();
        } else {
            filter.searchAll();
        }

        MethodCall mc = filter.getMethodCall();
        return new Tuple2(emitInvoke(h, mc, emitters, needed, special), mc);
    }

    public static Tuple2<Type, MethodCall> emitInvoke(GenHandler h, String name, Type type, List<Emitter> emitters, Type needed, boolean static_) {
        return emitInvoke(h, name, type, emitters, needed, false, static_);
    }

    public static Type emitInvoke(GenHandler h, MethodCall mc, List<Emitter> emitters, Type needed, boolean special) {
        CodeAttr code = h.code;
        boolean output = code != null;
        Method method = mc.m;
        TypeVariable[] typeParameters = method.getTypeParameters();
        Type[] params = method.getGenericParameterTypes();
        boolean varargs = (method.getModifiers() & Access.TRANSIENT) != 0;
        int j = 0;

        int n;
        for(n = !varargs || mc.types.length >= params.length && arrayDim(params[params.length - 1]) == arrayDim(mc.types[params.length - 1])?0:1; j != params.length - n; ++j) {
            ((Emitter)emitters.get(j)).emit(h, code, resolveType(mc.tvs, mc.t, params[j]));
        }

        if(n == 1) {
            ArrayType at = (ArrayType)params[params.length - 1];
            Type et = resolveType(mc.tvs, mc.t, at.elements);
            if(output) {
                code.emitPushInt(1 + (mc.types.length - params.length));
            }

            if(output) {
                code.emitNewArray(at.elements.getRawType());
            }

            for(int oj = j; j != mc.types.length; ++j) {
                if(output) {
                    code.emitDup();
                }

                if(output) {
                    code.emitPushInt(j - oj);
                }

                ((Emitter)emitters.get(j)).emit(h, code, et);
                if(output) {
                    code.emitArrayStore();
                }
            }
        }

        if(special) {
            if(output) {
                code.emitInvokeSpecial(method);
            }
        } else if(output) {
            code.emitInvoke(method);
        }

        Type out = resolveType(mc.tvs, mc.t, method.getReturnType());
        if(out != Type.voidType && needed != Type.voidType && output) {
            code.emitCheckcast(out.getRawType());
        }

        return h.castMaybe(out, needed);
    }

    public static Type emitInvoke(GenHandler h, MethodCall mc, List<Emitter> emitters, Type needed) {
        return emitInvoke(h, mc, emitters, needed, false);
    }

    public static Type compareType(Type[] types) {
        return (Type)(allNumeric(types)?numericOpType(types):(Collections.frequency(Arrays.asList(types), Type.booleanType) + Collections.frequency(Arrays.asList(types), ClassType.make("java.lang.Boolean")) == types.length?Type.booleanType:Type.objectType));
    }

    public static int compare(Type a, Type b) {
        int var10000;
        if(!(a instanceof ClassType) && !(a instanceof ParameterizedType) || !(b instanceof ClassType) && !(b instanceof ParameterizedType)) {
            var10000 = a.compare(b);
        } else {
            if(a instanceof ClassType && b instanceof ParameterizedType && ((ClassType)a).getTypeParameters() != null && ((ClassType)a).getTypeParameters().length != 0) {
                b = b.getRawType();
            }

            if(a instanceof ParameterizedType && b instanceof ClassType && ((ClassType)b).getTypeParameters() != null && ((ClassType)b).getTypeParameters().length != 0) {
                a = a.getRawType();
            }

            if(Type.isSame(a, b)) {
                var10000 = 0;
            } else {
                LinkedHashSet iterable = superTypes(b);
                Iterator it = iterable.iterator();

                for(int notused = 0; it.hasNext(); ++notused) {
                    Type var5 = (Type)it.next();
                    if(Type.isSame(a, var5)) {
                        return 1;
                    }
                }

                var10000 = -1;
            }
        }

        return var10000;
    }

    public static Type getGenericSuperclass(Type a) {
        return resolveType(a, ((ClassType)a.getRawType()).getGenericSuperclass());
    }

    public static List<Type> getGenericInterfaces(Type a) {
        Type[] gintfs = ((ClassType)a.getRawType()).getGenericInterfaces();
        Object var10000;
        if(gintfs == null) {
            var10000 = Collections.EMPTY_LIST;
        } else {
            ArrayList o = new ArrayList();
            Type[] array = gintfs;

            for(int notused = 0; notused != array.length; ++notused) {
                Type intf = array[notused];
                o.add(resolveType(a, intf));
            }

            var10000 = o;
        }

        return (List)var10000;
    }

    public static LinkedHashSet<Type> superClasses(Type a) {
        LinkedHashSet o = new LinkedHashSet();

        for(Type superA = a; superA != null; superA = getGenericSuperclass(superA)) {
            o.add(superA);
        }

        o.add(Type.javalangObjectType);
        return o;
    }

    public static LinkedHashSet<Type> superIntfs(LinkedHashSet<Type> sc) {
        LinkedHashSet o = new LinkedHashSet();
        Iterator it = sc.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Type superA = (Type)it.next();
            ArrayDeque q = new ArrayDeque();
            q.addAll(getGenericInterfaces(superA));

            while(!q.isEmpty()) {
                Type t = (Type)q.poll();
                o.add(t);
                q.addAll(getGenericInterfaces(t));
            }
        }

        return o;
    }

    public static LinkedHashSet<Type> superTypes(Type a) {
        LinkedHashSet o = superClasses(a);
        o.addAll(superIntfs(o));
        return o;
    }

    public static Type superType(Type a, Type b) {
        LinkedHashSet supersA = superClasses(a);
        LinkedHashSet supersB = superClasses(b);
        Iterator it = supersA.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Type t = (Type)it.next();
            if(supersB.contains(t)) {
                return t;
            }
        }

        LinkedHashSet intfsA = superIntfs(supersA);
        LinkedHashSet intfsB = superIntfs(supersB);
        intfsA.retainAll(intfsB);
        Iterator it1 = intfsA.iterator();

        label31:
        for(int notused1 = 0; it1.hasNext(); ++notused1) {
            Type intf1 = (Type)it1.next();
            Iterator it2 = intfsA.iterator();

            for(int notused2 = 0; it2.hasNext(); ++notused2) {
                Type intf2 = (Type)it2.next();
                if(!((ClassType)intf2.getRawType()).implementsInterface((ClassType)intf1.getRawType())) {
                    continue label31;
                }
            }

            return intf1;
        }

        return Type.objectType;
    }

    public static Type commonType(Type a, Type b) {
        boolean aReturns = a == returnType || a == throwType;
        boolean bReturns = b == returnType || b == throwType;
        Object var10000;
        if(a != Type.voidType && b != Type.voidType) {
            if(aReturns && bReturns) {
                var10000 = Type.voidType;
            } else if(aReturns) {
                var10000 = b;
            } else if(bReturns) {
                var10000 = a;
            } else if(a == Type.nullType) {
                var10000 = tryBox(b);
            } else if(b == Type.nullType) {
                var10000 = tryBox(a);
            } else if(Type.isSame(a, b)) {
                var10000 = a;
            } else if(isNumeric(a) && isNumeric(b)) {
                a = tryUnbox(a);
                b = tryUnbox(b);
                List l = Arrays.asList(new Type[]{a, b});
                if(Type.isSame(a, b)) {
                    var10000 = a;
                } else if(l.contains(Type.doubleType)) {
                    var10000 = Type.doubleType;
                } else if(l.contains(Type.floatType)) {
                    var10000 = Type.floatType;
                } else if(l.contains(Type.longType)) {
                    var10000 = Type.longType;
                } else if(l.contains(Type.intType)) {
                    var10000 = Type.intType;
                } else {
                    if(!l.contains(Type.shortType) || !l.contains(Type.booleanType)) {
                        throw new RuntimeException();
                    }

                    var10000 = Type.shortType;
                }
            } else {
                var10000 = tryUnbox(a) == Type.booleanType && tryUnbox(b) == Type.booleanType?Type.booleanType:superType(a, b);
            }
        } else {
            var10000 = Type.voidType;
        }

        return (Type)var10000;
    }

    public static void generateBridgeMethod(Method target, Type[] params, Type ret) {
        ClassType c = target.getDeclaringClass();
        Type[] rparams = new Type[params.length];

        for(int i = 0; i != params.length; ++i) {
            rparams[i] = params[i].getRawType();
        }

        Method found = c.getDeclaredMethod(target.getName(), rparams);
        if(found == null || !Type.isSame(found.getReturnType(), ret)) {
            Method m = c.addMethod(target.getName(), rparams, ret.getRawType(), Access.PUBLIC | Access.BRIDGE | Access.SYNTHETIC);
            CodeAttr code = m.startCode();
            code.emitPushThis();

            for(int i1 = 0; i1 != rparams.length; ++i1) {
                code.emitLoad(code.getArg(i1 + 1));
                GenHandler.castMaybe(code, rparams[i1], target.getGenericParameterTypes()[i1]);
            }

            code.emitInvoke(target);
            GenHandler.castMaybe(code, target.getReturnType(), ret.getRawType());
            code.emitReturn();
        }

    }

    public static ClassType getCompilerType(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("sjava.compiler.");
        sb.append(name);
        return ClassType.make(sb.toString());
    }

    public static List<Emitter> toEmitters(List l) {
        return l;
    }

    public static List<LexedParsedToken> toLexedParsed(List l) {
        return l;
    }

    public static LexedParsedToken typeToTok(Type t) {
        Object var10000;
        if(t instanceof ArrayType) {
            ArrayType t1 = (ArrayType)t;
            var10000 = new ArrayToken(-1, new ArrayList(Arrays.asList(new Object[]{typeToTok(t1.getComponentType())})));
        } else if(t instanceof ParameterizedType) {
            ParameterizedType t2 = (ParameterizedType)t;
            ArrayList toks = new ArrayList();
            Type[] array = t2.getTypeArgumentTypes();

            for(int notused = 0; notused != array.length; ++notused) {
                Type ta = array[notused];
                toks.add(typeToTok(ta));
            }

            var10000 = new GenericToken(-1, typeToTok(t2.getRawType()), toks);
        } else {
            var10000 = new VToken(-1, t.getName());
        }

        return (LexedParsedToken)var10000;
    }

    public static Tuple2<Integer, Integer> extractModifiers(List<LexedParsedToken> toks, int i) {
        int mods;
        for(mods = 0; i < toks.size(); ++i) {
            LexedParsedToken tok = (LexedParsedToken)toks.get(i);
            if(!(tok instanceof VToken) || !accessModifiers.containsKey(((VToken)tok).val)) {
                return new Tuple2(Integer.valueOf(mods), Integer.valueOf(i));
            }

            mods |= ((Short)accessModifiers.get(((VToken)tok).val)).shortValue();
        }

        return new Tuple2(Integer.valueOf(mods), Integer.valueOf(i));
    }

    public static ArrayClassLoader getClassLoader() {
        return new ArrayClassLoader();
    }
}
