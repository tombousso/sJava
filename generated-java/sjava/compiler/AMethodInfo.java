package sjava.compiler;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Label;
import gnu.bytecode.Method;
import gnu.bytecode.Type;
import gnu.bytecode.Variable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import sjava.compiler.AVar;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.Main;
import sjava.compiler.Var;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.mfilters.BridgeFilter;
import sjava.compiler.tokens.BeginToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class AMethodInfo {
    public ClassInfo ci;
    public BeginToken block;
    public Method method;
    public ArrayList<ArrayDeque<Map<String, AVar>>> levels;
    Map<String, Arg> firstScope;
    ArrayDeque<Map<String, Label>> labels;
    boolean compiled;
    Deque<Integer> numLevels = new ArrayDeque();

    AMethodInfo(ClassInfo ci, List<LexedParsedToken> toks, Method method, LinkedHashMap<String, Arg> firstScope) {
        this.ci = ci;
        if(toks != null && toks.size() != 0) {
            this.block = new BeginToken(((LexedParsedToken)toks.get(0)).line, new ArrayList(toks));
        }

        this.method = method;
        this.levels = new ArrayList();
        this.ensureLevels(0);
        Set iterable = firstScope.entrySet();
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Entry entry = (Entry)it.next();
            this.putVar((String)entry.getKey(), (Arg)entry.getValue(), ((Arg)entry.getValue()).level);
        }

        this.firstScope = firstScope;
        this.labels = new ArrayDeque();
        this.compiled = false;
    }

    public void pushScope(CodeAttr code, Map label) {
        boolean output = code != null;
        ArrayList iterable = this.levels;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ArrayDeque level = (ArrayDeque)it.next();
            level.push(new HashMap());
        }

        this.labels.push(label);
    }

    public void popScope(CodeAttr code) {
        boolean output = code != null;
        ArrayList iterable = this.levels;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ArrayDeque level = (ArrayDeque)it.next();
            level.pop();
        }

        this.labels.pop();
    }

    public void pushLevel() {
        this.numLevels.push(Integer.valueOf(this.levels.size()));
    }

    public void popLevel() {
        Integer i = (Integer)this.numLevels.pop();

        while(this.levels.size() > i.intValue()) {
            this.levels.remove(this.levels.size() - 1);
        }

    }

    private void ensureLevels(int level) {
        while(this.levels.size() <= level) {
            ArrayDeque ad = new ArrayDeque();
            ad.push(new LinkedHashMap());
            this.levels.add(ad);
        }

    }

    public AVar getVar(VToken tok) {
        if(tok.macro < this.levels.size()) {
            ArrayDeque iterable = (ArrayDeque)this.levels.get(tok.macro);
            Iterator it = iterable.iterator();

            for(int notused = 0; it.hasNext(); ++notused) {
                Map scope = (Map)it.next();
                if(scope.containsKey(tok.val)) {
                    return (AVar)scope.get(tok.val);
                }
            }
        }

        return (AVar)null;
    }

    public Label getLabel(String name) {
        ArrayDeque iterable = this.labels;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Map labelScope = (Map)it.next();
            if(labelScope.containsKey(name)) {
                return (Label)labelScope.get(name);
            }
        }

        return (Label)null;
    }

    public void removeVar(VToken tok) {
        ArrayDeque iterable = (ArrayDeque)this.levels.get(tok.macro);
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Map scope = (Map)it.next();
            if(scope.containsKey(tok.val)) {
                scope.remove(tok.val);
                return;
            }
        }

    }

    public Variable newVar(CodeAttr code, VToken tok, Type type) {
        boolean output = code != null;
        Variable var = output?code.addLocal(type.getRawType(), tok.val):(Variable)null;
        this.putVar(tok, new Var(var, type));
        return var;
    }

    public void putVar(VToken tok, AVar v) {
        this.putVar(tok.val, v, tok.macro);
    }

    public void putVar(String name, AVar v, int level) {
        this.ensureLevels(level);
        ((Map)((ArrayDeque)this.levels.get(level)).getFirst()).put(name, v);
    }

    public void putVarOuter(VToken tok, AVar v) {
        this.putVarOuter(tok.val, v, tok.macro);
    }

    public void putVarOuter(String name, AVar v, int level) {
        this.ensureLevels(level);
        ((Map)((ArrayDeque)this.levels.get(level)).getLast()).put(name, v);
    }

    public Type getType(Token tok) {
        return this.ci.getType(tok);
    }

    public void compileMethodBody(GenHandler h) {
        if(!this.compiled && !this.method.isAbstract()) {
            Main.transformBlockToks(this.block, this);
            h.compile(this.block, this, (CodeAttr)null, this.method.getReturnType());
            BridgeFilter filter = new BridgeFilter(this.method);
            filter.searchAll();
            CodeAttr code = this.method.startCode();
            Set iterable = this.firstScope.entrySet();
            Iterator it = iterable.iterator();

            for(int notused = 0; it.hasNext(); ++notused) {
                Entry entry = (Entry)it.next();
                Arg arg = (Arg)entry.getValue();
                code.getArg(arg.n).setName((String)entry.getKey());
            }

            h.compile(this.block, this, code, this.method.getReturnType());
            code.popScope();
            if(code.reachableHere()) {
                code.emitReturn();
            }

            this.compiled = true;
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
