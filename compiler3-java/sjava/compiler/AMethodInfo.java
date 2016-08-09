package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Label;
import gnu.bytecode.Method;
import gnu.bytecode.Type;
import gnu.bytecode.Variable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sjava.compiler.AVar;
import sjava.compiler.ClassInfo;
import sjava.compiler.Main;
import sjava.compiler.Var;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.handlers.Handler;
import sjava.compiler.mfilters.BridgeFilter;
import sjava.compiler.tokens.BeginToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class AMethodInfo {
    public ClassInfo ci;
    public BlockToken block;
    public Method method;
    public ArrayList<ArrayDeque<Map<String, AVar>>> scopes;
    Map<String, AVar> firstScope;
    ArrayDeque<HashMap> labels;

    AMethodInfo(ClassInfo ci, List<Token> toks, Method method, Map<String, AVar> firstScope) {
        this.ci = ci;
        if(toks.size() != 0) {
            ArrayList btoks = new ArrayList();
            btoks.add(new VToken(((Token)toks.get(0)).line, "begin"));
            btoks.addAll(toks);
            this.block = (BlockToken)Main.transformBlockToks(new BeginToken(((Token)toks.get(0)).line, btoks), this);
        }

        this.method = method;
        this.scopes = new ArrayList();
        ArrayDeque h = new ArrayDeque();
        this.scopes.add(h);
        this.firstScope = firstScope;
        this.labels = new ArrayDeque();
    }

    public void pushScope(CodeAttr code, HashMap label) {
        boolean output = code != null;

        for(int i = 0; i != this.scopes.size(); ++i) {
            ((ArrayDeque)this.scopes.get(i)).push(new HashMap());
        }

        this.labels.push(label);
    }

    public void popScope(CodeAttr code) {
        boolean output = code != null;

        for(int i = 0; i != this.scopes.size(); ++i) {
            ((ArrayDeque)this.scopes.get(i)).pop();
        }

        this.labels.pop();
    }

    public void pushLevel() {
        this.scopes.add(new ArrayDeque());
    }

    public void popLevel() {
        this.scopes.remove(this.scopes.size() - 1);
    }

    public AVar getVar(VToken tok) {
        ArrayDeque scopes = (ArrayDeque)this.scopes.get(tok.macro);
        AVar found = (AVar)null;
        Iterator it = scopes.iterator();

        while(it.hasNext() && found == null) {
            Map vars = (Map)it.next();
            if(vars.containsKey(tok.val)) {
                found = (AVar)vars.get(tok.val);
            }
        }

        return found == null?(AVar)this.firstScope.get(tok.val):found;
    }

    public Label getLabel(String name) {
        Label found = (Label)null;
        Iterator it = this.labels.iterator();

        while(it.hasNext() && found == null) {
            HashMap vars = (HashMap)it.next();
            if(vars.containsKey(name)) {
                found = (Label)vars.get(name);
            }
        }

        return found;
    }

    public Variable newVar(CodeAttr code, VToken tok, Type type) {
        boolean output = code != null;
        String name = tok.val;
        Variable var = output?code.addLocal(type.getRawType(), name):(Variable)null;
        ((HashMap)((Map)((ArrayDeque)this.scopes.get(tok.macro)).getFirst())).put(name, new Var(var, type));
        return var;
    }

    public Type getType(Token tok) {
        return this.ci.getType(tok);
    }

    public void compileMethodBody(Handler h) {
        if(!this.method.isAbstract()) {
            h.compile((Token)this.block, this, (CodeAttr)null, this.method.getReturnType());
            BridgeFilter filter = new BridgeFilter(this.method);
            filter.searchAll();
            CodeAttr code = this.method.startCode();
            Set paramNames = this.firstScope.keySet();
            Iterator it = paramNames.iterator();

            for(int i = 0; it.hasNext(); ++i) {
                String paramName = (String)it.next();
                if(!paramName.equals("this")) {
                    code.getArg(i).setName(paramName);
                }
            }

            Type ret = h.compile((Token)this.block, this, code, this.method.getReturnType());
            code.popScope();
            if(ret != Main.returnType && code.reachableHere()) {
                code.emitReturn();
            }
        }

    }

    void compileMethodBody() {
        this.compileMethodBody(GenHandler.inst);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(":");
        sb.append(this.method);
        return sb.toString();
    }
}
