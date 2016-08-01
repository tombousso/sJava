package sjava.compiler.handlers;

import gnu.bytecode.Access;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Field;
import gnu.bytecode.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import sjava.compiler.AMethodInfo;
import sjava.compiler.AVar;
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

    public Type compile(VToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean output = code != null;
        AVar found = mi.getVar(tok);
        Type var9;
        if(found == null) {
            AVar outer = this.enc.getVar(tok);
            Field var10000;
            Field var8;
            if(output) {
                if(this.captured.containsKey(outer)) {
                    var10000 = (Field)this.captured.get(outer);
                } else {
                    var8 = mi.ci.c.addField("captured$".concat(Integer.toString(this.n)), outer.type, Access.SYNTHETIC);
                    ++this.n;
                    this.captured.put(outer, var8);
                    var10000 = var8;
                }
            } else {
                var10000 = null;
            }

            var8 = var10000;
            if(output) {
                code.emitPushThis();
            }

            if(output) {
                code.emitGetField(var8);
            }

            var9 = outer.type;
        } else {
            var9 = found.load(code);
        }

        return var9;
    }
}
