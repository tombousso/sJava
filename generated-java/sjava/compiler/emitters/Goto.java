package sjava.compiler.emitters;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Label;
import gnu.bytecode.Type;
import sjava.compiler.emitters.Emitter;
import sjava.compiler.handlers.GenHandler;

public class Goto extends Emitter {
    public Label label;

    public Goto(Label label) {
        this.label = label;
    }

    public Type emit(GenHandler h, CodeAttr code, Type needed) {
        boolean output = code != null;
        if(output && code.reachableHere()) {
            code.emitGoto(this.label);
        }

        return Type.voidType;
    }
}
