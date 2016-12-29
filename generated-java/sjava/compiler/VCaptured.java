package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Field;
import gnu.bytecode.Type;
import sjava.compiler.AVar;

public class VCaptured extends AVar {
    public Field field;
    public AVar avar;

    public VCaptured(AVar avar, Field field) {
        super(avar.type);
        this.avar = avar;
        this.field = field;
    }

    public Type load(CodeAttr code) {
        boolean output = code != null;
        if(output) {
            code.emitPushThis();
        }

        if(output) {
            code.emitGetField(this.field);
        }

        return super.type;
    }

    public void store(CodeAttr code) {
        throw new RuntimeException();
    }
}
