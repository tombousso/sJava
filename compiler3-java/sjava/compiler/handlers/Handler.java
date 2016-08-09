package sjava.compiler.handlers;

import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Method;
import gnu.bytecode.PrimType;
import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Main;
import sjava.compiler.tokens.AGetToken;
import sjava.compiler.tokens.ALenToken;
import sjava.compiler.tokens.ASetToken;
import sjava.compiler.tokens.AsToken;
import sjava.compiler.tokens.BeginToken;
import sjava.compiler.tokens.BinOpToken;
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
import sjava.compiler.tokens.VToken;
import sjava.compiler.tokens.WhileToken;

public abstract class Handler {
    boolean valid(Token tok, AMethodInfo mi, Type needed) {
        return false;
    }

    public Type[] compileAll(List<Token> toks, int i, int e, AMethodInfo mi, CodeAttr code, Object needed) {
        int l = toks.size();
        Type[] types = new Type[e - i];

        for(int j = i; j < e; ++j) {
            types[j - i] = this.compile((Token)toks.get(j), mi, code, needed instanceof Type[]?((Type[])needed)[j - i]:(Type)needed);
        }

        return types;
    }

    public abstract Type compile(EmptyToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(SToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(CToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(NToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ColonToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(QuoteToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ConstToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(VToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(IncludeToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ObjectToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(MacroCallToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(BeginToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(LabelToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(GotoToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(DefineToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(TryToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(InstanceToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(SetToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ASetToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(AGetToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ALenToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(AsToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(BinOpToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(IfToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(WhileToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(CompareToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ThrowToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ClassToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(SynchronizedToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(TypeToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(ReturnToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(CallToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public abstract Type compile(DefaultToken var1, AMethodInfo var2, CodeAttr var3, Type var4);

    public Type compile(Token tok, AMethodInfo mi, CodeAttr code, Type needed) {
        Type var10000;
        if(tok instanceof EmptyToken) {
            var10000 = this.compile((EmptyToken)tok, mi, code, needed);
        } else if(tok instanceof SToken) {
            var10000 = this.compile((SToken)tok, mi, code, needed);
        } else if(tok instanceof CToken) {
            var10000 = this.compile((CToken)tok, mi, code, needed);
        } else if(tok instanceof NToken) {
            var10000 = this.compile((NToken)tok, mi, code, needed);
        } else if(tok instanceof ColonToken) {
            var10000 = this.compile((ColonToken)tok, mi, code, needed);
        } else if(tok instanceof QuoteToken) {
            var10000 = this.compile((QuoteToken)tok, mi, code, needed);
        } else if(tok instanceof ConstToken) {
            var10000 = this.compile((ConstToken)tok, mi, code, needed);
        } else if(tok instanceof VToken) {
            var10000 = this.compile((VToken)tok, mi, code, needed);
        } else if(tok instanceof IncludeToken) {
            var10000 = this.compile((IncludeToken)tok, mi, code, needed);
        } else if(tok instanceof ObjectToken) {
            var10000 = this.compile((ObjectToken)tok, mi, code, needed);
        } else if(tok instanceof MacroCallToken) {
            var10000 = this.compile((MacroCallToken)tok, mi, code, needed);
        } else if(tok instanceof BeginToken) {
            var10000 = this.compile((BeginToken)tok, mi, code, needed);
        } else if(tok instanceof LabelToken) {
            var10000 = this.compile((LabelToken)tok, mi, code, needed);
        } else if(tok instanceof GotoToken) {
            var10000 = this.compile((GotoToken)tok, mi, code, needed);
        } else if(tok instanceof DefineToken) {
            var10000 = this.compile((DefineToken)tok, mi, code, needed);
        } else if(tok instanceof TryToken) {
            var10000 = this.compile((TryToken)tok, mi, code, needed);
        } else if(tok instanceof InstanceToken) {
            var10000 = this.compile((InstanceToken)tok, mi, code, needed);
        } else if(tok instanceof SetToken) {
            var10000 = this.compile((SetToken)tok, mi, code, needed);
        } else if(tok instanceof ASetToken) {
            var10000 = this.compile((ASetToken)tok, mi, code, needed);
        } else if(tok instanceof AGetToken) {
            var10000 = this.compile((AGetToken)tok, mi, code, needed);
        } else if(tok instanceof ALenToken) {
            var10000 = this.compile((ALenToken)tok, mi, code, needed);
        } else if(tok instanceof AsToken) {
            var10000 = this.compile((AsToken)tok, mi, code, needed);
        } else if(tok instanceof BinOpToken) {
            var10000 = this.compile((BinOpToken)tok, mi, code, needed);
        } else if(tok instanceof IfToken) {
            var10000 = this.compile((IfToken)tok, mi, code, needed);
        } else if(tok instanceof WhileToken) {
            var10000 = this.compile((WhileToken)tok, mi, code, needed);
        } else if(tok instanceof CompareToken) {
            var10000 = this.compile((CompareToken)tok, mi, code, needed);
        } else if(tok instanceof ThrowToken) {
            var10000 = this.compile((ThrowToken)tok, mi, code, needed);
        } else if(tok instanceof ClassToken) {
            var10000 = this.compile((ClassToken)tok, mi, code, needed);
        } else if(tok instanceof SynchronizedToken) {
            var10000 = this.compile((SynchronizedToken)tok, mi, code, needed);
        } else if(tok instanceof TypeToken) {
            var10000 = this.compile((TypeToken)tok, mi, code, needed);
        } else if(tok instanceof ReturnToken) {
            var10000 = this.compile((ReturnToken)tok, mi, code, needed);
        } else if(tok instanceof CallToken) {
            var10000 = this.compile((CallToken)tok, mi, code, needed);
        } else {
            if(!(tok instanceof DefaultToken)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Double dispatch with ");
                sb.append(tok);
                throw new RuntimeException(sb.toString());
            }

            var10000 = this.compile((DefaultToken)tok, mi, code, needed);
        }

        return var10000;
    }

    public Type castMaybe(CodeAttr code, Type result, Type needed) {
        boolean output = code != null;
        Type var10000;
        if(needed == Main.unknownType || result == Main.returnType || result == Main.throwType) {
            var10000 = result;
        } else {
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
                            ClassType var8;
                            if(prim == null) {
                                prim = (PrimType)result;
                                var8 = ((PrimType)result).boxedType();
                            } else {
                                if(output) {
                                    code.emitConvert((PrimType)result, prim);
                                }

                                var8 = (ClassType)needed;
                            }

                            ClassType box = var8;
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
        }

        return var10000;
    }
}
