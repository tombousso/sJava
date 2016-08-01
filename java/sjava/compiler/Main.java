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
import org.apache.commons.lang3.StringEscapeUtils;
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
import sjava.compiler.tokens.CToken;
import sjava.compiler.tokens.CallToken;
import sjava.compiler.tokens.ClassToken;
import sjava.compiler.tokens.ColonToken;
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
import sjava.compiler.tokens.NToken;
import sjava.compiler.tokens.ObjectToken;
import sjava.compiler.tokens.QuoteToken;
import sjava.compiler.tokens.ReturnToken;
import sjava.compiler.tokens.SToken;
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
        precs = new String[][]{{":", "{"}, {"(", ")", "}", "\'", ",", ",$", "`", "~", "\n", "\r\n", ";"}};
        specialChars = new HashMap();
        specialChars.put("space", Character.valueOf(' '));
        specialChars.put("singlequote", Character.valueOf('\''));
        MP = precs.length - 1;
        ML = 2;
        s2prec = new HashMap();
        String[][] array = precs;

        for(int i = 0; i != array.length; ++i) {
            String[] a = array[i];
            String[] array1 = a;

            for(int notused = 0; notused != array1.length; ++notused) {
                String iterable = array1[notused];
                s2prec.put(iterable, Integer.valueOf(i + 1));
            }
        }

        ArgumentParser var15 = ArgumentParsers.newArgumentParser("sJava compiler");
        var15.addArgument(new String[]{"-d"}).setDefault("");
        var15.addArgument(new String[]{"file"}).nargs("*");
        Namespace var16 = (Namespace)null;

        try {
            var16 = var15.parseArgs(args);
        } catch (Throwable var14) {
            var14.printStackTrace();
        }

        List var17 = var16.getList("file");
        LinkedHashMap var18 = new LinkedHashMap();
        Iterator it = var17.iterator();

        for(int notused1 = 0; it.hasNext(); ++notused1) {
            String name = (String)it.next();
            String s = (String)null;

            try {
                s = new String(Files.readAllBytes(Paths.get(name, new String[0])));
            } catch (Throwable var13) {
                var13.printStackTrace();
            }

            ArrayList toks = (new Lexer(s)).lex();
            toks = (ArrayList)(new Parser(toks)).parseAll();
            var18.put(name, toks);
        }

        compile(var18, var16.getString("d"));
    }

    static void compile(HashMap<String, ArrayList<Token>> files, String dir) {
        HashMap locals = new HashMap();
        LinkedHashMap fileScopes = new LinkedHashMap();
        HashMap macroNames = new HashMap();
        Set iterable = files.entrySet();
        Iterator iterable1 = iterable.iterator();

        int it;
        Entry notused;
        FileScope fs;
        for(it = 0; iterable1.hasNext(); ++it) {
            notused = (Entry)iterable1.next();
            ArrayList entry = (ArrayList)notused.getValue();
            fs = new FileScope((String)notused.getKey(), entry, locals);
            fs.macroNames = macroNames;
            fileScopes.put((String)notused.getKey(), fs);
            fs.compileRoot();
        }

        iterable = fileScopes.entrySet();
        iterable1 = iterable.iterator();

        for(it = 0; iterable1.hasNext(); ++it) {
            notused = (Entry)iterable1.next();
            FileScope var19 = (FileScope)notused.getValue();
            var19.compileDefs();
        }

        ArrayClassLoader var15 = new ArrayClassLoader();
        Set var16 = fileScopes.entrySet();
        Iterator var17 = var16.iterator();

        int var18;
        Entry var20;
        for(var18 = 0; var17.hasNext(); ++var18) {
            var20 = (Entry)var17.next();
            fs = (FileScope)var20.getValue();
            fs.compileMacros(var15);
        }

        var16 = fileScopes.entrySet();
        var17 = var16.iterator();

        for(var18 = 0; var17.hasNext(); ++var18) {
            var20 = (Entry)var17.next();
            fs = (FileScope)var20.getValue();
            fs.compileIncludes();
        }

        var16 = fileScopes.entrySet();
        var17 = var16.iterator();

        for(var18 = 0; var17.hasNext(); ++var18) {
            var20 = (Entry)var17.next();
            fs = (FileScope)var20.getValue();
            fs.compileMethods(GenHandler.inst);
        }

        var16 = fileScopes.entrySet();
        var17 = var16.iterator();

        for(var18 = 0; var17.hasNext(); ++var18) {
            var20 = (Entry)var17.next();
            fs = (FileScope)var20.getValue();
            List iterable2 = fs.newClasses;
            Iterator it1 = iterable2.iterator();

            int notused1;
            ClassInfo var14;
            for(notused1 = 0; it1.hasNext(); ++notused1) {
                var14 = (ClassInfo)it1.next();
                var14.writeFile(dir);
            }

            iterable2 = fs.anonClasses;
            it1 = iterable2.iterator();

            for(notused1 = 0; it1.hasNext(); ++notused1) {
                var14 = (ClassInfo)it1.next();
                var14.writeFile(dir);
            }
        }

    }

    public static Type resolveType(Map<TypeVariable, Type> map, Type pt, Type t) {
        int i;
        Object var10000;
        if(t instanceof TypeVariable) {
            if(pt instanceof ParameterizedType) {
                TypeVariable[] types = ((ParameterizedType)pt).getRawType().getTypeParameters();
                String parameterized = ((TypeVariable)t).getName();
                TypeVariable[] array = types;

                for(i = 0; i != array.length; ++i) {
                    TypeVariable type = array[i];
                    if(type.getName().equals(parameterized)) {
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
            Type[] var8 = ((ParameterizedType)t).getTypeArgumentTypes();
            Type[] var9 = new Type[var8.length];
            Type[] var10 = var8;

            for(i = 0; i != var10.length; ++i) {
                Type var11 = var10[i];
                var9[i] = resolveType(map, pt, var11);
            }

            var10000 = new ParameterizedType((ClassType)t.getRawType(), var9);
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
        if(tvs == null || generics.length != reals.length) {
            var10000 = (Map)null;
        } else {
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

    public static void transformBlockTok(Token block, AMethodInfo mi, boolean transform, int i) {
        Token tok = (Token)block.toks.get(i);
        Token ntok = transformBlock(tok, mi, transform && !block.neverTransform || block.alwaysTransform);
        if(transform) {
            ntok.transformed = true;
        }

        block.toks.set(i, ntok);
        if(transform && tok instanceof BlockToken && tok.toks.size() > 0 && (Token)tok.toks.get(0) instanceof VToken) {
            String val = ((VToken)((Token)tok.toks.get(0))).val;
            if(val.equals("label")) {
                ((BlockToken)block).labels.put(((VToken)((Token)tok.toks.get(1))).val, new Label());
            }
        }

    }

    public static Token transformBlockToks(Token block, AMethodInfo mi, boolean transform, int i) {
        if(transform && block instanceof BlockToken) {
            ((BlockToken)block).labels = new HashMap();
        }

        while(i != block.toks.size()) {
            transformBlockTok(block, mi, transform, i);
            ++i;
        }

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
                    String out = ((VToken)((Token)block.toks.get(0))).val;
                    if(out.equals("object")) {
                        return new ObjectToken(block.line, block.toks);
                    }

                    if(out.equals("lambda")) {
                        return new LambdaToken(block.line, block.toks);
                    }

                    if(mi.ci.fs.macroNames.containsKey(out)) {
                        return new MacroCallToken(block.line, block.toks);
                    }

                    if(out.equals("begin")) {
                        if(block.toks.size() == 2) {
                            return transformBlock((Token)block.toks.get(1), mi);
                        }

                        return transformBlockToks(new BeginToken(block.line, block.toks), mi);
                    }

                    if(out.equals("label")) {
                        return new LabelToken(block.line, block.toks);
                    }

                    if(out.equals("goto")) {
                        return new GotoToken(block.line, block.toks);
                    }

                    if(out.equals("define")) {
                        return transformBlockToks(new DefineToken(block.line, block.toks), mi);
                    }

                    if(out.equals("try")) {
                        return transformBlockToks(new TryToken(block.line, block.toks), mi);
                    }

                    if(out.equals("instance?")) {
                        return transformBlockToks(new InstanceToken(block.line, block.toks), mi);
                    }

                    if(out.equals("set")) {
                        return transformBlockToks(new SetToken(block.line, block.toks), mi);
                    }

                    if(out.equals("aset")) {
                        return transformBlockToks(new ASetToken(block.line, block.toks), mi);
                    }

                    if(out.equals("aget")) {
                        return transformBlockToks(new AGetToken(block.line, block.toks), mi);
                    }

                    if(out.equals("alen")) {
                        return transformBlockToks(new ALenToken(block.line, block.toks), mi);
                    }

                    if(out.equals("as")) {
                        return transformBlockToks(new AsToken(block.line, block.toks), mi);
                    }

                    if(binOps.containsKey(out)) {
                        return transformBlockToks(new BinOpToken(block.line, block.toks), mi);
                    }

                    if(out.equals("if")) {
                        return transformBlockToks(new IfToken(block.line, block.toks), mi);
                    }

                    if(out.equals("while")) {
                        return transformBlockToks(new WhileToken(block.line, block.toks), mi);
                    }

                    if(isCompare(out)) {
                        return transformBlockToks(new CompareToken(block.line, block.toks), mi);
                    }

                    if(out.equals("throw")) {
                        return transformBlockToks(new ThrowToken(block.line, block.toks), mi);
                    }

                    if(out.equals("class")) {
                        return transformBlockToks(new ClassToken(block.line, block.toks), mi);
                    }

                    if(out.equals("synchronized")) {
                        return transformBlockToks(new SynchronizedToken(block.line, block.toks), mi);
                    }

                    if(out.equals("type")) {
                        return transformBlockToks(new TypeToken(block.line, block.toks), mi);
                    }

                    if(out.equals("return")) {
                        return transformBlockToks(new ReturnToken(block.line, block.toks), mi);
                    }
                } else if((Token)block.toks.get(0) instanceof ColonToken) {
                    CallToken out1 = new CallToken(block.line, block.toks);
                    transformBlockTok((Token)out1.toks.get(0), mi, true, 0);
                    transformBlockToks(out1, mi, true, 1);
                    return out1;
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
        if(l.contains(Type.doubleType) || l.contains(ClassType.make("java.lang.Double"))) {
            otype = Type.doubleType;
        } else if(l.contains(Type.longType) || l.contains(ClassType.make("java.lang.Long"))) {
            otype = Type.longType;
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
        for(n = varargs && (types.length < params.length || arrayDim(params[params.length - 1]) != arrayDim(types[params.length - 1]))?1:0; j != params.length - n; ++j) {
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

        Type var21 = resolveType(mc.tvs, mc.t, method.getReturnType());
        if(var21 != Type.voidType && needed != Type.voidType && output) {
            code.emitCheckcast(var21.getRawType());
        }

        return h.castMaybe(code, var21, needed);
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
        } else {
            Label falseLabel;
            Label skip;
            Goto label;
            int j;
            if(!inv && compare.equals("&&") || inv && compare.equals("||")) {
                falseLabel = new Label();
                skip = new Label();
                label = new Goto(skip);

                for(j = 1; j != e - 1; ++j) {
                    emitIf(h, inv, tok, j, Nothing.inst, label, mi, code, needed);
                }

                emitIf(h, inv, tok, e - 1, new Emitters(new Emitter[]{trueE, new Goto(falseLabel)}), (Emitter)null, mi, code, needed);
                if(output) {
                    skip.define(code);
                }

                if(falseE != null) {
                    falseE.emit(h, mi, code, needed);
                }

                if(output) {
                    falseLabel.define(code);
                }

                var10000 = trueE.emit(h, mi, (CodeAttr)null, needed);
            } else if(!inv && compare.equals("||") || inv && compare.equals("&&")) {
                falseLabel = new Label();
                skip = new Label();
                label = new Goto(skip);

                for(j = 1; j != e - 1; ++j) {
                    emitIf(h, !inv, tok, j, Nothing.inst, label, mi, code, needed);
                }

                emitIf(h, !inv, tok, e - 1, new Emitters(new Emitter[]{falseE, new Goto(falseLabel)}), (Emitter)null, mi, code, needed);
                if(output) {
                    skip.define(code);
                }

                Type end = trueE.emit(h, mi, code, needed);
                if(output) {
                    falseLabel.define(code);
                }

                var10000 = end;
            } else {
                boolean var18 = falseE instanceof Goto;
                skip = new Label();
                Label var19 = var18?((Goto)falseE).label:skip;
                Type otype;
                Type[] var20;
                if(compare1Ops.containsKey(compare)) {
                    for(j = i; j != e; ++j) {
                        var20 = h.compileAll(tok.toks, j, j + 1, mi, (CodeAttr)null, unknownType);
                        otype = compareType(var20);
                        h.compileAll(tok.toks, j, j + 1, mi, code, otype);
                        if(output) {
                            code.emitGotoIfCompare1(var19, invertComp(inv, ((Integer)compare1Ops.get(compare)).intValue()));
                        }
                    }
                } else {
                    for(j = i; j + 1 != e; ++j) {
                        var20 = h.compileAll(tok.toks, j, j + 2, mi, (CodeAttr)null, unknownType);
                        otype = compareType(var20);
                        h.compileAll(tok.toks, j, j + 2, mi, code, otype);
                        if(output) {
                            code.emitGotoIfCompare2(var19, invertComp(inv, ((Integer)compare2Ops.get(compare)).intValue()));
                        }
                    }
                }

                Type var22 = trueE.emit(h, mi, code, needed);
                if(!var18) {
                    Label var21 = new Label();
                    if(!(trueE instanceof Nothing) && falseE != null && output && code.reachableHere()) {
                        code.emitGoto(var21);
                    }

                    if(output) {
                        skip.define(code);
                    }

                    if(falseE != null) {
                        falseE.emit(h, mi, code, needed);
                        if(output) {
                            var21.define(code);
                        }
                    }
                }

                var10000 = var22;
            }
        }

        return var10000;
    }

    public static Type emitIf(Handler h, boolean inv, Token tok, int i, Emitter trueE, Emitter falseE, AMethodInfo mi, CodeAttr code, Type needed) {
        Token cond = (Token)tok.toks.get(i);
        return cond instanceof BlockToken && (Token)cond.toks.get(0) instanceof VToken && isCompare(((VToken)((Token)cond.toks.get(0))).val)?emitIf_(h, inv, cond, 1, cond.toks.size(), ((VToken)((Token)cond.toks.get(0))).val, trueE, falseE, mi, code, needed):emitIf_(h, inv, tok, i, i + 1, "!=0", trueE, falseE, mi, code, needed);
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

    static int toksToString(Token tok, int line, StringBuffer sb) {
        for(int c = tok.line - line; c > 0; --c) {
            sb.append("\n");
        }

        line = tok.line;
        if(tok instanceof SToken) {
            sb.append('\"');
            sb.append(StringEscapeUtils.escapeJava(((SToken)tok).val));
            sb.append('\"');
        } else if(tok instanceof NToken) {
            sb.append(((NToken)tok).val.toString());
        } else if(tok instanceof CToken) {
            String var8 = ((CToken)tok).val.toString();
            Set iterable = specialChars.entrySet();
            Iterator it = iterable.iterator();

            for(int notused = 0; it.hasNext(); ++notused) {
                Entry entry = (Entry)it.next();
                if(((CToken)tok).val.equals((Character)entry.getValue())) {
                    var8 = (String)entry.getKey();
                }
            }

            sb.append("#\\");
            sb.append(var8);
        } else if(tok instanceof VToken) {
            sb.append(((VToken)tok).val);
        } else if(tok instanceof BlockToken) {
            sb.append("(");
            line = toksToString(tok.toks, line, sb);
            sb.append(")");
        } else if(tok instanceof GenericToken) {
            line = toksToString(((GenericToken)tok).tok, line, sb);
            sb.append("{");
            line = toksToString(tok.toks, line, sb);
            sb.append("}");
        } else if(tok instanceof ColonToken) {
            line = toksToString((Token)tok.toks.get(0), line, sb);
            sb.append(":");
            line = toksToString((Token)tok.toks.get(1), line, sb);
        } else if(tok instanceof SingleQuoteToken) {
            sb.append("\'");
            line = toksToString((Token)tok.toks.get(0), line, sb);
        } else if(tok instanceof QuoteToken) {
            sb.append("`");
            line = toksToString((Token)tok.toks.get(0), line, sb);
        } else if(tok instanceof UnquoteToken) {
            sb.append(((UnquoteToken)tok).s?",$":",");
            line = toksToString((Token)tok.toks.get(0), line, sb);
        } else {
            sb.append(tok.what);
        }

        return line;
    }

    static int toksToString(List<Token> toks, int line, StringBuffer sb) {
        int i = 0;
        Iterator it = toks.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Token tok = (Token)it.next();
            line = toksToString(tok, line, sb);
            ++i;
            if(i != toks.size()) {
                sb.append(" ");
            }
        }

        return line;
    }
}
