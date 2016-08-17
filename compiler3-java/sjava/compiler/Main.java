package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayClassLoader;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Label;
import gnu.bytecode.Method;
import gnu.bytecode.ParameterizedType;
import gnu.bytecode.PrimType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.FileScope;
import sjava.compiler.Lexer;
import sjava.compiler.Parser;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.emitters.Emitters;
import sjava.compiler.emitters.Goto;
import sjava.compiler.emitters.Nothing;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.handlers.Handler;
import sjava.compiler.mfilters.MFilter;
import sjava.compiler.mfilters.MethodCall;
import sjava.compiler.tokens.AGetToken;
import sjava.compiler.tokens.ALenToken;
import sjava.compiler.tokens.ASetToken;
import sjava.compiler.tokens.AsToken;
import sjava.compiler.tokens.BeginToken;
import sjava.compiler.tokens.BinOpToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.CallToken;
import sjava.compiler.tokens.ClassToken;
import sjava.compiler.tokens.ColonToken;
import sjava.compiler.tokens.CommentToken;
import sjava.compiler.tokens.CompareToken;
import sjava.compiler.tokens.DefaultToken;
import sjava.compiler.tokens.DefineToken;
import sjava.compiler.tokens.EmptyToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.GotoToken;
import sjava.compiler.tokens.IfToken;
import sjava.compiler.tokens.InstanceToken;
import sjava.compiler.tokens.LabelToken;
import sjava.compiler.tokens.LambdaToken;
import sjava.compiler.tokens.MacroCallToken;
import sjava.compiler.tokens.ObjectToken;
import sjava.compiler.tokens.QuoteToken;
import sjava.compiler.tokens.ReturnToken;
import sjava.compiler.tokens.SetToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.SynchronizedToken;
import sjava.compiler.tokens.ThrowToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;
import sjava.compiler.tokens.TryToken;
import sjava.compiler.tokens.TypeToken;
import sjava.compiler.tokens.UnquoteToken;
import sjava.compiler.tokens.VToken;
import sjava.compiler.tokens.WhileToken;

public class Main {
    static int ML;
    static int MP;
    static String[][] precs;
    static HashMap<String, Integer> s2prec;
    public static HashMap<String, Character> specialChars;
    public static HashMap<Type, Method> unboxMethods;
    static HashMap<String, Type> constTypes;
    static HashMap<String, Short> accessModifiers;
    public static HashMap<String, Integer> binOps;
    static HashMap<String, Integer> compare2Ops;
    static HashMap<String, Integer> compare1Ops;
    public static Type unknownType;
    public static Type returnType;
    public static Type throwType;

    public static void main(String[] args) {
        unknownType = Type.getType("unknownType");
        returnType = Type.getType("returnType");
        throwType = Type.getType("throwType");
        unboxMethods = new HashMap();
        unboxMethods.put(Type.shortType.boxedType(), Type.javalangNumberType.getDeclaredMethod("shortValue", 0));
        unboxMethods.put(Type.intType.boxedType(), Type.javalangNumberType.getDeclaredMethod("intValue", 0));
        unboxMethods.put(Type.longType.boxedType(), Type.javalangNumberType.getDeclaredMethod("longValue", 0));
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
        precs = new String[][]{{"\"\"\"", "\"", ")", "}", ";"}, {":", "{"}, {"(", "\'", ",", ",$", "`", "~"}};
        specialChars = new HashMap();
        specialChars.put("space", Character.valueOf(' '));
        specialChars.put("singlequote", Character.valueOf('\''));
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

        ArgumentParser parser = ArgumentParsers.newArgumentParser("sJava compiler");
        parser.addArgument(new String[]{"-d"}).setDefault("");
        parser.addArgument(new String[]{"file"}).nargs("*");
        Namespace res = (Namespace)null;

        try {
            res = parser.parseArgs(args);
        } catch (Throwable var23) {
            var23.printStackTrace();
        }

        List fileNames = res.getList("file");
        LinkedHashMap files = new LinkedHashMap();
        Iterator it = fileNames.iterator();

        for(int notused1 = 0; it.hasNext(); ++notused1) {
            String name = (String)it.next();
            String s = (String)null;

            try {
                s = new String(Files.readAllBytes(Paths.get(name, new String[0])));
            } catch (Throwable var22) {
                var22.printStackTrace();
            }

            ArrayList toks = (new Lexer(s)).lex();
            List toks1 = (new Parser(toks, false)).parseAll();
            StringBuffer sb = new StringBuffer();
            toksToString(toks1, 1, 0, sb);
            sb.append('\n');
            PrintStream var10000 = System.out;
            StringBuilder sb1 = new StringBuilder();
            sb1.append(name);
            sb1.append(" FORMAT:");
            sb1.append(s.equals(sb.toString()));
            var10000.println(sb1.toString());
            toks = (ArrayList)(new Parser(toks)).parseAll();
            files.put(name, toks);
        }

        compile(files, res.getString("d"));
    }

    static void compile(HashMap<String, ArrayList<Token>> files, String dir) {
        HashMap locals = new HashMap();
        LinkedHashMap fileScopes = new LinkedHashMap();
        HashMap macroNames = new HashMap();
        Set iterable = files.entrySet();
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Entry entry = (Entry)it.next();
            ArrayList toks = (ArrayList)entry.getValue();
            FileScope fs = new FileScope((String)entry.getKey(), toks, locals);
            fs.macroNames = macroNames;
            fileScopes.put((String)entry.getKey(), fs);
            fs.compileRoot();
        }

        Set iterable1 = fileScopes.entrySet();
        Iterator it1 = iterable1.iterator();

        for(int notused1 = 0; it1.hasNext(); ++notused1) {
            Entry entry1 = (Entry)it1.next();
            FileScope fs1 = (FileScope)entry1.getValue();
            fs1.compileDefs();
        }

        ArrayClassLoader cl = new ArrayClassLoader();
        Set iterable2 = fileScopes.entrySet();
        Iterator it2 = iterable2.iterator();

        for(int notused2 = 0; it2.hasNext(); ++notused2) {
            Entry entry2 = (Entry)it2.next();
            FileScope fs2 = (FileScope)entry2.getValue();
            fs2.compileMacros(cl);
        }

        Set iterable3 = fileScopes.entrySet();
        Iterator it3 = iterable3.iterator();

        for(int notused3 = 0; it3.hasNext(); ++notused3) {
            Entry entry3 = (Entry)it3.next();
            FileScope fs3 = (FileScope)entry3.getValue();
            fs3.compileIncludes();
        }

        Set iterable4 = fileScopes.entrySet();
        Iterator it4 = iterable4.iterator();

        for(int notused4 = 0; it4.hasNext(); ++notused4) {
            Entry entry4 = (Entry)it4.next();
            FileScope fs4 = (FileScope)entry4.getValue();
            fs4.compileMethods(GenHandler.inst);
        }

        Set iterable5 = fileScopes.entrySet();
        Iterator it5 = iterable5.iterator();

        for(int notused5 = 0; it5.hasNext(); ++notused5) {
            Entry entry5 = (Entry)it5.next();
            FileScope fs5 = (FileScope)entry5.getValue();
            List iterable6 = fs5.newClasses;
            Iterator it6 = iterable6.iterator();

            for(int notused6 = 0; it6.hasNext(); ++notused6) {
                ClassInfo var40 = (ClassInfo)it6.next();
                var40.writeFile(dir);
            }

            List iterable7 = fs5.anonClasses;
            Iterator it7 = iterable7.iterator();

            for(int notused7 = 0; it7.hasNext(); ++notused7) {
                ClassInfo var44 = (ClassInfo)it7.next();
                var44.writeFile(dir);
            }
        }

    }

    public static Type resolveType(Map<TypeVariable, Type> map, Type pt, Type t) {
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

                var10000 = t.getRawType();
            } else {
                var10000 = map != null && map.containsKey((TypeVariable)t)?(Type)map.get((TypeVariable)t):t.getRawType();
            }
        } else if(t instanceof ArrayType) {
            var10000 = new ArrayType(resolveType(map, pt, ((ArrayType)t).elements));
        } else if(t instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType)t).getTypeArgumentTypes();
            Type[] parameterized = new Type[types.length];
            Type[] array1 = types;

            for(int i1 = 0; i1 != array1.length; ++i1) {
                Type type = array1[i1];
                parameterized[i1] = resolveType(map, pt, type);
            }

            var10000 = new ParameterizedType((ClassType)t.getRawType(), parameterized);
        } else {
            var10000 = t;
        }

        return (Type)var10000;
    }

    public static Type resolveType(Type pt, Type t) {
        return resolveType((Map)null, pt, t);
    }

    static Type unresolveTv(TypeVariable tv, Type generic, Type real) {
        Type var10000;
        if(generic instanceof TypeVariable) {
            var10000 = tv.equals(generic)?tryBox(real):(Type)null;
        } else if(generic instanceof ParameterizedType && real instanceof ParameterizedType && generic.getRawType().equals(real.getRawType())) {
            Type[] gtypes = ((ParameterizedType)generic).getTypeArgumentTypes();
            Type[] rtypes = ((ParameterizedType)real).getTypeArgumentTypes();

            for(int i = 0; i < gtypes.length; ++i) {
                Type ret = unresolveTv(tv, gtypes[i], rtypes[i]);
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
                int j = 0;

                Type t;
                for(t = (Type)null; t == null && j != generics.length; ++j) {
                    t = unresolveTv(tv, generics[j], reals[j]);
                }

                out.put(tv, t);
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

    static boolean compileClassMod(Token tok, ClassType c) {
        boolean var10000;
        if(tok instanceof SingleQuoteToken) {
            short nmod = ((Short)accessModifiers.get(((VToken)((Token)tok.toks.get(0))).val)).shortValue();
            c.addModifiers(nmod);
            var10000 = true;
        } else {
            var10000 = false;
        }

        return var10000;
    }

    public static Type[] getParams(ClassInfo ci, Token tok, LinkedHashMap scope, int i, int n) {
        Type[] types = new Type[(tok.toks.size() - i) / 2];

        for(int j = 0; j != types.length; ++j) {
            Type type = ci.getType((Token)tok.toks.get(j * 2 + i + 1));
            types[j] = type;
            scope.put(((VToken)((Token)tok.toks.get(j * 2 + i))).val, new Arg(n + j, type));
        }

        return types;
    }

    public static Token transformBlockTok(Token block, AMethodInfo mi, boolean transform, int i) {
        Token tok = (Token)block.toks.get(i);
        Token ntok = transformBlock(tok, mi, transform && !block.neverTransform || block.alwaysTransform);
        if(transform) {
            ntok.transformed = true;
        }

        if(transform && tok instanceof BlockToken && tok.toks.size() > 0 && (Token)tok.toks.get(0) instanceof VToken) {
            String val = ((VToken)((Token)tok.toks.get(0))).val;
            if(val.equals("label")) {
                ((BlockToken)block).labels.put(((VToken)((Token)tok.toks.get(1))).val, new Label());
            }
        }

        return ntok;
    }

    public static Token transformBlockTokReplace(Token block, AMethodInfo mi, boolean transform, int i) {
        return (Token)block.toks.set(i, transformBlockTok(block, mi, transform, i));
    }

    public static Token transformBlockToks(Token block, AMethodInfo mi, boolean transform, int i) {
        if(transform && block instanceof BlockToken) {
            ((BlockToken)block).labels = new HashMap();
        }

        ArrayList ntoks;
        for(ntoks = new ArrayList(block.toks.subList(0, i)); i != block.toks.size(); ++i) {
            ntoks.add(transformBlockTok(block, mi, transform, i));
        }

        block.toks = ntoks;
        return block;
    }

    public static Token transformBlockToks(Token block, AMethodInfo mi, boolean transform) {
        return transformBlockToks(block, mi, transform, 0);
    }

    public static Token transformBlockToks(Token block, AMethodInfo mi) {
        return transformBlockToks(block, mi, !block.neverTransform);
    }

    static Token transformBlock(Token block, AMethodInfo mi, boolean transform) {
        if(block.toks != null && !block.transformed && !(block instanceof Transformed)) {
            if(transform && block instanceof BlockToken) {
                if(block.toks.size() == 0) {
                    return new EmptyToken(block.line, block.toks);
                }

                if((Token)block.toks.get(0) instanceof VToken) {
                    String val = ((VToken)((Token)block.toks.get(0))).val;
                    if(val.equals("object")) {
                        return new ObjectToken(block.line, block.toks);
                    }

                    if(val.equals("lambda")) {
                        return new LambdaToken(block.line, block.toks);
                    }

                    if(mi.ci.fs.macroNames.containsKey(val)) {
                        return new MacroCallToken(block.line, block.toks);
                    }

                    if(val.equals("begin")) {
                        if(block.toks.size() == 2) {
                            return transformBlock((Token)block.toks.get(1), mi);
                        }

                        return transformBlockToks(new BeginToken(block.line, block.toks), mi);
                    }

                    if(val.equals("label")) {
                        return new LabelToken(block.line, block.toks);
                    }

                    if(val.equals("goto")) {
                        return new GotoToken(block.line, block.toks);
                    }

                    if(val.equals("define")) {
                        return transformBlockToks(new DefineToken(block.line, block.toks), mi);
                    }

                    if(val.equals("try")) {
                        return transformBlockToks(new TryToken(block.line, block.toks), mi);
                    }

                    if(val.equals("instance?")) {
                        return transformBlockToks(new InstanceToken(block.line, block.toks), mi);
                    }

                    if(val.equals("set")) {
                        return transformBlockToks(new SetToken(block.line, block.toks), mi);
                    }

                    if(val.equals("aset")) {
                        return transformBlockToks(new ASetToken(block.line, block.toks), mi);
                    }

                    if(val.equals("aget")) {
                        return transformBlockToks(new AGetToken(block.line, block.toks), mi);
                    }

                    if(val.equals("alen")) {
                        return transformBlockToks(new ALenToken(block.line, block.toks), mi);
                    }

                    if(val.equals("as")) {
                        return transformBlockToks(new AsToken(block.line, block.toks), mi);
                    }

                    if(binOps.containsKey(val)) {
                        return transformBlockToks(new BinOpToken(block.line, block.toks), mi);
                    }

                    if(val.equals("if")) {
                        return transformBlockToks(new IfToken(block.line, block.toks), mi);
                    }

                    if(val.equals("while")) {
                        return transformBlockToks(new WhileToken(block.line, block.toks), mi);
                    }

                    if(isCompare(val)) {
                        return transformBlockToks(new CompareToken(block.line, block.toks), mi);
                    }

                    if(val.equals("throw")) {
                        return transformBlockToks(new ThrowToken(block.line, block.toks), mi);
                    }

                    if(val.equals("class")) {
                        return transformBlockToks(new ClassToken(block.line, block.toks), mi);
                    }

                    if(val.equals("synchronized")) {
                        return transformBlockToks(new SynchronizedToken(block.line, block.toks), mi);
                    }

                    if(val.equals("type")) {
                        return transformBlockToks(new TypeToken(block.line, block.toks), mi);
                    }

                    if(val.equals("return")) {
                        return transformBlockToks(new ReturnToken(block.line, block.toks), mi);
                    }
                } else if((Token)block.toks.get(0) instanceof ColonToken) {
                    CallToken out = new CallToken(block.line, block.toks);
                    transformBlockTokReplace((Token)out.toks.get(0), mi, true, 0);
                    transformBlockToks(out, mi, true, 1);
                    return out;
                }

                return transformBlockToks(new DefaultToken(block.line, block.toks), mi);
            }

            transformBlockToks(block, mi, transform);
        }

        return block;
    }

    public static Token transformBlock(Token block, AMethodInfo mi) {
        return transformBlock(block, mi, !block.neverTransform);
    }

    public static Type numericOpType(Type[] types) {
        List l = Arrays.asList(types);
        PrimType otype = Type.intType;
        if(!l.contains(Type.doubleType) && !l.contains(ClassType.make("java.lang.Double"))) {
            if(l.contains(Type.longType) || l.contains(ClassType.make("java.lang.Long"))) {
                otype = Type.longType;
            }
        } else {
            otype = Type.doubleType;
        }

        return otype;
    }

    public static boolean allNumeric(Type[] types) {
        for(int i = 0; i != types.length; ++i) {
            Type t = tryUnbox(types[i]);
            if(!(t instanceof PrimType)) {
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
        return s.equals("!") || s.equals("&&") || s.equals("||") || compare1Ops.containsKey(s) || compare2Ops.containsKey(s);
    }

    static int invertComp(boolean inv, int n) {
        return inv?((n & 1) != 0?n + 1:n - 1):n;
    }

    public static Type emitInvoke(Handler h, String name, Type type, Emitters emitter, AMethodInfo mi, CodeAttr code, Type needed, boolean special) {
        boolean output = code != null;
        Type[] types = emitter.emitAll(h, mi, (CodeAttr)null, unknownType);
        MFilter filter = new MFilter(name, types, type);
        filter.searchAll();
        MethodCall mc = filter.getMethodCall();
        Method method = mc.m;
        TypeVariable[] typeParameters = method.getTypeParameters();
        Type[] params = method.getGenericParameterTypes();
        boolean varargs = (method.getModifiers() & Access.TRANSIENT) != 0;
        int j = 0;

        int n;
        for(n = !varargs || types.length >= params.length && arrayDim(params[params.length - 1]) == arrayDim(types[params.length - 1])?0:1; j != params.length - n; ++j) {
            ((Emitter)emitter.emitters.get(j)).emit(h, mi, code, resolveType(mc.tvs, mc.t, params[j]));
        }

        if(n == 1) {
            ArrayType at = (ArrayType)params[params.length - 1];
            Type et = resolveType(mc.tvs, mc.t, at.elements);
            if(output) {
                code.emitPushInt(1 + (types.length - params.length));
            }

            if(output) {
                code.emitNewArray(at.elements.getRawType());
            }

            for(int oj = j; j != types.length; ++j) {
                if(output) {
                    code.emitDup();
                }

                if(output) {
                    code.emitPushInt(j - oj);
                }

                ((Emitter)emitter.emitters.get(j)).emit(h, mi, code, et);
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

        return h.castMaybe(code, out, needed);
    }

    public static Type emitInvoke(Handler h, String name, Type type, Emitters emitter, AMethodInfo mi, CodeAttr code, Type needed) {
        return emitInvoke(h, name, type, emitter, mi, code, needed, false);
    }

    static Type compareType(Type[] types) {
        return (Type)(allNumeric(types)?numericOpType(types):Type.objectType);
    }

    public static Type emitIf_(Handler h, boolean inv, Token tok, int i, int e, String compare, Emitter trueE, Emitter falseE, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Type var10000;
        if(compare.equals("!")) {
            var10000 = emitIf(h, !inv, tok, i, trueE, falseE, mi, code, needed);
        } else if(!inv && compare.equals("&&") || inv && compare.equals("||")) {
            Label skipL1 = new Label();
            Label falseL = new Label();
            Goto falseG = new Goto(falseL);

            for(int i2 = 1; i2 != e - 1; ++i2) {
                emitIf(h, inv, tok, i2, Nothing.inst, falseG, mi, code, needed);
            }

            emitIf(h, inv, tok, e - 1, new Emitters(new Emitter[]{trueE, new Goto(skipL1)}), (Emitter)null, mi, code, needed);
            if(output) {
                falseL.define(code);
            }

            if(falseE != null) {
                falseE.emit(h, mi, code, needed);
            }

            if(output) {
                skipL1.define(code);
            }

            var10000 = trueE.emit(h, mi, (CodeAttr)null, needed);
        } else if(!inv && compare.equals("||") || inv && compare.equals("&&")) {
            Label skipL = new Label();
            Label trueL = new Label();
            Goto trueG = new Goto(trueL);

            for(int i1 = 1; i1 != e - 1; ++i1) {
                emitIf(h, !inv, tok, i1, Nothing.inst, trueG, mi, code, needed);
            }

            emitIf(h, !inv, tok, e - 1, new Emitters(new Emitter[]{falseE, new Goto(skipL)}), (Emitter)null, mi, code, needed);
            if(output) {
                trueL.define(code);
            }

            Type type1 = trueE.emit(h, mi, code, needed);
            if(output) {
                skipL.define(code);
            }

            var10000 = type1;
        } else {
            boolean falseLabel = falseE instanceof Goto;
            Label skip = new Label();
            Label label = falseLabel?((Goto)falseE).label:skip;
            if(compare1Ops.containsKey(compare)) {
                for(int j = i; j != e; ++j) {
                    Type[] types = h.compileAll(tok.toks, j, j + 1, mi, (CodeAttr)null, unknownType);
                    Type otype = compareType(types);
                    h.compileAll(tok.toks, j, j + 1, mi, code, otype);
                    if(output) {
                        code.emitGotoIfCompare1(label, invertComp(inv, ((Integer)compare1Ops.get(compare)).intValue()));
                    }
                }
            } else {
                for(int j1 = i; j1 + 1 != e; ++j1) {
                    Type[] types1 = h.compileAll(tok.toks, j1, j1 + 2, mi, (CodeAttr)null, unknownType);
                    Type otype1 = compareType(types1);
                    h.compileAll(tok.toks, j1, j1 + 2, mi, code, otype1);
                    if(output) {
                        code.emitGotoIfCompare2(label, invertComp(inv, ((Integer)compare2Ops.get(compare)).intValue()));
                    }
                }
            }

            Type type = trueE.emit(h, mi, code, needed);
            if(!falseLabel) {
                Label end = new Label();
                if(!(trueE instanceof Nothing) && falseE != null && output && code.reachableHere()) {
                    code.emitGoto(end);
                }

                if(output) {
                    skip.define(code);
                }

                if(falseE != null) {
                    falseE.emit(h, mi, code, needed);
                    if(output) {
                        end.define(code);
                    }
                }
            }

            var10000 = type;
        }

        return var10000;
    }

    public static Type emitIf(Handler h, boolean inv, Token tok, int i, Emitter trueE, Emitter falseE, AMethodInfo mi, CodeAttr code, Type needed) {
        Token cond = (Token)tok.toks.get(i);
        return cond instanceof CompareToken?emitIf_(h, inv, cond, 1, cond.toks.size(), ((VToken)((Token)cond.toks.get(0))).val, trueE, falseE, mi, code, needed):emitIf_(h, inv, tok, i, i + 1, "!=0", trueE, falseE, mi, code, needed);
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
            int i1 = 0;
            code.emitPushThis();

            while(i1 != rparams.length) {
                code.emitLoad(code.getArg(i1 + 1));
                GenHandler.inst.castMaybe(code, rparams[i1], target.getGenericParameterTypes()[i1]);
                ++i1;
            }

            code.emitInvoke(target);
            GenHandler.inst.castMaybe(code, target.getReturnType(), ret.getRawType());
            code.emitReturn();
        }

    }

    public static ClassType getCompilerType(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("sjava.compiler.");
        sb.append(name);
        return ClassType.make(sb.toString());
    }

    static int tokToString(Token tok, int line, int tabs, StringBuffer sb) {
        for(int i = tok.line - line; i > 0; --i) {
            sb.append("\n");
        }

        if(tok.line != line) {
            for(int i1 = tabs; i1 > 0; --i1) {
                sb.append("\t");
            }
        }

        line = tok.line;
        if(tok instanceof BlockToken) {
            blockTokToString(tok, line, tabs, sb);
        } else if(tok instanceof GenericToken) {
            line = tokToString(((GenericToken)tok).tok, line, tabs, sb);
            sb.append("{");
            toksToString(tok.toks, line, tabs, sb);
            sb.append("}");
        } else if(tok instanceof ColonToken) {
            line = tokToString((Token)tok.toks.get(0), line, tabs, sb);
            sb.append(":");
            tokToString((Token)tok.toks.get(1), line, tabs, sb);
        } else if(tok instanceof SingleQuoteToken) {
            sb.append("\'");
            tokToString((Token)tok.toks.get(0), line, tabs, sb);
        } else if(tok instanceof QuoteToken) {
            sb.append(((QuoteToken)tok).transform?"`":"~");
            tokToString((Token)tok.toks.get(0), line, tabs, sb);
        } else if(tok instanceof UnquoteToken) {
            sb.append(((UnquoteToken)tok).s?",$":",");
            tokToString((Token)tok.toks.get(0), line, tabs, sb);
        } else if(tok instanceof CommentToken) {
            sb.append(";");
            sb.append(((CommentToken)tok).val);
        } else {
            sb.append(tok.toString());
        }

        return tok.endLine;
    }

    static int blockTokToString(Token block, int line, int tabs, StringBuffer sb) {
        sb.append("(");
        int i = 0;
        List iterable = block.toks;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Token tok = (Token)it.next();
            if(i != 0 && tok.line == line) {
                sb.append(" ");
            }

            line = tokToString(tok, line, tabs + (tok.line == block.line?0:1), sb);
            ++i;
        }

        for(int i1 = block.endLine - line; i1 > 0; --i1) {
            sb.append("\n");
        }

        if(line != block.endLine) {
            for(int i2 = tabs; i2 > 0; --i2) {
                sb.append("\t");
            }
        }

        sb.append(")");
        line = block.endLine;
        return line;
    }

    static int toksToString(List<Token> toks, int line, int tabs, StringBuffer sb) {
        int i = 0;
        Iterator it = toks.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Token tok = (Token)it.next();
            if(i != 0 && tok.line == line) {
                sb.append(" ");
            }

            line = tokToString(tok, line, tabs, sb);
            ++i;
        }

        return line;
    }
}
