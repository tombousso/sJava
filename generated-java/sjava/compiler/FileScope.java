package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Arg;
import sjava.compiler.ClassInfo;
import sjava.compiler.MacroInfo;
import sjava.compiler.Main;
import sjava.compiler.MethodInfo;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.IncludeToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class FileScope {
    public String name;
    List<Token> toks;
    HashMap locals;
    HashMap<String, String> imports;
    ArrayList<String> starImports;
    HashMap<String, Boolean> found;
    public ClassInfo includes;
    public List<ClassInfo> newClasses;
    String package_;
    ArrayList<ClassInfo> macros;
    public HashMap<String, List<ClassInfo>> macroNames;

    FileScope(String name, List<Token> toks, HashMap locals) {
        this.name = name;
        this.toks = toks;
        this.locals = locals;
        this.imports = new HashMap();
        this.starImports = new ArrayList();
        this.starImports.add("java.lang.");
        this.starImports.add("sjava.std.");
        this.found = new HashMap();
        this.newClasses = new ArrayList();
        this.macros = new ArrayList();
        this.package_ = toks.size() > 0 && (Token)toks.get(0) instanceof BlockToken && (Token)((Token)toks.get(0)).toks.get(0) instanceof VToken && ((VToken)((Token)((Token)toks.get(0)).toks.get(0))).val.equals("package")?((VToken)((Token)((Token)toks.get(0)).toks.get(1))).val.concat("."):"";
    }

    boolean classExists(String name) {
        boolean var10000;
        if(this.found.containsKey(name)) {
            var10000 = ((Boolean)this.found.get(name)).booleanValue();
        } else {
            boolean b;
            try {
                Class.forName(name);
                b = true;
            } catch (Throwable var4) {
                b = false;
            }

            this.found.put(name, Boolean.valueOf(b));
            var10000 = b;
        }

        return var10000;
    }

    ClassType getNewType(Token tok) {
        ClassType var10000;
        if(tok instanceof GenericToken) {
            GenericToken tok1 = (GenericToken)tok;
            ClassType c = new ClassType(this.package_.concat(((VToken)tok1.tok).val));
            List params = tok1.toks;
            Type[] tparams = new Type[params.size()];

            for(int i = 0; i != params.size(); ++i) {
                String name = ((VToken)((Token)params.get(i))).val;
                TypeVariable tv = new TypeVariable(name);
                tparams[i] = tv;
            }

            c.setTypeParameters(tparams);
            var10000 = c;
        } else {
            var10000 = tok instanceof VToken?new ClassType(this.package_.concat(((VToken)tok).val)):(ClassType)null;
        }

        return var10000;
    }

    void compileRoot() {
        for(int i = 0; i != this.toks.size(); ++i) {
            this.compileRoot((Token)this.toks.get(i));
        }

    }

    void compileRoot(Token tok) {
        if((Token)tok.toks.get(0) instanceof VToken) {
            VToken first = (VToken)((Token)tok.toks.get(0));
            if(first.val.equals("define-class")) {
                ClassType c = this.getNewType((Token)tok.toks.get(1));
                String name = c.getName();
                ClassInfo ci = new ClassInfo(c, this);
                ci.toks = tok.toks;
                this.newClasses.add(ci);
                this.locals.put(name, ci.c);
                boolean run = true;
                int i = 3;

                while(run && i != ci.toks.size()) {
                    run = Main.compileClassMod((Token)ci.toks.get(i), ci.c);
                    if(run) {
                        ++i;
                    }
                }
            } else if(first.val.equals("import")) {
                String var8 = ((VToken)((Token)tok.toks.get(1))).val;
                if(var8.equals("%tokens%")) {
                    this.starImports.add("sjava.compiler.tokens.");
                } else if(var8.contains("*")) {
                    this.starImports.add(var8.replace("*", ""));
                } else {
                    this.imports.put(var8.substring(var8.lastIndexOf(".") + 1), var8);
                }
            } else if(first.val.equals("define-macro")) {
                LinkedHashMap scope = new LinkedHashMap();
                Token params = (Token)tok.toks.get(1);
                String name1 = ((VToken)((Token)params.toks.get(0))).val;
                byte o = 4;
                Type[] var10000 = new Type[o + (params.toks.size() - 1)];
                var10000[0] = Main.getCompilerType("AMethodInfo");
                var10000[1] = Type.getType("gnu.bytecode.Type");
                var10000[2] = Type.intType;
                var10000[3] = Main.getCompilerType("handlers.GenHandler");
                Type[] types = var10000;
                int mods = Access.PUBLIC | Access.STATIC;
                scope.put("mi", new Arg(0, Main.getCompilerType("AMethodInfo")));

                for(int i1 = 0; o + i1 != types.length; ++i1) {
                    Object t = Main.getCompilerType("tokens.Token");
                    String name2 = ((VToken)((Token)params.toks.get(i1 + 1))).val;
                    if(name2.contains("@")) {
                        name2 = name2.replace("@", "");
                        mods |= Access.TRANSIENT;
                        t = new ArrayType((Type)t);
                    }

                    types[o + i1] = (Type)t;
                    scope.put(name2, new Arg(o + i1, (Type)t));
                }

                String cname = "Macros$".concat(this.name.replace("/", "-")).concat("$").concat(Integer.toString(this.macros.size()));
                MacroInfo macros = new MacroInfo(cname, this);
                macros.c.setModifiers(Access.PUBLIC);
                MethodInfo macro = new MethodInfo(macros, tok.toks.subList(2, tok.toks.size()), macros.c.addMethod(name1, types, Main.getCompilerType("tokens.Token"), mods), scope);
                if(this.macroNames.containsKey(name1)) {
                    ((List)this.macroNames.get(name1)).add(macros);
                } else {
                    ArrayList al = new ArrayList();
                    al.add(macros);
                    this.macroNames.put(name1, al);
                }

                this.macros.add(macros);
                macros.methods.add(macro);
            }
        }

    }

    void compileDefs() {
        for(int i = 0; i != this.newClasses.size(); ++i) {
            ((ClassInfo)this.newClasses.get(i)).compileDefs();
        }

    }

    void compileMacros() {
        for(int i = 0; i != this.macros.size(); ++i) {
            ClassInfo macros = (ClassInfo)this.macros.get(i);
            ((AMethodInfo)macros.methods.get(0)).compileMethodBody();
            macros.rc = macros.getClazz();
        }

    }

    void compileIncludes() {
        MacroInfo includes = new MacroInfo("Includes", this);
        this.includes = includes;
        includes.c.setModifiers(Access.PUBLIC);
        int n = 0;

        for(int i = 0; i != this.newClasses.size(); ++i) {
            List methods = ((ClassInfo)this.newClasses.get(i)).methods;

            for(int j = 0; j != methods.size(); ++j) {
                AMethodInfo method = (AMethodInfo)methods.get(j);
                if(method.block != null) {
                    n = this.compileIncludes(method.block.toks, n);
                }
            }
        }

        includes.rc = includes.getClazz();
    }

    int compileIncludes(List<Token> toks, int n) {
        for(int i = 0; i != toks.size(); ++i) {
            Token tok = (Token)toks.get(i);
            if(tok.toks != null && tok.toks.size() > 0) {
                if((Token)tok.toks.get(0) instanceof VToken && ((VToken)((Token)tok.toks.get(0))).val.equals("include")) {
                    String name = "$".concat(Integer.toString(n));
                    LinkedHashMap scope = new LinkedHashMap();
                    scope.put("mi", new Arg(0, Main.getCompilerType("AMethodInfo")));
                    MethodInfo mi = new MethodInfo(this.includes, tok.toks.subList(1, tok.toks.size()), this.includes.c.addMethod(name, new Type[]{Main.getCompilerType("AMethodInfo"), Type.getType("gnu.bytecode.Type"), Type.intType, Main.getCompilerType("handlers.GenHandler")}, Main.getCompilerType("tokens.Token"), Access.PUBLIC | Access.STATIC), scope);
                    toks.set(i, new IncludeToken(tok.line, mi));
                    this.includes.methods.add(mi);
                    mi.compileMethodBody();
                    ++n;
                } else {
                    n = this.compileIncludes(tok.toks, n);
                }
            }
        }

        return n;
    }

    public void compileMethods(GenHandler h) {
        for(int i = 0; i != this.newClasses.size(); ++i) {
            ((ClassInfo)this.newClasses.get(i)).compileMethods(h);
        }

    }
}
