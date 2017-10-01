package sjava.compiler.handlers;

import gnu.bytecode.Access;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Field;
import gnu.bytecode.Type;
import java.util.Map;
import sjava.compiler.AMethodInfo;
import sjava.compiler.AVar;
import sjava.compiler.VCaptured;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.tokens.VToken;

class CaptureVHandler extends GenHandler {
    AMethodInfo enc;
    Map<AVar, Field> captured;
    int n;

    CaptureVHandler(AMethodInfo mi, AMethodInfo enc, Map captured) {
        super(mi);
        this.enc = enc;
        this.captured = captured;
    }

    void assignField(VCaptured vcaptured, CodeAttr code) {
        boolean output = code != null;
        if(output && vcaptured.field == null) {
            Field var4 = super.mi.ci.c.addField("captured$".concat(Integer.toString(this.n)), vcaptured.type, Access.SYNTHETIC);
            ++this.n;
            vcaptured.field = var4;
            this.captured.put(vcaptured.avar, var4);
        }

    }

    public Type compile(VToken tok, Type needed) {
        boolean output = super.code != null;
        AVar found = super.mi.getVar(tok);
        if(found instanceof VCaptured) {
            this.assignField((VCaptured)found, super.code);
        }

        Type var10001;
        if(found == null) {
            AVar outer = this.enc.getVar(tok);
            if(outer == null) {
                throw new RuntimeException(tok.toString());
            }

            VCaptured vcaptured = new VCaptured(outer, (Field)null);
            this.assignField(vcaptured, super.code);
            super.mi.putCapturedVar(tok, vcaptured);
            var10001 = vcaptured.load(super.code);
        } else {
            var10001 = found.load(super.code);
        }

        return this.castMaybe(var10001, needed);
    }
}
