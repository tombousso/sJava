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
        } else {
            Type t;
            if(o instanceof Token) {
                Token o1 = (Token)o;
                Type al = o1 instanceof UnquoteToken?this.compile((Token)o1.toks.get(0), mi, (CodeAttr)null, Main.unknownType):null;
                if(o1 instanceof UnquoteToken && (al == Main.getCompilerType("tokens.Token") || al instanceof ArrayType && ((ArrayType)al).elements == Main.getCompilerType("tokens.Token") || al.getRawType().isSubtype(Type.getType("java.util.List")))) {
                    var10000 = this.compile((Token)o1.toks.get(0), mi, code, Main.unknownType);
                } else if(o1 instanceof UnquoteToken) {
                    Token i = (Token)o1.toks.get(0);
                    t = this.compile(i, mi, (CodeAttr)null, Main.unknownType);
                    ClassType field = Main.getCompilerType(((UnquoteToken)o1).s?"tokens.SToken":(t == Type.charType?"tokens.CToken":(t instanceof PrimType?"tokens.NToken":"tokens.VToken")));
                    if(output) {
                        code.emitNew(field);
                    }

                    if(output) {
                        code.emitDup();
                    }

                    if(output) {
                        code.emitInvoke(field.getDeclaredMethod("<init>", 0));
                    }

                    if(output) {
                        code.emitDup();
                    }

                    this.compile((Token)i, mi, code, Type.objectType);
                    if(output) {
                        code.emitPutField(field.getField("val"));
                    }

                    var10000 = Main.getCompilerType("tokens.Token");
                } else {
                    ClassType var16 = (ClassType)Type.getType(o.getClass().getName());
                    if(output) {
                        code.emitNew(var16);
                    }

                    if(output) {
                        code.emitDup();
                    }

                    if(output) {
                        code.emitInvoke(var16.getDeclaredMethod("<init>", 0));
                    }

                    for(ClassType var18 = var16; var18 != null; var18 = var18.getSuperclass()) {
                        for(Field var19 = var18.getFields(); var19 != null; var19 = var19.getNext()) {
                            if((var19.getModifiers() & Access.PUBLIC) != 0 && (var19.getModifiers() & Access.TRANSIENT) == 0) {
                                if(output) {
                                    code.emitDup();
                                }

                                try {
                                    if(var19.getType() == Type.intType) {
                                        this.compileQuasi(Integer.valueOf(var19.getReflectField().getInt(o1)), mi, code, var19.getType());
                                    } else {
                                        this.compileQuasi(var19.getReflectField().get(o1), mi, code, var19.getType());
                                    }
                                } catch (Throwable var12) {
                                    var12.printStackTrace();
                                }

                                if(output) {
                                    code.emitPutField(var19);
                                }
                            }
                        }
                    }

                    if(var16 == Main.getCompilerType("tokens.VToken")) {
                        if(output) {
                            code.emitDup();
                        }

                        if(output) {
                            code.emitLoad(code.getArg(2));
                        }

                        if(output) {
                            code.emitPutField(var16.getField("macro"));
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
            } else if(o instanceof List) {
                List var13 = (List)o;
                ClassType var15 = (ClassType)Type.getType("java.util.ArrayList");
                if(output) {
                    code.emitNew(var15);
                }

                if(output) {
                    code.emitDup();
                }

                if(output) {
                    code.emitInvoke(var15.getMethod("<init>", new Type[0]));
                }

                for(int var17 = 0; var17 != var13.size(); ++var17) {
                    t = this.compileQuasi(var13.get(var17), mi, (CodeAttr)null, Main.unknownType);
                    if(t == Main.getCompilerType("tokens.Token")) {
                        if(output) {
                            code.emitDup();
                        }

                        this.compileQuasi(var13.get(var17), mi, code, Main.unknownType);
                        if(output) {
                            code.emitInvoke(var15.getMethod("add", new Type[]{Type.objectType}));
                        }

                        if(output) {
                            code.emitPop(1);
                        }
                    } else if(t instanceof ArrayType) {
                        if(output) {
                            code.emitDup();
                        }

                        this.compileQuasi(var13.get(var17), mi, code, Main.unknownType);
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

                        this.compileQuasi(var13.get(var17), mi, code, Main.unknownType);
                        if(output) {
                            code.emitInvoke(((ClassType)Type.getType("java.util.List")).getDeclaredMethod("addAll", 1));
                        }

                        if(output) {
                            code.emitPop(1);
                        }
                    }
                }

                var10000 = var15;
            } else {
                var10000 = null;
            }
        }

        Object var14 = var10000;
        return this.castMaybe(code, (Type)var14, needed);
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
                var7.printStackTrace();
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
        Emitters emitters = new Emitters((List)(lambda || !(superT instanceof BlockToken)?new ArrayList():superT.toks.subList(1, superT.toks.size())));
        if(ci == null) {
            CaptureVHandler i = new CaptureVHandler(mi);
            if(output) {
                ci = new ClassInfo(mi.ci.c.getName().concat("$".concat(Integer.toString(mi.ci.anonymous))), mi.ci.fs);
                tok.ci = ci;
                mi.ci.fs.anonClasses.add(ci);
                ++mi.ci.anonymous;
            }

            Type[] var26;
            if(FunctionN) {
                LinkedHashMap var23 = new LinkedHashMap();
                var26 = Main.getParams(mi.ci, superT, var23, 0, 1);
                Type[] var27 = new Type[var26.length + 1];
                System.arraycopy(var26, 0, var27, 0, var26.length);
                List var29 = tok.toks.subList(2, tok.toks.size());
                MethodInfo var31 = new MethodInfo(new ClassInfo((ClassType)null, mi.ci.fs), var29, (Method)null, var23);
                Type var33 = Main.tryBox(i.compile((Token)var31.block, var31, (CodeAttr)null, Main.unknownType));
                var27[var26.length] = var33;
                ParameterizedType var36 = new ParameterizedType(ClassType.make("sjava.std.Function".concat(Integer.toString(var26.length))), var27);
                tok.t = var36;
                if(output) {
                    Method var38 = ci.c.addMethod("apply", var26, var33, Access.PUBLIC);
                    ci.c.addInterface(var36);
                    ci.methods.add(new MethodInfo(ci, var29, var38, var23));
                    ci.compileMethods(i);
                }
            } else {
                Type superC;
                if(lambda) {
                    superC = mi.getType(superT);
                    tok.t = superC;
                    if(output) {
                        Method var25 = ((ClassType)superC.getRawType()).checkSingleAbstractMethod();
                        LinkedHashMap filter = new LinkedHashMap();
                        Token superCons = (Token)tok.toks.get(2);
                        int fields = 0;
                        Type[] n = new Type[superCons.toks.size()];

                        while(true) {
                            if(fields == superCons.toks.size()) {
                                ci.c.addInterface(superC);
                                Method var35 = ci.c.addMethod(var25.getName(), n, Main.resolveType(superC, var25.getReturnType()), Access.PUBLIC);
                                ci.methods.add(new MethodInfo(ci, tok.toks.subList(3, tok.toks.size()), var35, filter));
                                ci.compileMethods(i);
                                break;
                            }

                            VToken params = (VToken)((Token)superCons.toks.get(fields));
                            Type i1 = Main.resolveType(superC, var25.getGenericParameterTypes()[fields]);
                            filter.put(params.val, new Arg(fields + 1, i1));
                            n[fields] = i1;
                            ++fields;
                        }
                    }
                } else {
                    superC = superT instanceof BlockToken?mi.getType((Token)superT.toks.get(0)):mi.getType(superT);
                    tok.t = superC;
                    if(output) {
                        if(((ClassType)superC.getRawType()).isInterface()) {
                            ci.c.addInterface(superC);
                        } else {
                            ci.c.setSuper(superC);
                        }

                        int types = 2;

                        while(true) {
                            if(types == tok.toks.size()) {
                                ci.compileMethods(i);
                                break;
                            }

                            ci.compileDef((Token)tok.toks.get(types));
                            ++types;
                        }
                    }
                }
            }

            if(output) {
                ClassType var24 = ci.c.getSuperclass();
                var26 = emitters.emitAll(i, mi, (CodeAttr)null, Main.unknownType);
                MFilter var28 = new MFilter("<init>", var26, var24);
                var28.searchDeclared();
                Method var30 = var28.getMethod();
                tok.captured = new AVar[i.captured.size()];
                i.captured.keySet().toArray(tok.captured);
                Field[] var32 = new Field[i.captured.size()];
                i.captured.values().toArray(var32);
                int var34 = var30.getGenericParameterTypes().length;
                Type[] var37 = new Type[var34 + var32.length];
                System.arraycopy(var30.getGenericParameterTypes(), 0, var37, 0, var34);

                int var39;
                for(var39 = 0; var39 != var32.length; ++var39) {
                    var37[var34 + var39] = var32[var39].getType();
                }

                Method cons = ci.c.addMethod("<init>", var37, Type.voidType, 0);
                CodeAttr code1 = cons.startCode();
                code1.emitLoad(code1.getArg(0));

                for(var39 = 0; var39 != var34; ++var39) {
                    code1.emitLoad(code1.getArg(var39 + 1));
                }

                code1.emitInvoke(var30);

                for(var39 = 0; var39 != var32.length; ++var39) {
                    code1.emitPushThis();
                    code1.emitLoad(code1.getArg(var34 + var39 + 1));
                    code1.emitPutField(var32[var39]);
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
            for(int var22 = 0; var22 != tok.captured.length; ++var22) {
                emitters.emitters.add(new LoadAVar(tok.captured[var22]));
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

            Type[] var22 = method.getGenericParameterTypes();
            Class[] classes = new Class[var22.length];

            for(j = 0; j != var22.length; ++j) {
                classes[j] = var22[j].getReflectClass();
            }

            ArrayList args = new ArrayList(Arrays.asList(new Object[]{mi, needed, Integer.valueOf(mi.scopes.size()), this}));
            Object var10001;
            if(var22.length > 0 && var22[var22.length - 1] instanceof ArrayType) {
                int var = var22.length - o;
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
            } catch (Throwable var20) {
                var20.printStackTrace();
            }
        }

        mi.pushLevel();
        Type var21 = this.compile(tok.ret, mi, code, needed);
        mi.popLevel();
        return var21;
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
        if(output) {
            code.emitTryStart(false, (Type)null);
        }

        Type type = this.compile((Token)tok.toks.get(1), mi, code, needed);
        Variable var = mi.newVar(code, (VToken)((Token)tok.toks.get(2)), Type.javalangThrowableType);
        if(output) {
            code.emitCatchStart(var);
        }

        this.compile((Token)tok.toks.get(3), mi, code, Main.unknownType);
        if(output) {
            code.emitCatchEnd();
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
            Token var = (Token)out.toks.get(0);
            Type t = mi.getType(var);
            if(t == null) {
                t = this.compile(var, mi, code, Main.unknownType);
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
            AVar var1 = mi.getVar((VToken)((Token)tok.toks.get(1)));
            this.compile((Token)tok.toks.get(2), mi, code, var1.type);
            var1.store(code);
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
            Type var8 = this.compile(first, mi, code, Main.unknownType);
            ClassType len = (ClassType)var8.getRawType();
            Method i0 = len.getDeclaredMethods();
            boolean i = false;

            Method tocall;
            for(tocall = (Method)null; i0 != null; i0 = i0.getNext()) {
                if(!i0.getName().equals("<init>") && 0 == (Access.SYNTHETIC & i0.getModifiers()) && tocall == null) {
                    tocall = i0;
                }
            }

            var10000 = Main.emitInvoke(this, tocall.getName(), var8, new Emitters(tok.toks.subList(1, tok.toks.size())), mi, code, Main.unknownType);
        } else if(type instanceof ArrayType) {
            ArrayType var13 = (ArrayType)type;
            Token var15 = tok.toks.size() > 1?(Token)tok.toks.get(1):(Token)null;
            byte var18;
            if(var15 != null && var15 instanceof ColonToken && ((VToken)((Token)var15.toks.get(0))).val.equals("len")) {
                this.compile((Token)var15.toks.get(1), mi, code, Main.unknownType);
                var18 = 2;
            } else {
                if(output) {
                    code.emitPushInt(tok.toks.size() - 1);
                }

                var18 = 1;
            }

            byte var16 = var18;
            if(output) {
                code.emitNewArray(var13.elements.getRawType());
            }

            for(int var17 = var16; var17 != tok.toks.size(); ++var17) {
                if(output) {
                    code.emitDup();
                }

                if(output) {
                    code.emitPushInt(var17 - var16);
                }

                this.compile((Token)tok.toks.get(var17), mi, code, var13.elements);
                if(output) {
                    code.emitArrayStore();
                }
            }

            var10000 = type;
        } else {
            ClassType var14 = (ClassType)type.getRawType();
            if(output) {
                code.emitNew(var14);
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
            return this.castMaybe(code, type, needed);
        } catch (Throwable var7) {
            System.out.println(mi.ci.fs.name.concat(": Error compiling line ".concat(Integer.toString(tok.line))));
            throw var7;
        }
    }
}
