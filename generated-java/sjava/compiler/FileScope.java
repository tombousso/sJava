package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.CompileScope;
import sjava.compiler.MacroInfo;
import sjava.compiler.Main;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.IncludeToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class FileScope {
    public CompileScope cs;
    public String path;
    List<LexedParsedToken> toks;
    HashMap<String, String> imports;
    ArrayList<String> starImports;
    public MacroInfo includes;
    public List<ClassInfo> newClasses;
    String package_;

    FileScope(CompileScope cs, String path, List<LexedParsedToken> toks) {
        this.cs = cs;
        this.path = path;
        this.toks = toks;
        this.imports = new HashMap();
        this.starImports = new ArrayList();
        this.starImports.add("java.lang.");
        this.starImports.add("sjava.std.");
        this.newClasses = new ArrayList();
        this.package_ = toks.size() > 0 && (LexedParsedToken)toks.get(0) instanceof BlockToken && (LexedParsedToken)((BlockToken)((LexedParsedToken)toks.get(0))).toks.get(0) instanceof VToken && ((VToken)((LexedParsedToken)((BlockToken)((LexedParsedToken)toks.get(0))).toks.get(0))).val.equals("package")?((VToken)((LexedParsedToken)((BlockToken)((LexedParsedToken)toks.get(0))).toks.get(1))).val.concat("."):"";
        MacroInfo includes = new MacroInfo(this, "Includes");
        this.includes = includes;
        includes.c.setModifiers(Access.PUBLIC);
    }

    ClassType getNewType(Token tok) {
        ClassType var10000;
        if(tok instanceof GenericToken) {
            GenericToken tok1 = (GenericToken)tok;
            ClassType c = new ClassType(this.package_.concat(((VToken)tok1.tok).val));
            TypeVariable[] tparams = new TypeVariable[tok1.toks.size()];
            List iterable = tok1.toks;
            Iterator it = iterable.iterator();

            for(int i = 0; it.hasNext(); ++i) {
                LexedParsedToken param = (LexedParsedToken)it.next();
                String name = ((VToken)param).val;
                TypeVariable tv = new TypeVariable(name);
                tparams[i] = tv;
            }

            c.setTypeParameters(tparams);
            var10000 = c;
        } else if(tok instanceof VToken) {
            VToken tok2 = (VToken)tok;
            var10000 = new ClassType(this.package_.concat(tok2.val));
        } else {
            var10000 = (ClassType)null;
        }

        return var10000;
    }

    void compileRoot(List<MacroInfo> macros) {
        List iterable = this.toks;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            LexedParsedToken tok = (LexedParsedToken)it.next();
            this.compileRoot(macros, (BlockToken)tok);
        }

    }

    boolean getMacroParams(List<Type> out, BlockToken params, Map scope) {
        boolean varargs = false;
        int o = out.size();
        List iterable = params.toks.subList(1, params.toks.size());
        Iterator it = iterable.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            LexedParsedToken tok = (LexedParsedToken)it.next();
            Object t = Main.getCompilerType("tokens.LexedParsedToken");
            String name = ((VToken)tok).val;
            if(name.contains("@")) {
                name = name.replace("@", "");
                t = new ArrayType((Type)t);
                varargs = true;
            }

            out.add((Type)t);
            scope.put(name, new Arg((Type)t, o + i, 0));
        }

        return varargs;
    }

    void compileRoot(List<MacroInfo> macros, BlockToken tok) {
        LexedParsedToken first = (LexedParsedToken)tok.toks.get(0);
        if(first instanceof VToken) {
            if(((VToken)first).val.equals("define-class")) {
                ClassType c = this.getNewType((LexedParsedToken)tok.toks.get(1));
                String name = c.getName();
                ClassInfo ci = new ClassInfo(this, c);
                ci.supers = (BlockToken)((LexedParsedToken)tok.toks.get(2));
                this.newClasses.add(ci);
                this.cs.locals.put(name, ci.c);
                boolean run = true;
                int i = 3;

                while(run && i != tok.toks.size()) {
                    run = Main.compileClassMod((LexedParsedToken)tok.toks.get(i), ci.c);
                    if(run) {
                        ++i;
                    }
                }

                ci.toks = tok.toks.subList(i, tok.toks.size());
            } else if(((VToken)first).val.equals("import")) {
                String var9 = ((VToken)((LexedParsedToken)tok.toks.get(1))).val;
                if(var9.endsWith("*")) {
                    this.starImports.add(var9.substring(0, var9.length() - 1));
                } else {
                    this.imports.put(var9.substring(var9.lastIndexOf(".") + 1), var9);
                }
            } else if(((VToken)first).val.equals("define-macro")) {
                LinkedHashMap scope = new LinkedHashMap();
                BlockToken params = (BlockToken)((LexedParsedToken)tok.toks.get(1));
                String name1 = ((VToken)((LexedParsedToken)params.toks.get(0))).val;
                ArrayList types = new ArrayList(Arrays.asList(new Type[]{Main.getCompilerType("AMethodInfo"), Type.intType, Main.getCompilerType("handlers.GenHandler")}));
                int mods = Access.PUBLIC | Access.STATIC;
                scope.put("mi", new Arg(Main.getCompilerType("AMethodInfo"), 0, 0));
                boolean varargs = this.getMacroParams(types, params, scope);
                if(varargs) {
                    mods |= Access.TRANSIENT;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Macros");
                sb.append(this.cs.macroIndex);
                String cname = sb.toString();
                ++this.cs.macroIndex;
                MacroInfo macroi = new MacroInfo(this, cname);
                macroi.c.setModifiers(Access.PUBLIC);
                macroi.addMethod(name1, types, Main.getCompilerType("tokens.LexedParsedToken"), mods, tok.toks.subList(2, tok.toks.size()), scope);
                if(this.cs.macroNames.containsKey(name1)) {
                    ((List)this.cs.macroNames.get(name1)).add(macroi);
                } else {
                    this.cs.macroNames.put(name1, new ArrayList(Arrays.asList(new Object[]{macroi})));
                }

                macros.add(macroi);
            } else if(((VToken)first).val.equals("define-class-macro")) {
                LinkedHashMap scope1 = new LinkedHashMap();
                BlockToken params1 = (BlockToken)((LexedParsedToken)tok.toks.get(1));
                String name2 = ((VToken)((LexedParsedToken)params1.toks.get(0))).val;
                ArrayList types1 = new ArrayList(Arrays.asList(new Type[]{Main.getCompilerType("ClassInfo")}));
                int mods1 = Access.PUBLIC | Access.STATIC;
                scope1.put("ci", new Arg(Main.getCompilerType("ClassInfo"), 0, 0));
                boolean varargs1 = this.getMacroParams(types1, params1, scope1);
                if(varargs1) {
                    mods1 |= Access.TRANSIENT;
                }

                String cname1 = "Macros";
                MacroInfo macroi1 = new MacroInfo(this, cname1);
                macroi1.c.setModifiers(Access.PUBLIC);
                macroi1.addClassMacroMethod(name2, types1, Type.voidType, mods1, tok.toks.subList(2, tok.toks.size()), scope1);
                if(this.cs.classMacroNames.containsKey(name2)) {
                    ((List)this.cs.classMacroNames.get(name2)).add(macroi1);
                } else {
                    this.cs.classMacroNames.put(name2, new ArrayList(Arrays.asList(new Object[]{macroi1})));
                }
            } else if(!((VToken)first).val.equals("package")) {
                throw new RuntimeException(((VToken)first).val);
            }
        }

    }

    void compileDefs() {
        List iterable = this.newClasses;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ClassInfo ci = (ClassInfo)it.next();
            ci.compileDefs();
        }

    }

    void runClassMacros() {
        List iterable = this.newClasses;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ClassInfo ci = (ClassInfo)it.next();
            ci.runClassMacros();
        }

    }

    public void compileInclude(IncludeToken tok) {
        String name = "$".concat(Integer.toString(this.includes.c.getMethodCount()));
        LinkedHashMap scope = new LinkedHashMap();
        scope.put("mi", new Arg(Main.getCompilerType("AMethodInfo"), 0, 0));
        Type[] params = new Type[]{Main.getCompilerType("AMethodInfo"), Type.intType, Main.getCompilerType("handlers.GenHandler")};
        AMethodInfo mi = this.includes.addMethod(name, Arrays.asList(params), Main.getCompilerType("tokens.LexedParsedToken"), Access.PUBLIC | Access.STATIC, tok.toks, scope);
        mi.compileMethodBody(new GenHandler(mi));
        tok.mi = mi;
    }
}
