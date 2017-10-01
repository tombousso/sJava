package sjava.compiler.handlers;

import gnu.bytecode.Type;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.tokens.AGetToken;
import sjava.compiler.tokens.ALenToken;
import sjava.compiler.tokens.ASetToken;
import sjava.compiler.tokens.ArrayConstructorToken;
import sjava.compiler.tokens.AsToken;
import sjava.compiler.tokens.BeginToken;
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
import sjava.compiler.tokens.MacroIncludeToken;
import sjava.compiler.tokens.NToken;
import sjava.compiler.tokens.NumOpToken;
import sjava.compiler.tokens.ObjectToken;
import sjava.compiler.tokens.QuoteToken2;
import sjava.compiler.tokens.ReturnToken;
import sjava.compiler.tokens.SToken;
import sjava.compiler.tokens.SetToken;
import sjava.compiler.tokens.ShiftToken;
import sjava.compiler.tokens.SpecialBeginToken;
import sjava.compiler.tokens.SynchronizedToken;
import sjava.compiler.tokens.ThrowToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.TryToken;
import sjava.compiler.tokens.TypeToken;
import sjava.compiler.tokens.VToken;

public abstract class Handler {
    AMethodInfo mi;

    Handler(AMethodInfo mi) {
        this.mi = mi;
    }

    public Type[] compileAll(List<Token> toks, int i, int e, Object needed) {
        int l = toks.size();
        Type[] types = new Type[e - i];

        for(int j = i; j < e; ++j) {
            types[j - i] = this.compile((Token)toks.get(j), needed instanceof Type[]?((Type[])needed)[j - i]:(Type)needed);
        }

        return types;
    }

    public abstract Type compile(EmptyToken var1, Type var2);

    public abstract Type compile(SToken var1, Type var2);

    public abstract Type compile(CToken var1, Type var2);

    public abstract Type compile(NToken var1, Type var2);

    public abstract Type compile(FieldToken var1, Type var2);

    public abstract Type compile(QuoteToken2 var1, Type var2);

    public abstract Type compile(ConstToken var1, Type var2);

    public abstract Type compile(VToken var1, Type var2);

    public abstract Type compile(IncludeToken var1, Type var2);

    public abstract Type compile(ObjectToken var1, Type var2);

    public abstract Type compile(MacroIncludeToken var1, Type var2);

    public abstract Type compile(BeginToken var1, Type var2);

    public abstract Type compile(SpecialBeginToken var1, Type var2);

    public abstract Type compile(LabelToken var1, Type var2);

    public abstract Type compile(GotoToken var1, Type var2);

    public abstract Type compile(DefineToken var1, Type var2);

    public abstract Type compile(TryToken var1, Type var2);

    public abstract Type compile(InstanceToken var1, Type var2);

    public abstract Type compile(SetToken var1, Type var2);

    public abstract Type compile(ASetToken var1, Type var2);

    public abstract Type compile(AGetToken var1, Type var2);

    public abstract Type compile(ALenToken var1, Type var2);

    public abstract Type compile(AsToken var1, Type var2);

    public abstract Type compile(NumOpToken var1, Type var2);

    public abstract Type compile(ShiftToken var1, Type var2);

    public abstract Type compile(IfToken var1, Type var2);

    public abstract Type compile(CompareToken var1, Type var2);

    public abstract Type compile(ThrowToken var1, Type var2);

    public abstract Type compile(ClassToken var1, Type var2);

    public abstract Type compile(SynchronizedToken var1, Type var2);

    public abstract Type compile(TypeToken var1, Type var2);

    public abstract Type compile(ReturnToken var1, Type var2);

    public abstract Type compile(CallToken var1, Type var2);

    public abstract Type compile(DefaultToken var1, Type var2);

    public abstract Type compile(ConstructorToken var1, Type var2);

    public abstract Type compile(ArrayConstructorToken var1, Type var2);

    public Type compile(Token tok, Type needed) {
        Type var10000;
        if(tok instanceof EmptyToken) {
            var10000 = this.compile((EmptyToken)tok, needed);
        } else if(tok instanceof SToken) {
            var10000 = this.compile((SToken)tok, needed);
        } else if(tok instanceof CToken) {
            var10000 = this.compile((CToken)tok, needed);
        } else if(tok instanceof NToken) {
            var10000 = this.compile((NToken)tok, needed);
        } else if(tok instanceof FieldToken) {
            var10000 = this.compile((FieldToken)tok, needed);
        } else if(tok instanceof QuoteToken2) {
            var10000 = this.compile((QuoteToken2)tok, needed);
        } else if(tok instanceof ConstToken) {
            var10000 = this.compile((ConstToken)tok, needed);
        } else if(tok instanceof VToken) {
            var10000 = this.compile((VToken)tok, needed);
        } else if(tok instanceof IncludeToken) {
            var10000 = this.compile((IncludeToken)tok, needed);
        } else if(tok instanceof ObjectToken) {
            var10000 = this.compile((ObjectToken)tok, needed);
        } else if(tok instanceof MacroIncludeToken) {
            var10000 = this.compile((MacroIncludeToken)tok, needed);
        } else if(tok instanceof BeginToken) {
            var10000 = this.compile((BeginToken)tok, needed);
        } else if(tok instanceof SpecialBeginToken) {
            var10000 = this.compile((SpecialBeginToken)tok, needed);
        } else if(tok instanceof LabelToken) {
            var10000 = this.compile((LabelToken)tok, needed);
        } else if(tok instanceof GotoToken) {
            var10000 = this.compile((GotoToken)tok, needed);
        } else if(tok instanceof DefineToken) {
            var10000 = this.compile((DefineToken)tok, needed);
        } else if(tok instanceof TryToken) {
            var10000 = this.compile((TryToken)tok, needed);
        } else if(tok instanceof InstanceToken) {
            var10000 = this.compile((InstanceToken)tok, needed);
        } else if(tok instanceof SetToken) {
            var10000 = this.compile((SetToken)tok, needed);
        } else if(tok instanceof ASetToken) {
            var10000 = this.compile((ASetToken)tok, needed);
        } else if(tok instanceof AGetToken) {
            var10000 = this.compile((AGetToken)tok, needed);
        } else if(tok instanceof ALenToken) {
            var10000 = this.compile((ALenToken)tok, needed);
        } else if(tok instanceof AsToken) {
            var10000 = this.compile((AsToken)tok, needed);
        } else if(tok instanceof NumOpToken) {
            var10000 = this.compile((NumOpToken)tok, needed);
        } else if(tok instanceof ShiftToken) {
            var10000 = this.compile((ShiftToken)tok, needed);
        } else if(tok instanceof IfToken) {
            var10000 = this.compile((IfToken)tok, needed);
        } else if(tok instanceof CompareToken) {
            var10000 = this.compile((CompareToken)tok, needed);
        } else if(tok instanceof ThrowToken) {
            var10000 = this.compile((ThrowToken)tok, needed);
        } else if(tok instanceof ClassToken) {
            var10000 = this.compile((ClassToken)tok, needed);
        } else if(tok instanceof SynchronizedToken) {
            var10000 = this.compile((SynchronizedToken)tok, needed);
        } else if(tok instanceof TypeToken) {
            var10000 = this.compile((TypeToken)tok, needed);
        } else if(tok instanceof ReturnToken) {
            var10000 = this.compile((ReturnToken)tok, needed);
        } else if(tok instanceof CallToken) {
            var10000 = this.compile((CallToken)tok, needed);
        } else if(tok instanceof DefaultToken) {
            var10000 = this.compile((DefaultToken)tok, needed);
        } else if(tok instanceof ConstructorToken) {
            var10000 = this.compile((ConstructorToken)tok, needed);
        } else {
            if(!(tok instanceof ArrayConstructorToken)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Double dispatch with ");
                sb.append(tok);
                throw new RuntimeException(sb.toString());
            }

            var10000 = this.compile((ArrayConstructorToken)tok, needed);
        }

        return var10000;
    }

    public abstract Type castMaybe(Type var1, Type var2);
}
