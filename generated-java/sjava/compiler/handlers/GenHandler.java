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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.AVar;
import sjava.compiler.ClassInfo;
import sjava.compiler.MacroInfo;
import sjava.compiler.Main;
import sjava.compiler.MethodInfo;
import sjava.compiler.MethodMacroInfo;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.emitters.Emitters;
import sjava.compiler.emitters.Goto;
import sjava.compiler.emitters.LoadAVar;
import sjava.compiler.emitters.Nothing;
import sjava.compiler.handlers.CaptureVHandler;
import sjava.compiler.handlers.Handler;
import sjava.compiler.mfilters.MFilter;
import sjava.compiler.tokens.AGetToken;
import sjava.compiler.tokens.ALenToken;
import sjava.compiler.tokens.ASetToken;
import sjava.compiler.tokens.ArrayConstructorToken;
import sjava.compiler.tokens.AsToken;
import sjava.compiler.tokens.BeginToken;
import sjava.compiler.tokens.BlockToken2;
import sjava.compiler.tokens.CToken;
import sjava.compiler.tokens.CallToken;
import sjava.compiler.tokens.ClassToken;
import sjava.compiler.tokens.CompareToken;
import sjava.compiler.tokens.ConstToken;
import sjava.compiler.tokens.ConstructorToken;
import sjava.compiler.tokens.DefaultToken;
import sjava.compiler.tokens.DefineToken;
import sjava.compiler.tokens.EmptyToken;
import sjava.compiler.tokens.FieldToken;
import sjava.compiler.tokens.GotoToken;
import sjava.compiler.tokens.IfToken;
import sjava.compiler.tokens.IncludeToken;
import sjava.compiler.tokens.InstanceToken;
import sjava.compiler.tokens.LabelToken;
import sjava.compiler.tokens.LambdaFnToken;
import sjava.compiler.tokens.LambdaToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.MacroCallToken;
import sjava.compiler.tokens.NToken;
import sjava.compiler.tokens.NumOpToken;
import sjava.compiler.tokens.ObjectToken;
import sjava.compiler.tokens.QuoteToken2;
import sjava.compiler.tokens.ReturnToken;
import sjava.compiler.tokens.SToken;
import sjava.compiler.tokens.SetToken;
import sjava.compiler.tokens.ShiftToken;
import sjava.compiler.tokens.SynchronizedToken;
import sjava.compiler.tokens.ThrowToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.TryToken;
import sjava.compiler.tokens.TypeToken;
import sjava.compiler.tokens.UnquoteToken;
import sjava.compiler.tokens.VToken;
import sjava.compiler.tokens.WhileToken;
import sjava.std.Tuple3;

public class GenHandler extends Handler {
    public static GenHandler inst = new GenHandler();
    public CodeAttr code;

    public Type[] compileAll(List<Token> toks, int i, int e, AMethodInfo mi, CodeAttr code, Object needed) {
        CodeAttr ocode = this.code;
        this.code = code;
        Type[] out = this.compileAll(toks, i, e, mi, needed);
        this.code = ocode;
        return out;
    }

    public Type compile(Token tok, AMethodInfo mi, CodeAttr code, Type needed) {
        CodeAttr ocode = this.code;
        this.code = code;
        Type out = this.compile(tok, mi, needed);
        this.code = ocode;
        return out;
    }

    public Type castMaybe(Type result, Type needed) {
        return castMaybe(this.code, result, needed);
    }

    public static Type castMaybe(CodeAttr code, Type result, Type needed) {
        boolean output = code != null;
        Type var10000;
        if(needed != Main.unknownType && result != Main.returnType && result != Main.throwType) {
            if(needed == Type.voidType) {
                if(result != Type.voidType && output) {
                    code.emitPop(1);
                }
            } else if(!Type.isSame(needed, result)) {
                if(result instanceof PrimType) {
                    if(needed instanceof ClassType) {
                        if(result == Type.voidType) {
                            if(output) {
                                code.emitPushNull();
                            }
                        } else {
                            PrimType prim = PrimType.unboxedType(needed);
                            ClassType var7;
                            if(prim == null) {
                                prim = (PrimType)result;
                                var7 = ((PrimType)result).boxedType();
                            } else {
                                if(output) {
                                    code.emitConvert((PrimType)result, prim);
                                }

                                var7 = (ClassType)needed;
                            }

                            ClassType box = var7;
                            if(output) {
                                code.emitInvoke(box.getMethod("valueOf", new Type[]{prim}));
                            }
                        }
                    } else if(output) {
                        code.emitConvert((PrimType)result, (PrimType)needed);
                    }
                } else if(needed instanceof PrimType) {
                    Method unbox = (Method)Main.unboxMethods.get(result);
                    if(output) {
                        code.emitInvoke(unbox);
                    }

                    if(output) {
                        code.emitConvert((PrimType)unbox.getReturnType(), (PrimType)needed);
                    }
                } else if(output) {
                    code.emitCheckcast(needed.getRawType());
                }
            }

            var10000 = needed;
        } else {
            var10000 = result;
        }

        return var10000;
    }

    public Type compile(EmptyToken tok, AMethodInfo mi, Type needed) {
        return Type.voidType;
    }

    public Type compile(SToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(output) {
            this.code.emitPushString(tok.val);
        }

        return Type.javalangStringType;
    }

    public Type compile(CToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        char c = tok.val.charValue();
        Object var10000;
        if(!(needed instanceof PrimType)) {
            if(output) {
                this.code.emitPushInt(c);
            }

            var10000 = Type.charType;
        } else {
            if(output) {
                this.code.emitPushConstant(c, needed);
            }

            var10000 = needed;
        }

        return (Type)var10000;
    }

    public Type compile(NToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Number val = tok.val;
        Object var10000;
        if(val instanceof Double) {
            Double val1 = (Double)val;
            if(output) {
                this.code.emitPushDouble(val1.doubleValue());
            }

            var10000 = Type.doubleType;
        } else if(val instanceof Float) {
            Float val2 = (Float)val;
            if(output) {
                this.code.emitPushFloat(val2.floatValue());
            }

            var10000 = Type.floatType;
        } else {
            if(!(val instanceof Integer)) {
                throw new RuntimeException();
            }

            Integer val3 = (Integer)val;
            if(!(needed instanceof PrimType)) {
                if(output) {
                    this.code.emitPushInt(val3.intValue());
                }

                var10000 = Type.intType;
            } else {
                if(output) {
                    this.code.emitPushConstant(val3.intValue(), needed);
                }

                var10000 = needed;
            }
        }

        return (Type)var10000;
    }

    public Type compile(FieldToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Type t = mi.getType(tok.left);
        if(t == null) {
            t = this.compile(tok.left, mi, this.code, Main.unknownType);
        }

        ClassType var6 = (ClassType)t.getRawType();
        Field field = var6.getField(tok.right, -1);
        if(field.getStaticFlag()) {
            if(output) {
                this.code.emitGetStatic(field);
            }
        } else if(output) {
            this.code.emitGetField(field);
        }

        Type out = Main.resolveType(t, field.getType());
        if(out != Type.voidType && output) {
            this.code.emitCheckcast(out.getRawType());
        }

        return out;
    }

    Type compileQuasi(Object o, AMethodInfo mi, CodeAttr code, Type needed) {
        CodeAttr ocode = this.code;
        this.code = code;
        Type out = this.compileQuasi(o, mi, needed);
        this.code = ocode;
        return out;
    }

    Type compileQuasi(Object o, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(o == null) {
            if(output) {
                this.code.emitPushNull();
            }

            return needed;
        } else {
            Object var10000;
            if(o instanceof UnquoteToken) {
                UnquoteToken o1 = (UnquoteToken)o;
                Token tok = Main.transformBlock((LexedParsedToken)o1.toks.get(0), mi);
                Type t = this.compile(tok, mi, (CodeAttr)null, Main.unknownType);
                if(t == Type.getType("gnu.bytecode.Type")) {
                    this.compile(tok, mi, this.code, Main.unknownType);
                    if(output) {
                        this.code.emitInvoke(Main.getCompilerType("Main").getDeclaredMethod("typeToTok", 1));
                    }

                    var10000 = Main.getCompilerType("tokens.LexedParsedToken");
                } else if(t != Main.getCompilerType("tokens.LexedParsedToken") && !(t instanceof ArrayType) && !t.getRawType().isSubtype(Type.getType("java.util.List"))) {
                    Type t1 = this.compile(tok, mi, (CodeAttr)null, Main.unknownType);
                    ClassType type = Main.getCompilerType(o1.var?"tokens.VToken":(t1 == Type.charType?"tokens.CToken":(t1 instanceof PrimType?"tokens.NToken":"tokens.SToken")));
                    if(output) {
                        this.code.emitNew(type);
                    }

                    if(output) {
                        this.code.emitDup();
                    }

                    if(output) {
                        this.code.emitInvoke(type.getDeclaredMethod("<init>", 0));
                    }

                    if(output) {
                        this.code.emitDup();
                    }

                    this.compile(tok, mi, this.code, Type.objectType);
                    if(output) {
                        this.code.emitPutField(type.getField("val"));
                    }

                    var10000 = Main.getCompilerType("tokens.LexedParsedToken");
                } else {
                    var10000 = this.compile(tok, mi, this.code, Main.unknownType);
                }
            } else if(o instanceof LexedParsedToken) {
                LexedParsedToken o2 = (LexedParsedToken)o;
                ClassType type1 = (ClassType)Type.getType(o2.getClass().getName());
                if(output) {
                    this.code.emitNew(type1);
                }

                if(output) {
                    this.code.emitDup();
                }

                if(output) {
                    this.code.emitInvoke(type1.getDeclaredMethod("<init>", 0));
                }

                for(ClassType superC = type1; superC != null; superC = superC.getSuperclass()) {
                    for(Field field = superC.getFields(); field != null; field = field.getNext()) {
                        if((field.getModifiers() & Access.PUBLIC) != 0 && (field.getModifiers() & Access.TRANSIENT) == 0 && (field.getModifiers() & Access.STATIC) == 0) {
                            if(output) {
                                this.code.emitDup();
                            }

                            try {
                                this.compileQuasi(field.getReflectField().get(o2), mi, this.code, field.getType());
                            } catch (NoSuchFieldException var27) {
                                throw new RuntimeException(var27);
                            } catch (IllegalAccessException var28) {
                                throw new RuntimeException(var28);
                            }

                            if(output) {
                                this.code.emitPutField(field);
                            }
                        }
                    }
                }

                if(type1 == Main.getCompilerType("tokens.VToken")) {
                    if(output) {
                        this.code.emitDup();
                    }

                    if(mi.ci instanceof MethodMacroInfo) {
                        if(output) {
                            this.code.emitPushInt(0);
                        }
                    } else if(mi.ci instanceof MacroInfo && output) {
                        this.code.emitLoad(this.code.getArg(2));
                    }

                    if(output) {
                        this.code.emitPutField(type1.getField("macro"));
                    }
                }

                var10000 = Main.getCompilerType("tokens.LexedParsedToken");
            } else if(o instanceof String) {
                String o3 = (String)o;
                if(output) {
                    this.code.emitPushString(o3);
                }

                var10000 = Type.javalangStringType;
            } else if(o instanceof Integer) {
                Integer o4 = (Integer)o;
                if(output) {
                    this.code.emitPushInt(o4.intValue());
                }

                var10000 = Type.intType;
            } else if(o instanceof Long) {
                Long o5 = (Long)o;
                if(output) {
                    this.code.emitPushLong(o5.longValue());
                }

                var10000 = Type.longType;
            } else if(o instanceof Double) {
                Double o6 = (Double)o;
                if(output) {
                    this.code.emitPushDouble(o6.doubleValue());
                }

                var10000 = Type.doubleType;
            } else if(o instanceof Float) {
                Float o7 = (Float)o;
                if(output) {
                    this.code.emitPushFloat(o7.floatValue());
                }

                var10000 = Type.floatType;
            } else if(o instanceof Boolean) {
                Boolean o8 = (Boolean)o;
                if(output) {
                    this.code.emitPushInt(o8.booleanValue()?1:0);
                }

                var10000 = Type.booleanType;
            } else {
                if(!(o instanceof List)) {
                    throw new RuntimeException(o.getClass().toString());
                }

                List o9 = (List)o;
                ClassType al = (ClassType)Type.getType("java.util.ArrayList");
                if(output) {
                    this.code.emitNew(al);
                }

                if(output) {
                    this.code.emitDup();
                }

                if(output) {
                    this.code.emitInvoke(al.getMethod("<init>", new Type[0]));
                }

                for(int i = 0; i != o9.size(); ++i) {
                    Type t2 = this.compileQuasi(o9.get(i), mi, (CodeAttr)null, Main.unknownType);
                    if(t2 == Main.getCompilerType("tokens.LexedParsedToken")) {
                        if(output) {
                            this.code.emitDup();
                        }

                        this.compileQuasi(o9.get(i), mi, this.code, Main.unknownType);
                        if(output) {
                            this.code.emitInvoke(al.getMethod("add", new Type[]{Type.objectType}));
                        }

                        if(output) {
                            this.code.emitPop(1);
                        }
                    } else if(t2 instanceof ArrayType) {
                        if(output) {
                            this.code.emitDup();
                        }

                        this.compileQuasi(o9.get(i), mi, this.code, Main.unknownType);
                        if(output) {
                            this.code.emitInvoke(((ClassType)Type.getType("java.util.Collections")).getDeclaredMethod("addAll", 2));
                        }

                        if(output) {
                            this.code.emitPop(1);
                        }
                    } else {
                        if(output) {
                            this.code.emitDup();
                        }

                        this.compileQuasi(o9.get(i), mi, this.code, Main.unknownType);
                        if(output) {
                            this.code.emitInvoke(((ClassType)Type.getType("java.util.List")).getDeclaredMethod("addAll", 1));
                        }

                        if(output) {
                            this.code.emitPop(1);
                        }
                    }
                }

                var10000 = al;
            }

            Object result = var10000;
            return this.castMaybe((Type)result, needed);
        }
    }

    public Type compile(QuoteToken2 tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        return this.compileQuasi(tok.tok, mi, this.code, Main.getCompilerType("tokens.LexedParsedToken"));
    }

    public Type compile(ConstToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Object var10000;
        if(tok.val.equals("true")) {
            if(output) {
                this.code.emitPushInt(1);
            }

            var10000 = Type.booleanType;
        } else if(tok.val.equals("false")) {
            if(output) {
                this.code.emitPushInt(0);
            }

            var10000 = Type.booleanType;
        } else {
            if(output) {
                this.code.emitPushNull();
            }

            var10000 = Type.nullType;
        }

        return (Type)var10000;
    }

    public Type compile(VToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        AVar found = mi.getVar(tok);
        return found.load(this.code);
    }

    public Type compile(IncludeToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(tok.ret == null) {
            mi.ci.fs.compileInclude(tok);

            try {
                tok.ret = Main.transformBlock((LexedParsedToken)mi.ci.fs.includes.getClazz().getMethod(tok.mi.method.getName(), new Class[]{AMethodInfo.class, Type.class, Integer.TYPE, GenHandler.class}).invoke((Object)null, new Object[]{mi, needed, Integer.valueOf(0), this}), mi);
            } catch (NoSuchMethodException var8) {
                throw new RuntimeException(var8);
            } catch (IllegalAccessException var9) {
                throw new RuntimeException(var9);
            } catch (InvocationTargetException var10) {
                throw new RuntimeException(var10);
            }
        }

        return this.compile(tok.ret, mi, this.code, needed);
    }

    void createCtor(ClassType c, Type[] types, Collection<Field> fields) {
        ClassType superC = c.getSuperclass();
        MFilter filter = new MFilter("<init>", types, superC);
        filter.searchDeclared();
        Method superCons = filter.getMethod();
        int n = superCons.getGenericParameterTypes().length;
        Type[] params = new Type[n + fields.size()];
        System.arraycopy(superCons.getGenericParameterTypes(), 0, params, 0, n);
        Iterator it = fields.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            Field field = (Field)it.next();
            params[n + i] = field.getType();
        }

        Method cons = c.addMethod("<init>", params, Type.voidType, 0);
        CodeAttr ncode = cons.startCode();
        ncode.emitLoad(ncode.getArg(0));

        for(int i1 = 0; i1 != n; ++i1) {
            ncode.emitLoad(ncode.getArg(i1 + 1));
        }

        ncode.emitInvoke(superCons);
        Iterator it1 = fields.iterator();

        for(int i2 = 0; it1.hasNext(); ++i2) {
            Field field1 = (Field)it1.next();
            ncode.emitPushThis();
            ncode.emitLoad(ncode.getArg(n + i2 + 1));
            ncode.emitPutField(field1);
        }

        ncode.emitReturn();
    }

    public Type compile(ObjectToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        ClassInfo ci = tok.ci;
        ArrayList emitters = new ArrayList(tok.superArgs);
        if(ci == null) {
            CaptureVHandler captureH = new CaptureVHandler(mi);
            if(output) {
                ci = new ClassInfo(mi.ci.c.getName().concat("$".concat(Integer.toString(mi.ci.anonClasses.size()))), mi.ci.fs);
                tok.ci = ci;
                mi.ci.anonClasses.add(ci);
            }

            if(tok instanceof LambdaFnToken) {
                LambdaFnToken tok1 = (LambdaFnToken)tok;
                LinkedHashMap scope = tok1.scope;
                List params = tok1.params;
                ArrayList generics = new ArrayList(params);
                ArrayList toks = new ArrayList(tok1.toks);
                if(tok1.t == null) {
                    MethodInfo fakemi = new MethodInfo(new ClassInfo((ClassType)null, mi.ci.fs), (List)null, (Method)null, scope);
                    fakemi.ensureLevels(mi.scopes.size() - 1);
                    BlockToken2 beginTok = Main.transformBlockToks(new BeginToken(0, toks), mi);
                    tok1.ret = Main.tryBox(captureH.compile(beginTok, fakemi, (CodeAttr)null, Main.unknownType));
                    generics.add(tok1.ret);
                    Type[] agenerics = new Type[generics.size()];
                    generics.toArray(agenerics);
                    StringBuilder sb = new StringBuilder();
                    sb.append("sjava.std.Function");
                    sb.append(params.size());
                    tok1.t = new ParameterizedType(ClassType.make(sb.toString()), agenerics);
                }

                if(output) {
                    ci.c.addInterface(tok1.t);
                    ci.addMethod("apply", params, tok1.ret, Access.PUBLIC, tok1.toks, scope, false);
                }
            } else if(tok instanceof LambdaToken) {
                LambdaToken tok2 = (LambdaToken)tok;
                if(output) {
                    ci.c.addInterface(tok2.t);
                    ci.addMethod(tok2.sam.getName(), tok2.params, Main.resolveType(tok2.t, tok2.sam.getReturnType()), Access.PUBLIC, tok2.toks, tok2.scope, false);
                }
            } else if(output) {
                if(((ClassType)tok.t.getRawType()).isInterface()) {
                    ci.c.addInterface(tok.t);
                } else {
                    ci.c.setSuper(tok.t);
                }

                List iterable = tok.toks;
                Iterator it = iterable.iterator();

                for(int notused = 0; it.hasNext(); ++notused) {
                    LexedParsedToken tok3 = (LexedParsedToken)it.next();
                    ci.compileDef(tok3);
                }
            }

            if(output) {
                List iterable1 = ci.methods;
                Iterator it1 = iterable1.iterator();

                for(int notused1 = 0; it1.hasNext(); ++notused1) {
                    AMethodInfo omi = (AMethodInfo)it1.next();
                    omi.ensureLevels(mi.scopes.size() - 1);
                }

                ci.compileMethods(captureH);
                tok.captured = captureH.captured.keySet();
                this.createCtor(ci.c, Emitter.emitAll(emitters, captureH, mi, (CodeAttr)null, Main.unknownType), captureH.captured.values());
            }
        }

        if(output) {
            this.code.emitNew(ci.c);
            this.code.emitDup();
            Collection iterable2 = tok.captured;
            Iterator it2 = iterable2.iterator();

            for(int notused2 = 0; it2.hasNext(); ++notused2) {
                AVar v = (AVar)it2.next();
                emitters.add(new LoadAVar(v));
            }

            Main.emitInvoke(this, "<init>", ci.c, emitters, mi, this.code, Main.unknownType);
        }

        return tok.t;
    }

    public Type compile(MacroCallToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(tok.ret == null) {
            byte o = 4;
            int l = tok.toks.size();
            Type[] var10000 = new Type[o + l];
            var10000[0] = Main.getCompilerType("AMethodInfo");
            var10000[1] = Type.getType("gnu.bytecode.Type");
            var10000[2] = Type.intType;
            var10000[3] = Main.getCompilerType("handlers.GenHandler");
            Type[] types = var10000;

            for(int j = 0; j != l; ++j) {
                types[o + j] = Main.getCompilerType("tokens.LexedParsedToken");
            }

            Method method = (Method)null;
            ClassInfo ci = (ClassInfo)null;

            for(int i = 0; method == null; ++i) {
                ci = (ClassInfo)((List)mi.ci.fs.macroNames.get(tok.name)).get(i);
                MFilter filter = new MFilter(tok.name, types, ci.c);
                filter.searchDeclared();
                method = filter.getMethod();
            }

            ci.compileMethods(inst);
            Type[] params = method.getGenericParameterTypes();
            Class[] out = new Class[params.length];
            Type[] array = params;

            for(int i1 = 0; i1 != array.length; ++i1) {
                Type t = array[i1];
                out[i1] = t.getReflectClass();
            }

            Class[] classes = out;
            ArrayList args = new ArrayList(Arrays.asList(new Object[]{mi, needed, Integer.valueOf(mi.scopes.size()), this}));
            Object var10001;
            if((method.getModifiers() & Access.TRANSIENT) != 0) {
                int var = params.length - o - 1;
                ArrayList al = new ArrayList(tok.toks.subList(0, var));
                LexedParsedToken[] out1 = new LexedParsedToken[tok.toks.size() - var];
                tok.toks.subList(var, tok.toks.size()).toArray(out1);
                al.add(out1);
                var10001 = al;
            } else {
                var10001 = tok.toks;
            }

            args.addAll((Collection)var10001);

            try {
                LexedParsedToken ret = (LexedParsedToken)ci.getClazz().getMethod(tok.name, classes).invoke((Object)null, args.toArray());
                tok.ret = Main.transformBlock(ret, mi);
            } catch (NoSuchMethodException var29) {
                throw new RuntimeException(var29);
            } catch (IllegalAccessException var30) {
                throw new RuntimeException(var30);
            } catch (InvocationTargetException var31) {
                throw new RuntimeException(var31);
            }
        }

        mi.pushLevel();
        Type out2 = this.compile(tok.ret, mi, this.code, needed);
        mi.popLevel();
        return out2;
    }

    public Type compile(BeginToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        mi.pushScope(this.code, tok.labels);
        this.compileAll(tok.toks, 0, tok.toks.size() - 1, mi, this.code, Type.voidType);
        Type type = this.compile((Token)tok.toks.get(tok.toks.size() - 1), mi, this.code, needed);
        mi.popScope(this.code);
        return type;
    }

    public Type compile(LabelToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Label label = mi.getLabel(tok.label);
        if(output) {
            label.define(this.code);
        }

        return Type.voidType;
    }

    public Type compile(GotoToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(output) {
            this.code.emitGoto(mi.getLabel(tok.label));
        }

        return Type.voidType;
    }

    public Type compile(DefineToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Type var10000;
        if(tok.tok == null) {
            if(output) {
                this.code.emitPushDefaultValue(tok.type);
            }

            var10000 = tok.type;
        } else {
            var10000 = this.compile(tok.tok, mi, this.code, tok.type);
        }

        Type type = var10000;
        Variable var = mi.newVar(this.code, tok.name, type);
        if(output) {
            this.code.emitStore(var);
        }

        return Type.voidType;
    }

    public Type compile(TryToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Type type = this.compile(tok.tok, mi, (CodeAttr)null, needed);
        boolean hasFinally = tok.finallyToks != null;
        if(output) {
            this.code.emitTryStart(hasFinally, type);
        }

        this.compile(tok.tok, mi, this.code, needed);
        List iterable = tok.catches;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Tuple3 var10 = (Tuple3)it.next();
            mi.pushScope(this.code, Collections.emptyMap());
            Variable var = mi.newVar(this.code, (VToken)var10._1, (Type)var10._2);
            if(output) {
                this.code.emitCatchStart(var);
            }

            this.compileAll((List)var10._3, 0, ((List)var10._3).size(), mi, this.code, type);
            if(output) {
                this.code.emitCatchEnd();
            }

            mi.popScope(this.code);
        }

        if(hasFinally) {
            if(output) {
                this.code.emitFinallyStart();
            }

            this.compileAll(tok.finallyToks, 0, tok.finallyToks.size(), mi, this.code, type);
            if(output) {
                this.code.emitFinallyEnd();
            }
        }

        if(output) {
            this.code.emitTryCatchEnd();
        }

        return type;
    }

    public Type compile(InstanceToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        this.compile(tok.tok, mi, this.code, Main.unknownType);
        if(output) {
            this.code.emitInstanceof(tok.type);
        }

        return Type.booleanType;
    }

    public Type compile(SetToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Token first = (Token)tok.toks.get(0);
        Token out = (Token)tok.toks.get(1);
        if(out instanceof FieldToken) {
            FieldToken out1 = (FieldToken)out;
            Type t = mi.getType(out1.left);
            if(t == null) {
                t = this.compile(out1.left, mi, this.code, Main.unknownType);
            }

            ClassType var9 = (ClassType)t.getRawType();
            Field field = var9.getField(out1.right, -1);
            this.compile((Token)tok.toks.get(2), mi, this.code, Main.resolveType(t, field.getType()));
            if(field.getStaticFlag()) {
                if(output) {
                    this.code.emitPutStatic(field);
                }
            } else if(output) {
                this.code.emitPutField(field);
            }
        } else {
            AVar var = mi.getVar((VToken)((Token)tok.toks.get(1)));
            this.compile((Token)tok.toks.get(2), mi, this.code, var.type);
            var.store(this.code);
        }

        return Type.voidType;
    }

    public Type compile(ASetToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        ArrayType type = (ArrayType)this.compile((Token)tok.toks.get(1), mi, this.code, Main.unknownType);
        this.compile((Token)tok.toks.get(2), mi, this.code, Main.unknownType);
        this.compile((Token)tok.toks.get(3), mi, this.code, type.elements);
        if(output) {
            this.code.emitArrayStore();
        }

        return Type.voidType;
    }

    public Type compile(AGetToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        ArrayType type = (ArrayType)this.compile((Token)tok.toks.get(1), mi, this.code, Main.unknownType);

        for(int i = 2; i < tok.toks.size(); ++i) {
            this.compile((Token)tok.toks.get(i), mi, this.code, Type.intType);
            if(output) {
                this.code.emitArrayLoad();
            }

            if(i != 2) {
                type = (ArrayType)type.elements;
            }
        }

        return type.elements;
    }

    public Type compile(ALenToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        this.compile(tok.tok, mi, this.code, Main.unknownType);
        if(output) {
            this.code.emitArrayLength();
        }

        return Type.intType;
    }

    public Type compile(AsToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        return this.compile(tok.tok, mi, this.code, tok.type);
    }

    public Type compile(NumOpToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Type otype = Main.numericOpType(this.compileAll(tok.toks, 0, tok.toks.size(), mi, (CodeAttr)null, Main.unknownType));
        this.compile((Token)tok.toks.get(0), mi, this.code, otype);
        if(tok.op.equals("-") && tok.toks.size() == 1) {
            if(output) {
                this.code.emitPrimop(otype == Type.longType?117:(otype == Type.floatType?118:(otype == Type.doubleType?119:116)), 1, otype);
            }
        } else {
            for(int i = 1; i != tok.toks.size(); ++i) {
                this.compile((Token)tok.toks.get(i), mi, this.code, otype);
                if(output) {
                    this.code.emitBinop(((Integer)Main.binOps.get(tok.op)).intValue(), otype);
                }
            }
        }

        return otype;
    }

    public Type compile(ShiftToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        PrimType otype = PrimType.unboxedType(this.compile(tok.tok, mi, (CodeAttr)null, Main.unknownType));
        this.compile(tok.tok, mi, this.code, otype);
        this.compile(tok.amt, mi, this.code, Type.intType);
        if(output) {
            if(tok.right) {
                this.code.emitShr();
            } else {
                this.code.emitShl();
            }
        }

        return otype;
    }

    public Type compile(IfToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        boolean hasElse = tok.toks.size() == 4;
        return Main.emitIf(this, false, tok.toks, 1, (Token)tok.toks.get(2), (Emitter)(hasElse?(Token)tok.toks.get(3):Nothing.inst), mi, this.code, (Type)(hasElse?needed:Type.voidType));
    }

    public Type compile(WhileToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        mi.pushScope(this.code, tok.labels);
        Label start = new Label();
        if(output) {
            start.define(this.code);
        }

        Type t = Main.emitIf(this, false, tok.toks, 1, new Emitters(new Emitter[]{new Emitters(tok.toks.subList(2, tok.toks.size())), new Goto(start)}), Nothing.inst, mi, this.code, needed);
        mi.popScope(this.code);
        return t;
    }

    public Type compile(CompareToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        return Main.emitIf_(this, false, tok.toks, 0, tok.toks.size(), tok.compare, new ConstToken(tok.line, "true"), new ConstToken(tok.line, "false"), mi, this.code, Type.booleanType);
    }

    public Type compile(ThrowToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        this.compile(tok.tok, mi, this.code, Main.unknownType);
        if(output) {
            this.code.emitThrow();
        }

        return Main.throwType;
    }

    public Type compile(ClassToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(output) {
            this.code.emitPushClass((ObjectType)tok.type.getRawType());
        }

        return Type.javalangClassType;
    }

    public Type compile(SynchronizedToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        this.compile((Token)tok.toks.get(1), mi, this.code, Main.unknownType);
        mi.pushScope((CodeAttr)null, tok.labels);
        this.compileAll(tok.toks, 2, tok.toks.size() - 1, mi, (CodeAttr)null, Type.voidType);
        Type type = this.compile((Token)tok.toks.get(tok.toks.size() - 1), mi, (CodeAttr)null, needed);
        mi.popScope((CodeAttr)null);
        mi.pushScope(this.code, tok.labels);
        Variable obj = output?this.code.addLocal(Type.objectType):null;
        if(output) {
            this.code.emitDup();
        }

        if(output) {
            this.code.emitStore(obj);
        }

        if(output) {
            this.code.emitMonitorEnter();
        }

        if(output) {
            this.code.emitTryStart(true, type);
        }

        this.compileAll(tok.toks, 2, tok.toks.size() - 1, mi, this.code, Type.voidType);
        this.compile((Token)tok.toks.get(tok.toks.size() - 1), mi, this.code, needed);
        if(output) {
            this.code.emitFinallyStart();
        }

        if(output) {
            this.code.emitLoad(obj);
        }

        if(output) {
            this.code.emitMonitorExit();
        }

        if(output) {
            this.code.emitFinallyEnd();
        }

        if(output) {
            this.code.emitTryCatchEnd();
        }

        mi.popScope(this.code);
        return type;
    }

    public Type compile(TypeToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(output) {
            this.code.emitLoad(this.code.getArg(0));
        }

        if(output) {
            this.code.emitInvoke(Main.getCompilerType("AMethodInfo").getDeclaredMethod("pushLevel", 0));
        }

        if(output) {
            this.code.emitLoad(this.code.getArg(3));
        }

        this.compile(tok.tok, mi, this.code, Main.unknownType);
        if(output) {
            this.code.emitLoad(this.code.getArg(0));
        }

        if(output) {
            this.code.emitInvoke(Main.getCompilerType("Main").getDeclaredMethod("transformBlock", 2));
        }

        if(output) {
            this.code.emitLoad(this.code.getArg(0));
        }

        if(output) {
            this.code.emitPushNull();
        }

        if(output) {
            this.code.emitGetStatic(Main.getCompilerType("Main").getField("unknownType"));
        }

        if(output) {
            this.code.emitInvoke(Main.getCompilerType("handlers.GenHandler").getDeclaredMethod("compile", new Type[]{Main.getCompilerType("tokens.Token"), Main.getCompilerType("AMethodInfo"), ClassType.make("gnu.bytecode.CodeAttr"), ClassType.make("gnu.bytecode.Type")}));
        }

        if(output) {
            this.code.emitLoad(this.code.getArg(0));
        }

        if(output) {
            this.code.emitInvoke(Main.getCompilerType("AMethodInfo").getDeclaredMethod("popLevel", 0));
        }

        return Type.getType("gnu.bytecode.Type");
    }

    public Type compile(ReturnToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(tok.tok != null) {
            this.compile(tok.tok, mi, this.code, Main.unknownType);
        }

        if(output) {
            this.code.emitReturn();
        }

        return Main.returnType;
    }

    public Type compile(CallToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        boolean special = tok.target instanceof VToken && ((VToken)tok.target).val.equals("super");
        Type var10000;
        if(special) {
            if(output) {
                this.code.emitPushThis();
            }

            var10000 = mi.ci.c.getGenericSuperclass();
        } else {
            var10000 = mi.getType(tok.target);
        }

        Type t = var10000;
        if(t == null) {
            t = this.compile(tok.target, mi, this.code, Main.unknownType);
        }

        return Main.emitInvoke(this, tok.method, t, Main.toEmitters(tok.toks), mi, this.code, needed, special);
    }

    public Type compile(DefaultToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        Token first = (Token)tok.toks.get(0);
        Type t = this.compile(first, mi, this.code, Main.unknownType);
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

        return Main.emitInvoke(this, tocall.getName(), t, Main.toEmitters(tok.toks.subList(1, tok.toks.size())), mi, this.code, Main.unknownType);
    }

    public Type compile(ConstructorToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        ClassType var5 = (ClassType)tok.type.getRawType();
        if(output) {
            this.code.emitNew(var5);
        }

        if(output) {
            this.code.emitDup();
        }

        Main.emitInvoke(this, "<init>", tok.type, Main.toEmitters(tok.toks), mi, this.code, Main.unknownType);
        return tok.type;
    }

    public Type compile(ArrayConstructorToken tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        ArrayType array = (ArrayType)tok.type;
        if(tok.len != null) {
            this.compile(tok.len, mi, this.code, Main.unknownType);
        } else if(output) {
            this.code.emitPushInt(tok.toks.size());
        }

        if(output) {
            this.code.emitNewArray(array.elements.getRawType());
        }

        for(int i = 0; i != tok.toks.size(); ++i) {
            if(output) {
                this.code.emitDup();
            }

            if(output) {
                this.code.emitPushInt(i);
            }

            this.compile((Token)tok.toks.get(i), mi, this.code, array.elements);
            if(output) {
                this.code.emitArrayStore();
            }
        }

        return tok.type;
    }

    public Type compile(Token tok, AMethodInfo mi, Type needed) {
        boolean output = this.code != null;
        if(output) {
            this.code.putLineNumber(mi.ci.fs.name.substring(mi.ci.fs.name.lastIndexOf("/") + 1), tok.line);
        }

        try {
            Type type = super.compile(tok, mi, needed);
            Type var5 = this.castMaybe(type, needed);
            return var5;
        } catch (Throwable var8) {
            System.out.println(mi.ci.fs.name.concat(": Error compiling line ".concat(Integer.toString(tok.line))));
            Object var10000 = null;
            throw var8;
        }
    }
}
