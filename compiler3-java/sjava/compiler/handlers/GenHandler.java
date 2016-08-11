package sjava.compiler.handlers;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Field;
import gnu.bytecode.Label;
import gnu.bytecode.Method;
import gnu.bytecode.ObjectType;
import gnu.bytecode.ParameterizedType;
import gnu.bytecode.PrimType;
import gnu.bytecode.Type;
import gnu.bytecode.Variable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.AVar;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.Main;
import sjava.compiler.MethodInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.emitters.Emitters;
import sjava.compiler.emitters.Goto;
import sjava.compiler.emitters.LoadAVar;
import sjava.compiler.handlers.CaptureVHandler;
import sjava.compiler.handlers.Handler;
import sjava.compiler.mfilters.MFilter;
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
import sjava.compiler.tokens.ConstToken;
import sjava.compiler.tokens.DefaultToken;
import sjava.compiler.tokens.DefineToken;
import sjava.compiler.tokens.EmptyToken;
import sjava.compiler.tokens.GotoToken;
import sjava.compiler.tokens.IfToken;
import sjava.compiler.tokens.IncludeToken;
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
import sjava.compiler.tokens.SynchronizedToken;
import sjava.compiler.tokens.ThrowToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.TryToken;
import sjava.compiler.tokens.TypeToken;
import sjava.compiler.tokens.UnquoteToken;
import sjava.compiler.tokens.VToken;
import sjava.compiler.tokens.WhileToken;

public class GenHandler extends Handler {
    public static GenHandler inst = new GenHandler();

    public Type compile(EmptyToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        return Type.voidType;
    }

    public Type compile(SToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(output) {
            code.emitPushString(tok.val);
        }

        return Type.javalangStringType;
    }

    public Type compile(CToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        char c = tok.val.charValue();
        Object var10000;
        if(!(needed instanceof PrimType)) {
            if(output) {
                code.emitPushInt(c);
            }

            var10000 = Type.charType;
        } else {
            if(output) {
                code.emitPushConstant(c, needed);
            }

            var10000 = needed;
        }

        return (Type)var10000;
    }

    public Type compile(NToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Object var10000;
        if(tok.val instanceof Double) {
            if(output) {
                code.emitPushDouble(((Double)tok.val).doubleValue());
            }

            var10000 = Type.doubleType;
        } else if(!(needed instanceof PrimType)) {
            if(output) {
                code.emitPushInt(((Integer)tok.val).intValue());
            }

            var10000 = Type.intType;
        } else {
            if(output) {
                code.emitPushConstant(((Integer)tok.val).intValue(), needed);
            }

            var10000 = needed;
        }

        return (Type)var10000;
    }

    public Type compile(ColonToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Token first = (Token)tok.toks.get(0);
        Type t = mi.getType(first);
        if(t == null) {
            t = this.compile(first, mi, code, Main.unknownType);
        }

        ClassType var8 = (ClassType)t.getRawType();
        Field field = var8.getField(((VToken)((Token)tok.toks.get(1))).val, -1);
        if(field.getStaticFlag()) {
            if(output) {
                code.emitGetStatic(field);
            }
        } else if(output) {
            code.emitGetField(field);
        }

        Type out = Main.resolveType(t, field.getType());
        if(out != Type.voidType && output) {
            code.emitCheckcast(out.getRawType());
        }

        return out;
    }

    Type compileQuasi(Object o, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Object var10000;
        if(o == null) {
            if(output) {
                code.emitPushNull();
            }

            var10000 = Type.nullType;
        } else if(o instanceof Token) {
            Token tok = (Token)o;
            Type t = tok instanceof UnquoteToken?this.compile((Token)tok.toks.get(0), mi, (CodeAttr)null, Main.unknownType):null;
            if(tok instanceof UnquoteToken && (t == Main.getCompilerType("tokens.Token") || t instanceof ArrayType && ((ArrayType)t).elements == Main.getCompilerType("tokens.Token") || t.getRawType().isSubtype(Type.getType("java.util.List")))) {
                var10000 = this.compile((Token)tok.toks.get(0), mi, code, Main.unknownType);
            } else if(tok instanceof UnquoteToken) {
                Token var8 = (Token)tok.toks.get(0);
                Type t1 = this.compile(var8, mi, (CodeAttr)null, Main.unknownType);
                ClassType type = Main.getCompilerType(((UnquoteToken)tok).s?"tokens.SToken":(t1 == Type.charType?"tokens.CToken":(t1 instanceof PrimType?"tokens.NToken":"tokens.VToken")));
                if(output) {
                    code.emitNew(type);
                }

                if(output) {
                    code.emitDup();
                }

                if(output) {
                    code.emitInvoke(type.getDeclaredMethod("<init>", 0));
                }

                if(output) {
                    code.emitDup();
                }

                this.compile((Token)var8, mi, code, Type.objectType);
                if(output) {
                    code.emitPutField(type.getField("val"));
                }

                var10000 = Main.getCompilerType("tokens.Token");
            } else {
                ClassType type1 = (ClassType)Type.getType(o.getClass().getName());
                if(output) {
                    code.emitNew(type1);
                }

                if(output) {
                    code.emitDup();
                }

                if(output) {
                    code.emitInvoke(type1.getDeclaredMethod("<init>", 0));
                }

                for(ClassType superC = type1; superC != null; superC = superC.getSuperclass()) {
                    for(Field field = superC.getFields(); field != null; field = field.getNext()) {
                        if((field.getModifiers() & Access.PUBLIC) != 0 && (field.getModifiers() & Access.TRANSIENT) == 0) {
                            if(output) {
                                code.emitDup();
                            }

                            try {
                                if(field.getType() == Type.intType) {
                                    this.compileQuasi(Integer.valueOf(field.getReflectField().getInt(tok)), mi, code, field.getType());
                                } else {
                                    this.compileQuasi(field.getReflectField().get(tok), mi, code, field.getType());
                                }
                            } catch (Throwable var20) {
                                throw new RuntimeException(var20);
                            }

                            if(output) {
                                code.emitPutField(field);
                            }
                        }
                    }
                }

                if(type1 == Main.getCompilerType("tokens.VToken")) {
                    if(output) {
                        code.emitDup();
                    }

                    if(output) {
                        code.emitLoad(code.getArg(2));
                    }

                    if(output) {
                        code.emitPutField(type1.getField("macro"));
                    }
                }

                var10000 = Main.getCompilerType("tokens.Token");
            }
        } else if(o instanceof String) {
            if(output) {
                code.emitPushString((String)o);
            }

            var10000 = Type.javalangStringType;
        } else if(o instanceof Integer) {
            if(output) {
                code.emitPushInt(((Integer)o).intValue());
            }

            var10000 = Type.intType;
        } else if(o instanceof Boolean) {
            if(output) {
                code.emitPushInt(((Boolean)o).booleanValue()?1:0);
            }

            var10000 = Type.booleanType;
        } else {
            if(!(o instanceof List)) {
                throw new RuntimeException();
            }

            List o1 = (List)o;
            ClassType al = (ClassType)Type.getType("java.util.ArrayList");
            if(output) {
                code.emitNew(al);
            }

            if(output) {
                code.emitDup();
            }

            if(output) {
                code.emitInvoke(al.getMethod("<init>", new Type[0]));
            }

            for(int i = 0; i != o1.size(); ++i) {
                Type t2 = this.compileQuasi(o1.get(i), mi, (CodeAttr)null, Main.unknownType);
                if(t2 == Main.getCompilerType("tokens.Token")) {
                    if(output) {
                        code.emitDup();
                    }

                    this.compileQuasi(o1.get(i), mi, code, Main.unknownType);
                    if(output) {
                        code.emitInvoke(al.getMethod("add", new Type[]{Type.objectType}));
                    }

                    if(output) {
                        code.emitPop(1);
                    }
                } else if(t2 instanceof ArrayType) {
                    if(output) {
                        code.emitDup();
                    }

                    this.compileQuasi(o1.get(i), mi, code, Main.unknownType);
                    if(output) {
                        code.emitInvoke(((ClassType)Type.getType("java.util.Collections")).getDeclaredMethod("addAll", 2));
                    }

                    if(output) {
                        code.emitPop(1);
                    }
                } else {
                    if(output) {
                        code.emitDup();
                    }

                    this.compileQuasi(o1.get(i), mi, code, Main.unknownType);
                    if(output) {
                        code.emitInvoke(((ClassType)Type.getType("java.util.List")).getDeclaredMethod("addAll", 1));
                    }

                    if(output) {
                        code.emitPop(1);
                    }
                }
            }

            var10000 = al;
        }

        Object result = var10000;
        return this.castMaybe(code, (Type)result, needed);
    }

    public Type compile(QuoteToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Type out = this.compileQuasi((Token)tok.toks.get(0), mi, code, Main.getCompilerType("tokens.Token"));
        if(tok.transform) {
            if(output) {
                code.emitLoad(code.getArg(0));
            }

            if(output) {
                code.emitInvoke(Main.getCompilerType("Main").getDeclaredMethod("transformBlock", 2));
            }
        }

        return out;
    }

    public Type compile(ConstToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Object var10000;
        if(tok.what.equals("true")) {
            if(output) {
                code.emitPushInt(1);
            }

            var10000 = Type.booleanType;
        } else if(tok.what.equals("false")) {
            if(output) {
                code.emitPushInt(0);
            }

            var10000 = Type.booleanType;
        } else {
            if(output) {
                code.emitPushNull();
            }

            var10000 = Type.nullType;
        }

        return (Type)var10000;
    }

    public Type compile(VToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        AVar found = mi.getVar(tok);
        return found.load(code);
    }

    public Type compile(IncludeToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(tok.ret == null) {
            try {
                tok.ret = (Token)mi.ci.fs.includes.rc.getMethod(tok.mi.method.getName(), new Class[]{AMethodInfo.class, Type.class, Integer.TYPE, Handler.class}).invoke((Object)null, new Object[]{mi, needed, Integer.valueOf(0), this});
            } catch (Throwable var7) {
                throw new RuntimeException(var7);
            }
        }

        return this.compile(tok.ret, mi, code, needed);
    }

    public Type compile(ObjectToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        boolean lambda = tok instanceof LambdaToken;
        ClassInfo ci = tok.ci;
        Token superT = (Token)tok.toks.get(1);
        boolean FunctionN = lambda && superT instanceof BlockToken;
        Emitters emitters = new Emitters((List)(!lambda && superT instanceof BlockToken?superT.toks.subList(1, superT.toks.size()):new ArrayList()));
        if(ci == null) {
            CaptureVHandler captureH = new CaptureVHandler(mi);
            if(output) {
                ci = new ClassInfo(mi.ci.c.getName().concat("$".concat(Integer.toString(mi.ci.anonymous))), mi.ci.fs);
                tok.ci = ci;
                mi.ci.fs.anonClasses.add(ci);
                ++mi.ci.anonymous;
            }

            if(FunctionN) {
                LinkedHashMap scope = new LinkedHashMap();
                Type[] params = Main.getParams(mi.ci, superT, scope, 0, 1);
                Type[] generics = new Type[params.length + 1];
                System.arraycopy(params, 0, generics, 0, params.length);
                List toks = tok.toks.subList(2, tok.toks.size());
                MethodInfo fakemi = new MethodInfo(new ClassInfo((ClassType)null, mi.ci.fs), toks, (Method)null, scope);
                Type ret = Main.tryBox(captureH.compile((Token)fakemi.block, fakemi, (CodeAttr)null, Main.unknownType));
                generics[params.length] = ret;
                ParameterizedType t = new ParameterizedType(ClassType.make("sjava.std.Function".concat(Integer.toString(params.length))), generics);
                tok.t = t;
                if(output) {
                    Method m = ci.c.addMethod("apply", params, ret, Access.PUBLIC);
                    ci.c.addInterface(t);
                    ci.methods.add(new MethodInfo(ci, toks, m, scope));
                    ci.compileMethods(captureH);
                }
            } else if(lambda) {
                Type t1 = mi.getType(superT);
                tok.t = t1;
                if(output) {
                    Method sam = ((ClassType)t1.getRawType()).checkSingleAbstractMethod();
                    LinkedHashMap scope1 = new LinkedHashMap();
                    Token args = (Token)tok.toks.get(2);
                    int i = 0;

                    Type[] params1;
                    for(params1 = new Type[args.toks.size()]; i != args.toks.size(); ++i) {
                        VToken arg = (VToken)((Token)args.toks.get(i));
                        Type param = Main.resolveType(t1, sam.getGenericParameterTypes()[i]);
                        scope1.put(arg.val, new Arg(i + 1, param));
                        params1[i] = param;
                    }

                    ci.c.addInterface(t1);
                    Method m1 = ci.c.addMethod(sam.getName(), params1, Main.resolveType(t1, sam.getReturnType()), Access.PUBLIC);
                    ci.methods.add(new MethodInfo(ci, tok.toks.subList(3, tok.toks.size()), m1, scope1));
                    ci.compileMethods(captureH);
                }
            } else {
                Type t2 = superT instanceof BlockToken?mi.getType((Token)superT.toks.get(0)):mi.getType(superT);
                tok.t = t2;
                if(output) {
                    if(((ClassType)t2.getRawType()).isInterface()) {
                        ci.c.addInterface(t2);
                    } else {
                        ci.c.setSuper(t2);
                    }

                    for(int i1 = 2; i1 != tok.toks.size(); ++i1) {
                        ci.compileDef((Token)tok.toks.get(i1));
                    }

                    ci.compileMethods(captureH);
                }
            }

            if(output) {
                ClassType superC = ci.c.getSuperclass();
                Type[] types = emitters.emitAll(captureH, mi, (CodeAttr)null, Main.unknownType);
                MFilter filter = new MFilter("<init>", types, superC);
                filter.searchDeclared();
                Method superCons = filter.getMethod();
                tok.captured = new AVar[captureH.captured.size()];
                captureH.captured.keySet().toArray(tok.captured);
                Field[] fields = new Field[captureH.captured.size()];
                captureH.captured.values().toArray(fields);
                int n = superCons.getGenericParameterTypes().length;
                Type[] params2 = new Type[n + fields.length];
                System.arraycopy(superCons.getGenericParameterTypes(), 0, params2, 0, n);

                int i2;
                for(i2 = 0; i2 != fields.length; ++i2) {
                    params2[n + i2] = fields[i2].getType();
                }

                Method cons = ci.c.addMethod("<init>", params2, Type.voidType, 0);
                CodeAttr code1 = cons.startCode();
                code1.emitLoad(code1.getArg(0));

                for(i2 = 0; i2 != n; ++i2) {
                    code1.emitLoad(code1.getArg(i2 + 1));
                }

                code1.emitInvoke(superCons);

                for(i2 = 0; i2 != fields.length; ++i2) {
                    code1.emitPushThis();
                    code1.emitLoad(code1.getArg(n + i2 + 1));
                    code1.emitPutField(fields[i2]);
                }

                code1.emitReturn();
            }
        }

        if(output) {
            code.emitNew(ci.c);
        }

        if(output) {
            code.emitDup();
        }

        if(output) {
            for(int i3 = 0; i3 != tok.captured.length; ++i3) {
                emitters.emitters.add(new LoadAVar(tok.captured[i3]));
            }

            Main.emitInvoke(this, "<init>", ci.c, emitters, mi, code, Main.unknownType);
        }

        return tok.t;
    }

    public Type compile(MacroCallToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(tok.ret == null) {
            String name = ((VToken)((Token)tok.toks.get(0))).val;
            byte o = 4;
            int l = tok.toks.size() - 1;
            Type[] var10000 = new Type[o + l];
            var10000[0] = Main.getCompilerType("AMethodInfo");
            var10000[1] = Type.getType("gnu.bytecode.Type");
            var10000[2] = Type.intType;
            var10000[3] = Main.getCompilerType("handlers.Handler");
            Type[] types = var10000;

            int j;
            for(j = 0; j != l; ++j) {
                types[o + j] = Main.getCompilerType("tokens.Token");
            }

            Method method = (Method)null;
            ClassInfo ci = (ClassInfo)null;

            for(int i = 0; method == null; ++i) {
                ci = (ClassInfo)((List)mi.ci.fs.macroNames.get(name)).get(i);
                MFilter filter = new MFilter(name, types, ci.c);
                filter.searchDeclared();
                method = filter.getMethod();
            }

            Type[] params = method.getGenericParameterTypes();
            Class[] classes = new Class[params.length];

            for(j = 0; j != params.length; ++j) {
                classes[j] = params[j].getReflectClass();
            }

            ArrayList args = new ArrayList(Arrays.asList(new Object[]{mi, needed, Integer.valueOf(mi.scopes.size()), this}));
            Object var10001;
            if(params.length > 0 && params[params.length - 1] instanceof ArrayType) {
                int var = params.length - o;
                ArrayList al = new ArrayList(tok.toks.subList(1, var));
                Token[] out = new Token[tok.toks.size() - var];
                tok.toks.subList(var, tok.toks.size()).toArray(out);
                al.add(out);
                var10001 = al;
            } else {
                var10001 = tok.toks.subList(1, tok.toks.size());
            }

            args.addAll((Collection)var10001);

            try {
                tok.ret = (Token)ci.rc.getMethod(name, classes).invoke((Object)null, args.toArray());
            } catch (Throwable var23) {
                throw new RuntimeException(var23);
            }
        }

        mi.pushLevel();
        Type out1 = this.compile(tok.ret, mi, code, needed);
        mi.popLevel();
        return out1;
    }

    public Type compile(BeginToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        mi.pushScope(code, tok.labels);
        this.compileAll(tok.toks, 1, tok.toks.size() - 1, mi, code, Type.voidType);
        Type type = this.compile((Token)tok.toks.get(tok.toks.size() - 1), mi, code, needed);
        mi.popScope(code);
        return type;
    }

    public Type compile(LabelToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Label label = mi.getLabel(((VToken)((Token)tok.toks.get(1))).val);
        if(output) {
            label.define(code);
        }

        return Type.voidType;
    }

    public Type compile(GotoToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Label label = mi.getLabel(((VToken)((Token)tok.toks.get(1))).val);
        if(output) {
            code.emitGoto(label);
        }

        return Type.voidType;
    }

    public Type compile(DefineToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Type type = mi.getType((Token)tok.toks.get(2));
        if(type == null) {
            type = this.compile((Token)tok.toks.get(2), mi, code, Main.unknownType);
        } else if(tok.toks.size() == 4) {
            this.compile((Token)tok.toks.get(3), mi, code, type);
        } else if(output) {
            code.emitPushDefaultValue(needed);
        }

        Variable var = mi.newVar(code, (VToken)((Token)tok.toks.get(1)), type);
        if(output) {
            code.emitStore(var);
        }

        return Type.voidType;
    }

    public Type compile(TryToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Type type = this.compile((Token)tok.toks.get(1), mi, (CodeAttr)null, needed);
        Token last = (Token)tok.toks.get(tok.toks.size() - 1);
        boolean hasFinally = ((VToken)((Token)last.toks.get(0))).val.equals("finally");
        if(output) {
            code.emitTryStart(hasFinally, type);
        }

        this.compile((Token)tok.toks.get(1), mi, code, needed);
        int e = tok.toks.size() - (hasFinally?1:0);

        for(int i = 2; i != e; ++i) {
            Token var11 = (Token)tok.toks.get(i);
            mi.pushScope(code, ((BlockToken)var11).labels);
            Variable var = mi.newVar(code, (VToken)((Token)var11.toks.get(0)), mi.getType((Token)var11.toks.get(1)));
            if(output) {
                code.emitCatchStart(var);
            }

            this.compileAll(var11.toks, 2, var11.toks.size(), mi, code, type);
            if(output) {
                code.emitCatchEnd();
            }

            mi.popScope(code);
        }

        if(hasFinally) {
            if(output) {
                code.emitFinallyStart();
            }

            this.compileAll(last.toks, 1, last.toks.size(), mi, code, type);
            if(output) {
                code.emitFinallyEnd();
            }
        }

        if(output) {
            code.emitTryCatchEnd();
        }

        return type;
    }

    public Type compile(InstanceToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        if(output) {
            code.emitInstanceof(mi.getType((Token)tok.toks.get(2)));
        }

        return Type.booleanType;
    }

    public Type compile(SetToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Token first = (Token)tok.toks.get(0);
        Token out = (Token)tok.toks.get(1);
        if(out instanceof ColonToken) {
            Token first1 = (Token)out.toks.get(0);
            Type t = mi.getType(first1);
            if(t == null) {
                t = this.compile(first1, mi, code, Main.unknownType);
            }

            ClassType var10 = (ClassType)t.getRawType();
            Field field = var10.getField(((VToken)((Token)out.toks.get(1))).val, -1);
            this.compile((Token)tok.toks.get(2), mi, code, Main.resolveType(t, field.getType()));
            if(field.getStaticFlag()) {
                if(output) {
                    code.emitPutStatic(field);
                }
            } else if(output) {
                code.emitPutField(field);
            }
        } else {
            AVar var = mi.getVar((VToken)((Token)tok.toks.get(1)));
            this.compile((Token)tok.toks.get(2), mi, code, var.type);
            var.store(code);
        }

        return Type.voidType;
    }

    public Type compile(ASetToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        ArrayType type = (ArrayType)this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        this.compile((Token)tok.toks.get(2), mi, code, Main.unknownType);
        this.compile((Token)tok.toks.get(3), mi, code, type.elements);
        if(output) {
            code.emitArrayStore();
        }

        return Type.voidType;
    }

    public Type compile(AGetToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        ArrayType type = (ArrayType)this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        this.compile((Token)((Token)tok.toks.get(2)), mi, code, Type.intType);
        if(output) {
            code.emitArrayLoad();
        }

        return type.elements;
    }

    public Type compile(ALenToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        if(output) {
            code.emitArrayLength();
        }

        return Type.intType;
    }

    public Type compile(AsToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Type cast = mi.getType((Token)tok.toks.get(1));
        return this.compile((Token)tok.toks.get(2), mi, code, cast);
    }

    public Type compile(BinOpToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        VToken first = (VToken)((Token)tok.toks.get(0));
        Type otype = Main.numericOpType(this.compileAll(tok.toks, 1, tok.toks.size(), mi, (CodeAttr)null, Main.unknownType));
        this.compile((Token)tok.toks.get(1), mi, code, otype);

        for(int i = 2; i != tok.toks.size(); ++i) {
            this.compile((Token)tok.toks.get(i), mi, code, otype);
            if(output) {
                code.emitBinop(((Integer)Main.binOps.get(first.val)).intValue(), otype);
            }
        }

        return otype;
    }

    public Type compile(IfToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        boolean hasElse = tok.toks.size() == 4;
        return Main.emitIf(this, false, tok, 1, (Token)tok.toks.get(2), (Emitter)(hasElse?(Token)tok.toks.get(3):(Emitter)null), mi, code, (Type)(hasElse?needed:Type.voidType));
    }

    public Type compile(WhileToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        mi.pushScope(code, tok.labels);
        Label start = new Label();
        if(output) {
            start.define(code);
        }

        Type t = Main.emitIf(this, false, tok, 1, new Emitters(new Emitter[]{new Emitters(tok.toks.subList(2, tok.toks.size())), new Goto(start)}), (Emitter)null, mi, code, needed);
        mi.popScope(code);
        return t;
    }

    public Type compile(CompareToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Token first = (Token)tok.toks.get(0);
        return Main.emitIf_(this, false, tok, 1, tok.toks.size(), ((VToken)first).val, new ConstToken(tok.line, "true"), new ConstToken(tok.line, "false"), mi, code, Type.booleanType);
    }

    public Type compile(ThrowToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        if(output) {
            code.emitThrow();
        }

        return Main.throwType;
    }

    public Type compile(ClassToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(output) {
            code.emitPushClass((ObjectType)mi.getType((Token)tok.toks.get(1)));
        }

        return Type.javalangClassType;
    }

    public Type compile(SynchronizedToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        mi.pushScope((CodeAttr)null, tok.labels);
        this.compileAll(tok.toks, 2, tok.toks.size() - 1, mi, (CodeAttr)null, Type.voidType);
        Type type = this.compile((Token)tok.toks.get(tok.toks.size() - 1), mi, (CodeAttr)null, needed);
        mi.popScope((CodeAttr)null);
        mi.pushScope(code, tok.labels);
        Variable obj = output?code.addLocal(Type.objectType):null;
        if(output) {
            code.emitDup();
        }

        if(output) {
            code.emitStore(obj);
        }

        if(output) {
            code.emitMonitorEnter();
        }

        if(output) {
            code.emitTryStart(true, type);
        }

        this.compileAll(tok.toks, 2, tok.toks.size() - 1, mi, code, Type.voidType);
        this.compile((Token)tok.toks.get(tok.toks.size() - 1), mi, code, needed);
        if(output) {
            code.emitFinallyStart();
        }

        if(output) {
            code.emitLoad(obj);
        }

        if(output) {
            code.emitMonitorExit();
        }

        if(output) {
            code.emitFinallyEnd();
        }

        if(output) {
            code.emitTryCatchEnd();
        }

        mi.popScope(code);
        return type;
    }

    public Type compile(TypeToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(output) {
            code.emitLoad(code.getArg(0));
        }

        if(output) {
            code.emitInvoke(Main.getCompilerType("AMethodInfo").getDeclaredMethod("pushLevel", 0));
        }

        if(output) {
            code.emitLoad(code.getArg(3));
        }

        this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        if(output) {
            code.emitLoad(code.getArg(0));
        }

        if(output) {
            code.emitInvoke(Main.getCompilerType("Main").getDeclaredMethod("transformBlock", 2));
        }

        if(output) {
            code.emitLoad(code.getArg(0));
        }

        if(output) {
            code.emitPushNull();
        }

        if(output) {
            code.emitGetStatic(Main.getCompilerType("Main").getField("unknownType"));
        }

        if(output) {
            code.emitInvoke(Main.getCompilerType("handlers.Handler").getDeclaredMethod("compile", new Type[]{Main.getCompilerType("tokens.Token"), Main.getCompilerType("AMethodInfo"), ClassType.make("gnu.bytecode.CodeAttr"), ClassType.make("gnu.bytecode.Type")}));
        }

        if(output) {
            code.emitLoad(code.getArg(0));
        }

        if(output) {
            code.emitInvoke(Main.getCompilerType("AMethodInfo").getDeclaredMethod("popLevel", 0));
        }

        return Type.getType("gnu.bytecode.Type");
    }

    public Type compile(ReturnToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(tok.toks.size() == 2) {
            this.compile((Token)tok.toks.get(1), mi, code, Main.unknownType);
        }

        if(output) {
            code.emitReturn();
        }

        return Main.returnType;
    }

    public Type compile(CallToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Token first = (Token)tok.toks.get(0);
        String name = ((VToken)((Token)first.toks.get(1))).val;
        boolean special = (Token)first.toks.get(0) instanceof VToken && ((VToken)((Token)first.toks.get(0))).val.equals("super");
        Type var10000;
        if(special) {
            if(output) {
                code.emitPushThis();
            }

            var10000 = mi.ci.c.getGenericSuperclass();
        } else {
            var10000 = mi.getType((Token)first.toks.get(0));
        }

        Type t = var10000;
        if(t == null) {
            t = this.compile((Token)first.toks.get(0), mi, code, Main.unknownType);
        }

        return Main.emitInvoke(this, name, t, new Emitters(tok.toks.subList(1, tok.toks.size())), mi, code, needed, special);
    }

    public Type compile(DefaultToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        Token first = (Token)tok.toks.get(0);
        Type type = mi.getType(first);
        Type var10000;
        if(type == null) {
            Type t = this.compile(first, mi, code, Main.unknownType);
            ClassType c = (ClassType)t.getRawType();
            Method method = c.getDeclaredMethods();
            boolean i = false;

            Method tocall;
            for(tocall = (Method)null; method != null; method = method.getNext()) {
                if(!method.getName().equals("<init>") && 0 == (Access.SYNTHETIC & method.getModifiers())) {
                    if(tocall != null) {
                        throw new RuntimeException();
                    }

                    tocall = method;
                }
            }

            var10000 = Main.emitInvoke(this, tocall.getName(), t, new Emitters(tok.toks.subList(1, tok.toks.size())), mi, code, Main.unknownType);
        } else if(type instanceof ArrayType) {
            ArrayType array = (ArrayType)type;
            Token len = tok.toks.size() > 1?(Token)tok.toks.get(1):(Token)null;
            byte var18;
            if(len != null && len instanceof ColonToken && ((VToken)((Token)len.toks.get(0))).val.equals("len")) {
                this.compile((Token)len.toks.get(1), mi, code, Main.unknownType);
                var18 = 2;
            } else {
                if(output) {
                    code.emitPushInt(tok.toks.size() - 1);
                }

                var18 = 1;
            }

            byte i0 = var18;
            if(output) {
                code.emitNewArray(array.elements.getRawType());
            }

            for(int i1 = i0; i1 != tok.toks.size(); ++i1) {
                if(output) {
                    code.emitDup();
                }

                if(output) {
                    code.emitPushInt(i1 - i0);
                }

                this.compile((Token)tok.toks.get(i1), mi, code, array.elements);
                if(output) {
                    code.emitArrayStore();
                }
            }

            var10000 = type;
        } else {
            ClassType var17 = (ClassType)type.getRawType();
            if(output) {
                code.emitNew(var17);
            }

            if(output) {
                code.emitDup();
            }

            Main.emitInvoke(this, "<init>", type, new Emitters(tok.toks.subList(1, tok.toks.size())), mi, code, Main.unknownType);
            var10000 = type;
        }

        return var10000;
    }

    public Type compile(Token tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(output) {
            code.putLineNumber(mi.ci.fs.name.substring(mi.ci.fs.name.lastIndexOf("/") + 1), tok.line);
        }

        try {
            Type type = super.compile(tok, mi, code, needed);
            Type var6 = this.castMaybe(code, type, needed);
            return var6;
        } catch (Throwable var9) {
            System.out.println(mi.ci.fs.name.concat(": Error compiling line ".concat(Integer.toString(tok.line))));
            Object var10000 = null;
            throw var9;
        }
    }
}
