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
import sjava.compiler.tokens.ImList;
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
import sjava.compiler.tokens.SpecialBeginToken;
import sjava.compiler.tokens.SynchronizedToken;
import sjava.compiler.tokens.ThrowToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.TryToken;
import sjava.compiler.tokens.TypeToken;
import sjava.compiler.tokens.UnquoteToken;
import sjava.compiler.tokens.VToken;
import sjava.std.Tuple3;

public abstract class AMethodInfo {
    public ClassInfo ci;
    public BeginToken block;
    public Method method;
    public ArrayList<ArrayDeque<Map<String, AVar>>> levels;
    public ArrayList<Map<String, AVar>> capturedLevels;
    Map<String, Arg> firstScope;
    ArrayDeque<Map<String, Label>> labels;
    boolean compiled;
    ImList<LexedParsedToken> toks;

    AMethodInfo(ClassInfo ci, ImList<LexedParsedToken> toks, LinkedHashMap<String, Arg> firstScope, Method method) {
        this.toks = toks == null?(ImList)null:new ImList(toks);
        this.ci = ci;
        if(toks != null && toks.size() != 0) {
            this.block = new BeginToken(((LexedParsedToken)toks.get(0)).line, (ImList)null);
        }

        this.method = method;
        this.levels = new ArrayList();
        this.ensureLevels(0);
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

    AMethodInfo(ClassInfo ci, ImList<LexedParsedToken> toks, LinkedHashMap<String, Arg> firstScope, String name, List<Type> params, Type ret, int mods) {
        this(ci, toks, firstScope, toks != null?ci.c.addMethod(name, (Type[])params.toArray(new Type[0]), ret, mods):(Method)null);
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
            this.transformBlockToks(this.block, this.toks);
            h.compile(this.block, (CodeAttr)null, this.method.getReturnType());
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

            h.compile(this.block, code, this.method.getReturnType());
            code.popScope();
            if(code.reachableHere()) {
                code.emitReturn();
            }

            this.compiled = true;
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(":");
        sb.append(this.method);
        return sb.toString();
    }

    public Token transformBlockTok(BlockToken2 block, boolean transform, LexedParsedToken tok) {
        Token ntok = this.transformBlock(tok, transform);
        if(transform && tok instanceof BlockToken && ((BlockToken)tok).toks.size() > 0 && (LexedParsedToken)((BlockToken)tok).toks.get(0) instanceof VToken) {
            String val = ((VToken)((LexedParsedToken)((BlockToken)tok).toks.get(0))).val;
            if(val.equals("label")) {
                block.labels.put(((VToken)((LexedParsedToken)((BlockToken)tok).toks.get(1))).val, new Label());
            }
        }

        return ntok;
    }

    public BlockToken2 transformBlockToks(BlockToken2 block, ImList<LexedParsedToken> toks, boolean transform, int i) {
        if(!block.isTransformed) {
            ArrayList newToks;
            for(newToks = new ArrayList(toks.take(i)); i != toks.size(); ++i) {
                newToks.add(this.transformBlockTok(block, transform, (LexedParsedToken)toks.get(i)));
            }

            block.toks = new ImList(newToks);
            block.isTransformed = true;
        }

        return block;
    }

    public BlockToken2 transformBlockToks(BlockToken2 block, ImList<LexedParsedToken> toks, boolean transform) {
        return this.transformBlockToks(block, toks, transform, 0);
    }

    public BlockToken2 transformBlockToks(BlockToken2 block, ImList<LexedParsedToken> toks) {
        return this.transformBlockToks(block, toks, true);
    }

    public ImList<Token> transformToks(ImList<LexedParsedToken> l, boolean transform) {
        ArrayList out = new ArrayList();
        Iterator it = l.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            LexedParsedToken t = (LexedParsedToken)it.next();
            out.add(this.transformBlock(t, transform || t instanceof UnquoteToken));
        }

        return new ImList(out);
    }

    public ImList<Token> transformToks(ImList<LexedParsedToken> l) {
        return this.transformToks(l, true);
    }

    public Token transformForm(BlockToken block, boolean transform) {
        String val = ((VToken)((LexedParsedToken)block.toks.get(0))).val;
        ImList rest = block.toks.skip(1);
        if(val.equals("unquote")) {
            return this.transformBlock(new UnquoteToken(block.line, rest, false));
        } else if(val.equals("varunquote")) {
            return this.transformBlock(new UnquoteToken(block.line, rest, true));
        } else if(val.equals("include")) {
            return new IncludeToken(block.line, rest);
        } else {
            if(transform) {
                if(val.equals("quote")) {
                    return this.transformBlock(new QuoteToken(block.line, rest));
                }

                if(val.equals("object")) {
                    BlockToken superTok = (BlockToken)((LexedParsedToken)rest.get(0));
                    return new ObjectToken(block.line, this.getType((LexedParsedToken)superTok.toks.get(0)), superTok.toks.skip(1), rest.skip(1));
                }

                if(val.equals("lambda")) {
                    LinkedHashMap scope = new LinkedHashMap();
                    boolean FunctionN = (LexedParsedToken)rest.get(0) instanceof BlockToken;
                    if(FunctionN) {
                        List params = Main.getParams(this.ci, (BlockToken)((LexedParsedToken)rest.get(0)), scope, 0, 1);
                        return new LambdaFnToken(block.line, (Type)null, scope, params, rest.skip(1));
                    }

                    Type t = this.getType((LexedParsedToken)rest.get(0));
                    Method sam = ((ClassType)t.getRawType()).checkSingleAbstractMethod();
                    BlockToken args = (BlockToken)((LexedParsedToken)rest.get(1));
                    ArrayList params1 = new ArrayList(args.toks.size());
                    ImList iterable = args.toks;
                    Iterator it = iterable.iterator();

                    for(int i = 0; it.hasNext(); ++i) {
                        LexedParsedToken arg = (LexedParsedToken)it.next();
                        Type param = Main.resolveType(t, sam.getGenericParameterTypes()[i]);
                        scope.put(((VToken)arg).val, new Arg(param, i + 1, 0));
                        params1.add(param);
                    }

                    return new LambdaToken(block.line, this.getType((LexedParsedToken)rest.get(0)), scope, params1, rest.skip(2), sam);
                }

                if(this.ci.fs.cs.macroNames.containsKey(val)) {
                    return new MacroIncludeToken(block.line, val, rest);
                }

                if(val.equals("begin")) {
                    return this.transformBlockToks(new BeginToken(block.line, (ImList)null), rest);
                }

                if(val.equals("sbegin")) {
                    return this.transformBlockToks(new SpecialBeginToken(block.line, (ImList)null), rest);
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
                    boolean hasFinally = rest.size() != 1 && (LexedParsedToken)((BlockToken)((LexedParsedToken)rest.get(rest.size() - 1))).toks.get(0) instanceof VToken && ((VToken)((LexedParsedToken)((BlockToken)((LexedParsedToken)rest.get(rest.size() - 1))).toks.get(0))).val.equals("finally");
                    ImList finallyToks = (ImList)null;
                    if(hasFinally) {
                        ImList finallyToks1 = ((BlockToken)((LexedParsedToken)rest.get(rest.size() - 1))).toks;
                        this.transformToks(finallyToks1.skip(1));
                        rest = rest.skipLast(1);
                    }

                    ImList collection = rest.skip(1);
                    Tuple3[] out = new Tuple3[collection.size()];
                    Iterator it1 = collection.iterator();

                    for(int i1 = 0; it1.hasNext(); ++i1) {
                        LexedParsedToken var29 = (LexedParsedToken)it1.next();
                        BlockToken var30 = (BlockToken)var29;
                        out[i1] = new Tuple3((VToken)((LexedParsedToken)var30.toks.get(0)), this.getType((LexedParsedToken)var30.toks.get(1)), this.transformToks(var30.toks.skip(2)));
                    }

                    return new TryToken(block.line, this.transformBlock((LexedParsedToken)rest.get(0)), new ImList(Arrays.asList(out)), finallyToks);
                }

                if(val.equals("instance?")) {
                    return new InstanceToken(block.line, this.transformBlock((LexedParsedToken)block.toks.get(1)), this.getType((LexedParsedToken)block.toks.get(2)));
                }

                if(val.equals("set")) {
                    return new SetToken(block.line, this.transformToks(block.toks));
                }

                if(val.equals("aset")) {
                    int arrayN = rest.size() - 2;
                    Object array = arrayN == 1?this.transformBlock((LexedParsedToken)rest.get(0)):new AGetToken(block.line, this.transformToks(rest.take(arrayN)));
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
                    return this.transformBlockToks(new SynchronizedToken(block.line, (ImList)null), block.toks);
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
                        out1[i2] = (LexedParsedToken)(i2 == 0?tok2:new UnquoteToken(tok2.line, new ImList(Arrays.asList(new LexedParsedToken[]{tok2})), false));
                    }

                    List l = Arrays.asList(out1);
                    return new QuoteToken2(block.line, new BlockToken(block.line, new ImList(l)));
                }
            }

            return (Token)null;
        }
    }

    Token transformBlock_(LexedParsedToken block, boolean transform) {
        if(!(block instanceof BlockToken)) {
            if(block instanceof ColonToken) {
                ColonToken block2 = (ColonToken)block;
                return (Token)(transform?new FieldToken(block2.line, this.transformBlock(block2.left), ((VToken)block2.right).val):new ColonToken(block2.line, (LexedParsedToken)this.transformBlock(block2.left, transform), (LexedParsedToken)this.transformBlock(block2.right, transform)));
            } else if(block instanceof QuoteToken) {
                QuoteToken block3 = (QuoteToken)block;
                return (Token)(transform?new QuoteToken2(block3.line, this.transformBlock((LexedParsedToken)block3.toks.get(0), (LexedParsedToken)block3.toks.get(0) instanceof UnquoteToken)):new QuoteToken(block3.line, Main.toLexedParsed(this.transformToks(block3.toks, false))));
            } else if(block instanceof UnquoteToken) {
                UnquoteToken block4 = (UnquoteToken)block;
                return new UnquoteToken(block4.line, block4.toks, block4.var);
            } else if(block instanceof GenericToken) {
                GenericToken block5 = (GenericToken)block;
                if(transform) {
                    throw new RuntimeException();
                } else {
                    return new GenericToken(block5.line, (LexedParsedToken)this.transformBlock(block5.tok, transform), Main.toLexedParsed(this.transformToks(block5.toks, transform)));
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
        } else {
            BlockToken block1 = (BlockToken)block;
            if(block1.toks.size() == 0) {
                return (Token)(transform?new EmptyToken(block1.line):new BlockToken(block1.line, new ImList(Collections.EMPTY_LIST)));
            } else {
                ImList rest = block1.toks.skip(1);
                Type type = transform?this.getType((LexedParsedToken)block1.toks.get(0)):(Type)null;
                if(type != null) {
                    if(!(type instanceof ArrayType)) {
                        return new ConstructorToken(block1.line, type, this.transformToks(rest));
                    } else {
                        ArrayList lens = new ArrayList();
                        if(rest.size() != 0 && (LexedParsedToken)rest.get(0) instanceof ColonToken && ((ColonToken)((LexedParsedToken)rest.get(0))).left instanceof VToken && ((VToken)((ColonToken)((LexedParsedToken)rest.get(0))).left).val.equals("len")) {
                            lens.add(this.transformBlock(((ColonToken)((LexedParsedToken)rest.get(0))).right));
                            rest = rest.skip(1);
                        } else {
                            ArrayToken tok = (ArrayToken)((LexedParsedToken)block1.toks.get(0));
                            if(tok.toks.size() > 1) {
                                lens.add(0, this.transformBlock((LexedParsedToken)tok.toks.get(1)));
                            }

                            while((LexedParsedToken)tok.toks.get(0) instanceof ArrayToken) {
                                tok = (ArrayToken)((LexedParsedToken)tok.toks.get(0));
                                if(tok.toks.size() > 1) {
                                    lens.add(0, this.transformBlock((LexedParsedToken)tok.toks.get(1)));
                                }
                            }
                        }

                        return new ArrayConstructorToken(block1.line, (ArrayType)type, new ImList(lens), this.transformToks(rest));
                    }
                } else {
                    if((LexedParsedToken)block1.toks.get(0) instanceof VToken) {
                        Token o = this.transformForm(block1, transform);
                        if(o != null) {
                            return o;
                        }
                    } else if(transform && (LexedParsedToken)block1.toks.get(0) instanceof ColonToken) {
                        ColonToken first = (ColonToken)((LexedParsedToken)block1.toks.get(0));
                        CallToken out = new CallToken(block1.line, this.transformBlock(first.left, true), ((VToken)first.right).val, this.transformToks(block1.toks.skip(1)));
                        return out;
                    }

                    return (Token)(transform?new DefaultToken(block1.line, this.transformToks(block1.toks)):new BlockToken(block1.line, Main.toLexedParsed(this.transformToks(block1.toks, false))));
                }
            }
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
