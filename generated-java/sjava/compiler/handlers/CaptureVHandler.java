package sjava.compiler.handlers;

import gnu.bytecode.Access;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Field;
import gnu.bytecode.Type;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
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

    CaptureVHandler(AMethodInfo enc) {
        this.enc = enc;
        this.captured = new LinkedHashMap();
    }

    void assignField(VCaptured vcaptured, AMethodInfo mi, CodeAttr code) {
        boolean output = code != null;
        if(output && vcaptured.field == null) {
            Field var5 = mi.ci.c.addField("captured$".concat(Integer.toString(this.n)), vcaptured.type, Access.SYNTHETIC);
            ++this.n;
            vcaptured.field = var5;
            this.captured.put(vcaptured.avar, var5);
        }

    }

    public Type compile(VToken tok, AMethodInfo mi, Type needed) {
        boolean output = super.code != null;
        AVar found = mi.getVar(tok);
        if(found instanceof VCaptured) {
            this.assignField((VCaptured)found, mi, super.code);
        }

        Type var10000;
        if(found == null) {
            AVar outer = this.enc.getVar(tok);
            if(outer == null) {
                throw new RuntimeException(tok.toString());
            }

            VCaptured vcaptured = new VCaptured(outer, (Field)null);
            this.assignField(vcaptured, mi, super.code);
            ((Map)((ArrayDeque)mi.scopes.get(tok.macro)).getLast()).put(tok.val, vcaptured);
            var10000 = vcaptured.load(super.code);
        } else {
            var10000 = found.load(super.code);
        }

        return var10000;
    }
}
