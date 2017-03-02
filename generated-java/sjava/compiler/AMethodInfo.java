package sjava.compiler;

import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Label;
import gnu.bytecode.Method;
import gnu.bytecode.Type;
import gnu.bytecode.Variable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import sjava.compiler.tokens.AGetToken;
import sjava.compiler.tokens.ALenToken;
import sjava.compiler.tokens.ASetToken;
import sjava.compiler.tokens.ArrayConstructorToken;
import sjava.compiler.tokens.ArrayToken;
import sjava.compiler.tokens.AsToken;
import sjava.compiler.tokens.BeginToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.BlockToken2;
import sjava.compiler.tokens.CallToken;
import sjava.compiler.tokens.ClassToken;
import sjava.compiler.tokens.ColonToken;
import sjava.compiler.tokens.CompareToken;
import sjava.compiler.tokens.ConstructorToken;
import sjava.compiler.tokens.DefaultToken;
import sjava.compiler.tokens.DefineToken;
import sjava.compiler.tokens.EmptyToken;
import sjava.compiler.tokens.FieldToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.GotoToken;
import sjava.compiler.tokens.IfToken;
import sjava.compiler.tokens.IncludeToken;
import sjava.compiler.tokens.InstanceToken;
import sjava.compiler.tokens.LabelToken;
import sjava.compiler.tokens.LambdaFnToken;
import sjava.compiler.tokens.LambdaToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.MacroIncludeToken;
import sjava.compiler.tokens.NumOpToken;
import sjava.compiler.tokens.ObjectToken;
import sjava.compiler.tokens.QuoteToken;
import sjava.compiler.tokens.QuoteToken2;
import sjava.compiler.tokens.ReturnToken;
import sjava.compiler.tokens.SetToken;
import sjava.compiler.tokens.ShiftToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.SynchronizedToken;
import sjava.compiler.tokens.ThrowToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.TryToken;
import sjava.compiler.tokens.TypeToken;
import sjava.compiler.tokens.UnquoteToken;
import sjava.compiler.tokens.VToken;
import sjava.compiler.tokens.WhileToken;
import sjava.std.Tuple3;

public class AMethodInfo {
    public ClassInfo ci;
    public BeginToken block;
    public Method method;
    public ArrayList<ArrayDeque<Map<String, AVar>>> levels;
    public ArrayList<Map<String, AVar>> capturedLevels;
    Map<String, Arg> firstScope;
    ArrayDeque<Map<String, Label>> labels;
    boolean compiled;

    AMethodInfo(ClassInfo ci, List<LexedParsedToken> toks, Method method, LinkedHashMap<String, Arg> firstScope) {
        this.ci = ci;
        if(toks != null && toks.size() != 0) {
            this.block = new BeginToken(((LexedParsedToken)toks.get(0)).line, new ArrayList(toks));
        }

        this.method = method;
        this.levels = new ArrayList();
        this.capturedLevels = new ArrayList();
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
        Iterator it = this.levels.iterator();

        while(it.hasNext()) {
            ArrayDeque level = (ArrayDeque)it.next();
            if(level.size() == 0) {
                it.remove();
            } else {
                level.pop();
            }
        }

        this.labels.pop();
    }

    private void ensureLevels(int level) {
        while(this.levels.size() <= level) {
            ArrayDeque ad = new ArrayDeque();
            ad.push(new LinkedHashMap());
            this.levels.add(ad);
        }

    }

    public void putCapturedVar(VToken tok, AVar v) {
        while(this.capturedLevels.size() <= tok.macro) {
            this.capturedLevels.add(new LinkedHashMap());
        }

        ((Map)this.capturedLevels.get(tok.macro)).put(tok.val, v);
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

        return tok.macro < this.capturedLevels.size()?(AVar)((Map)this.capturedLevels.get(tok.macro)).get(tok.val):(AVar)null;
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

    public Type getType(Token tok) {
        return this.ci.getType(tok);
    }

    public void compileMethodBody(GenHandler h) {
        if(!this.compiled && !this.method.isAbstract()) {
            this.transformBlockToks(this.block);
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

    public Token transformBlockTok(BlockToken2 block, boolean transform, int i) {
        LexedParsedToken tok = (LexedParsedToken)((Token)block.toks.get(i));
        Token ntok = this.transformBlock(tok, transform);
        block.toks.set(i, ntok);
        if(transform && tok instanceof BlockToken && ((BlockToken)tok).toks.size() > 0 && (LexedParsedToken)((BlockToken)tok).toks.get(0) instanceof VToken) {
            String val = ((VToken)((LexedParsedToken)((BlockToken)tok).toks.get(0))).val;
            if(val.equals("label")) {
                block.labels.put(((VToken)((LexedParsedToken)((BlockToken)tok).toks.get(1))).val, new Label());
            }
        }

        return ntok;
    }

    public BlockToken2 transformBlockToks(BlockToken2 block, boolean transform, int i) {
        if(!block.isTransformed) {
            while(true) {
                if(i == block.toks.size()) {
                    block.isTransformed = true;
                    break;
                }

                this.transformBlockTok(block, transform, i);
                ++i;
            }
        }

        return block;
    }

    public BlockToken2 transformBlockToks(BlockToken2 block, boolean transform) {
        return this.transformBlockToks(block, transform, 0);
    }

    public BlockToken2 transformBlockToks(BlockToken2 block) {
        return this.transformBlockToks(block, true);
    }

    public List<Token> transformToks(List<LexedParsedToken> l, boolean transform) {
        ArrayList out = new ArrayList();
        Iterator it = l.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            LexedParsedToken t = (LexedParsedToken)it.next();
            out.add(this.transformBlock(t, transform || t instanceof UnquoteToken));
        }

        return out;
    }

    public List<Token> transformToks(List<LexedParsedToken> l) {
        return this.transformToks(l, true);
    }

    public Token transformForm(LexedParsedToken block, boolean transform) {
        String val = ((VToken)((LexedParsedToken)block.toks.get(0))).val;
        List rest = block.toks.subList(1, block.toks.size());
        if(val.equals("unquote")) {
            return this.transformBlock(new UnquoteToken(block.line, new ArrayList(rest), false));
        } else if(val.equals("varunquote")) {
            return this.transformBlock(new UnquoteToken(block.line, new ArrayList(rest), true));
        } else if(val.equals("include")) {
            return new IncludeToken(block.line, new ArrayList(rest));
        } else {
            if(transform) {
                if(val.equals("quote")) {
                    return this.transformBlock(new QuoteToken(block.line, new ArrayList(rest)));
                }

                if(val.equals("object")) {
                    LexedParsedToken superTok = (LexedParsedToken)rest.get(0);
                    return new ObjectToken(block.line, this.getType((LexedParsedToken)superTok.toks.get(0)), superTok.toks.subList(1, superTok.toks.size()), rest.subList(1, rest.size()));
                }

                if(val.equals("lambda")) {
                    LinkedHashMap scope = new LinkedHashMap();
                    boolean FunctionN = (LexedParsedToken)rest.get(0) instanceof BlockToken;
                    if(FunctionN) {
                        List params = Main.getParams(this.ci, (LexedParsedToken)rest.get(0), scope, 0, 1);
                        return new LambdaFnToken(block.line, (Type)null, scope, params, rest.subList(1, rest.size()));
                    }

                    Type t = this.getType((LexedParsedToken)rest.get(0));
                    Method sam = ((ClassType)t.getRawType()).checkSingleAbstractMethod();
                    LexedParsedToken args = (LexedParsedToken)rest.get(1);
                    ArrayList params1 = new ArrayList(args.toks.size());
                    List iterable = args.toks;
                    Iterator it = iterable.iterator();

                    for(int i = 0; it.hasNext(); ++i) {
                        LexedParsedToken arg = (LexedParsedToken)it.next();
                        Type param = Main.resolveType(t, sam.getGenericParameterTypes()[i]);
                        scope.put(((VToken)arg).val, new Arg(param, i + 1, 0));
                        params1.add(param);
                    }

                    return new LambdaToken(block.line, this.getType((LexedParsedToken)rest.get(0)), scope, params1, rest.subList(2, rest.size()), sam);
                }

                if(this.ci.fs.macroNames.containsKey(val)) {
                    return new MacroIncludeToken(block.line, val, new ArrayList(rest));
                }

                if(val.equals("begin")) {
                    return this.transformBlockToks(new BeginToken(block.line, new ArrayList(rest)));
                }

                if(val.equals("label")) {
                    return new LabelToken(block.line, ((VToken)((LexedParsedToken)block.toks.get(1))).val);
                }

                if(val.equals("goto")) {
                    return new GotoToken(block.line, ((VToken)((LexedParsedToken)block.toks.get(1))).val);
                }

                if(val.equals("define")) {
                    Type type = this.getType((LexedParsedToken)rest.get(1));
                    Token var10000;
                    if(type == null && rest.size() == 2) {
                        var10000 = this.transformBlock((LexedParsedToken)rest.get(1));
                    } else if(type != null && rest.size() == 3) {
                        var10000 = this.transformBlock((LexedParsedToken)rest.get(2));
                    } else {
                        if(type == null || rest.size() != 2) {
                            throw new RuntimeException(block.toString());
                        }

                        var10000 = (Token)null;
                    }

                    Token tok = var10000;
                    VToken name = (VToken)((LexedParsedToken)rest.get(0));
                    if(type == null) {
                        type = Main.unknownType;
                    }

                    return new DefineToken(block.line, name, type, tok);
                }

                if(val.equals("try")) {
                    boolean hasFinally = rest.size() != 1 && (LexedParsedToken)((LexedParsedToken)rest.get(rest.size() - 1)).toks.get(0) instanceof VToken && ((VToken)((LexedParsedToken)((LexedParsedToken)rest.get(rest.size() - 1)).toks.get(0))).val.equals("finally");
                    List finallyToks = (List)null;
                    if(hasFinally) {
                        List finallyToks1 = ((LexedParsedToken)rest.get(rest.size() - 1)).toks;
                        this.transformToks(finallyToks1.subList(1, finallyToks1.size()));
                        rest = rest.subList(0, rest.size() - 1);
                    }

                    List collection = rest.subList(1, rest.size());
                    Tuple3[] out = new Tuple3[collection.size()];
                    Iterator it1 = collection.iterator();

                    for(int i1 = 0; it1.hasNext(); ++i1) {
                        LexedParsedToken var29 = (LexedParsedToken)it1.next();
                        out[i1] = new Tuple3((VToken)((LexedParsedToken)var29.toks.get(0)), this.getType((LexedParsedToken)var29.toks.get(1)), this.transformToks(var29.toks.subList(2, var29.toks.size())));
                    }

                    return new TryToken(block.line, this.transformBlock((LexedParsedToken)rest.get(0)), Arrays.asList(out), finallyToks);
                }

                if(val.equals("instance?")) {
                    return new InstanceToken(block.line, this.transformBlock((LexedParsedToken)block.toks.get(1)), this.getType((LexedParsedToken)block.toks.get(2)));
                }

                if(val.equals("set")) {
                    return new SetToken(block.line, this.transformToks(block.toks));
                }

                if(val.equals("aset")) {
                    int arrayN = rest.size() - 2;
                    Object array = arrayN == 1?this.transformBlock((LexedParsedToken)rest.get(0)):new AGetToken(block.line, this.transformToks(rest.subList(0, arrayN)));
                    return new ASetToken(block.line, (Token)array, this.transformBlock((LexedParsedToken)rest.get(arrayN)), this.transformBlock((LexedParsedToken)rest.get(arrayN + 1)));
                }

                if(val.equals("aget")) {
                    return new AGetToken(block.line, this.transformToks(rest));
                }

                if(val.equals("alen")) {
                    return new ALenToken(block.line, this.transformBlock((LexedParsedToken)block.toks.get(1)));
                }

                if(val.equals("as")) {
                    return new AsToken(block.line, this.getType((LexedParsedToken)block.toks.get(1)), this.transformBlock((LexedParsedToken)block.toks.get(2)));
                }

                if(Main.binOps.containsKey(val)) {
                    return new NumOpToken(block.line, val, this.transformToks(rest));
                }

                if(val.equals(">>") || val.equals("<<")) {
                    return new ShiftToken(block.line, this.transformBlock((LexedParsedToken)rest.get(0)), this.transformBlock((LexedParsedToken)rest.get(1)), val.equals(">>"));
                }

                if(val.equals("if")) {
                    return new IfToken(block.line, this.transformToks(block.toks));
                }

                if(val.equals("while")) {
                    return this.transformBlockToks(new WhileToken(block.line, new ArrayList(block.toks)));
                }

                if(Main.isCompare(val)) {
                    return new CompareToken(block.line, val, this.transformToks(rest));
                }

                if(val.equals("throw")) {
                    return new ThrowToken(block.line, this.transformBlock((LexedParsedToken)block.toks.get(1)));
                }

                if(val.equals("class")) {
                    return new ClassToken(block.line, this.getType((LexedParsedToken)block.toks.get(1)));
                }

                if(val.equals("synchronized")) {
                    return this.transformBlockToks(new SynchronizedToken(block.line, new ArrayList(block.toks)));
                }

                if(val.equals("type")) {
                    return new TypeToken(block.line, this.transformBlock((LexedParsedToken)block.toks.get(1)));
                }

                if(val.equals("return")) {
                    Token tok1 = block.toks.size() == 1?(Token)null:this.transformBlock((LexedParsedToken)block.toks.get(1));
                    return new ReturnToken(block.line, tok1);
                }

                if(val.equals("macro")) {
                    LexedParsedToken[] out1 = new LexedParsedToken[rest.size()];
                    Iterator it2 = rest.iterator();

                    for(int i2 = 0; it2.hasNext(); ++i2) {
                        LexedParsedToken tok2 = (LexedParsedToken)it2.next();
                        out1[i2] = (LexedParsedToken)(i2 == 0?tok2:new UnquoteToken(tok2.line, Arrays.asList(new LexedParsedToken[]{tok2}), false));
                    }

                    List l = Arrays.asList(out1);
                    return new QuoteToken2(block.line, new BlockToken(block.line, l));
                }
            }

            return (Token)null;
        }
    }

    Token transformBlock_(LexedParsedToken block, boolean transform) {
        if(block instanceof BlockToken) {
            BlockToken block1 = (BlockToken)block;
            if(block1.toks.size() == 0) {
                return (Token)(transform?new EmptyToken(block1.line):new BlockToken(block1.line, Collections.EMPTY_LIST));
            } else {
                List rest = block1.toks.subList(1, block1.toks.size());
                Type type = transform?this.getType((LexedParsedToken)block1.toks.get(0)):(Type)null;
                if(transform && type != null) {
                    if(type instanceof ArrayType) {
                        Token len = (Token)null;
                        if(rest.size() != 0 && (LexedParsedToken)rest.get(0) instanceof ColonToken && (LexedParsedToken)((ColonToken)((LexedParsedToken)rest.get(0))).toks.get(0) instanceof VToken && ((VToken)((LexedParsedToken)((ColonToken)((LexedParsedToken)rest.get(0))).toks.get(0))).val.equals("len")) {
                            len = this.transformBlock((LexedParsedToken)((ColonToken)((LexedParsedToken)rest.get(0))).toks.get(1));
                            rest = rest.subList(1, rest.size());
                        }

                        if(rest.size() != 0 && (LexedParsedToken)rest.get(0) instanceof SingleQuoteToken) {
                            len = this.transformBlock((LexedParsedToken)((SingleQuoteToken)((LexedParsedToken)rest.get(0))).toks.get(0));
                            rest = rest.subList(1, rest.size());
                        }

                        return new ArrayConstructorToken(block1.line, (ArrayType)type, len, this.transformToks(rest));
                    } else {
                        return new ConstructorToken(block1.line, type, this.transformToks(rest));
                    }
                } else {
                    if((LexedParsedToken)block1.toks.get(0) instanceof VToken) {
                        Token o = this.transformForm(block1, transform);
                        if(o != null) {
                            return o;
                        }
                    } else if(transform && (LexedParsedToken)block1.toks.get(0) instanceof ColonToken) {
                        ColonToken first = (ColonToken)((LexedParsedToken)block1.toks.get(0));
                        CallToken out = new CallToken(block1.line, this.transformBlock((LexedParsedToken)first.toks.get(0), true), ((VToken)((LexedParsedToken)first.toks.get(1))).val, this.transformToks(block1.toks.subList(1, block1.toks.size())));
                        return out;
                    }

                    return (Token)(transform?new DefaultToken(block1.line, this.transformToks(block1.toks)):new BlockToken(block1.line, Main.toLexedParsed(this.transformToks(block1.toks, false))));
                }
            }
        } else if(block instanceof ColonToken) {
            ColonToken block2 = (ColonToken)block;
            return (Token)(transform?new FieldToken(block2.line, this.transformBlock((LexedParsedToken)block2.toks.get(0)), ((VToken)((LexedParsedToken)block2.toks.get(1))).val):new ColonToken(block2.line, Main.toLexedParsed(this.transformToks(block2.toks, transform))));
        } else if(block instanceof QuoteToken) {
            QuoteToken block3 = (QuoteToken)block;
            return (Token)(transform?new QuoteToken2(block3.line, this.transformBlock((LexedParsedToken)block3.toks.get(0), (LexedParsedToken)block3.toks.get(0) instanceof UnquoteToken)):new QuoteToken(block3.line, Main.toLexedParsed(this.transformToks(block3.toks, false))));
        } else if(block instanceof UnquoteToken) {
            UnquoteToken block4 = (UnquoteToken)block;
            return new UnquoteToken(block4.line, new ArrayList(block4.toks), block4.var);
        } else if(block instanceof GenericToken) {
            GenericToken block5 = (GenericToken)block;
            if(transform) {
                throw new RuntimeException();
            } else {
                return new GenericToken(block5.line, Main.toLexedParsed(this.transformToks(block5.toks, transform)));
            }
        } else if(block instanceof ArrayToken) {
            ArrayToken block6 = (ArrayToken)block;
            if(transform) {
                throw new RuntimeException();
            } else {
                return new ArrayToken(block6.line, Main.toLexedParsed(this.transformToks(block6.toks, transform)));
            }
        } else {
            return block;
        }
    }

    public Token transformBlock(LexedParsedToken block, boolean transform) {
        Token var10000;
        if(block.transformed != null) {
            var10000 = block.transformed;
        } else {
            Token out = this.transformBlock_(block, transform);
            block.transformed = out;
            var10000 = out;
        }

        return var10000;
    }

    public Token transformBlock(LexedParsedToken block) {
        return this.transformBlock(block, true);
    }
}
