package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayClassLoader;
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
import sjava.compiler.handlers.Handler;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.IncludeToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class FileScope {
    public String name;
    List<Token> toks;
    HashMap locals;
    HashMap imports;
    ArrayList<String> starImports;
    HashMap<String, Boolean> found;
    public ClassInfo includes;
    List<ClassInfo> newClasses;
    public List<ClassInfo> anonClasses;
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
        this.anonClasses = new ArrayList();
        this.macros = new ArrayList();
        this.package_ = toks.size() > 0 && (Token)toks.get(0) instanceof BlockToken && (Token)((Token)toks.get(0)).toks.get(0) instanceof VToken && ((VToken)((Token)((Token)toks.get(0)).toks.get(0))).val.equals("package")?((VToken)((Token)((Token)toks.get(0)).toks.get(1))).val.concat("."):"";
    }

    boolean classExists(String name) {
        boolean var10000;
        if(this.found.containsKey(name)) {
            var10000 = ((Boolean)this.found.get(name)).booleanValue();
        } else {
            try {
                Class.forName(name);
                var10000 = true;
            } catch (Throwable var4) {
                var10000 = false;
            }

            boolean b = var10000;
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
                ClassType scope = this.getNewType((Token)tok.toks.get(1));
                String params = scope.getName();
                ClassInfo name = new ClassInfo(scope, this);
                name.toks = tok.toks;
                this.newClasses.add(name);
                this.locals.put(params, name.c);
                boolean o = true;
                int types = 3;

                while(o && types != name.toks.size()) {
                    o = Main.compileClassMod((Token)name.toks.get(types), name.c);
                    if(o) {
                        ++types;
                    }
                }
            } else if(first.val.equals("import")) {
                String var13 = ((VToken)((Token)tok.toks.get(1))).val;
                if(var13.equals("%tokens%")) {
                    this.starImports.add("sjava.compiler.tokens.");
                } else if(var13.contains("*")) {
                    this.starImports.add(var13.replace("*", ""));
                } else {
                    this.imports.put(var13.substring(var13.lastIndexOf(".") + 1), Type.getType(var13));
                }
            } else if(first.val.equals("define-macro")) {
                LinkedHashMap var14 = new LinkedHashMap();
                Token var15 = (Token)tok.toks.get(1);
                String var16 = ((VToken)((Token)var15.toks.get(0))).val;
                byte var17 = 4;
                Type[] var10000 = new Type[var17 + (var15.toks.size() - 1)];
                var10000[0] = Main.getCompilerType("AMethodInfo");
                var10000[1] = Type.getType("gnu.bytecode.Type");
                var10000[2] = Type.intType;
                var10000[3] = Main.getCompilerType("handlers.Handler");
                Type[] var18 = var10000;
                int mods = Access.PUBLIC | Access.STATIC;
                var14.put("mi", new Arg(0, Main.getCompilerType("AMethodInfo")));

                for(int i = 0; var17 + i != var18.length; ++i) {
                    Object t = Main.getCompilerType("tokens.Token");
                    String name1 = ((VToken)((Token)var15.toks.get(i + 1))).val;
                    if(name1.contains("@")) {
                        name1 = name1.replace("@", "");
                        mods |= Access.TRANSIENT;
                        t = new ArrayType((Type)t);
                    }

                    var18[var17 + i] = (Type)t;
                    var14.put(name1, new Arg(var17 + i, (Type)t));
                }

                String var19 = "Macros$".concat(this.name.replace("/", "-")).concat("$").concat(Integer.toString(this.macros.size()));
                MacroInfo var20 = new MacroInfo(var19, this);
                var20.c.setModifiers(Access.PUBLIC);
                MethodInfo var21 = new MethodInfo(var20, tok.toks.subList(2, tok.toks.size()), var20.c.addMethod(var16, var18, Main.getCompilerType("tokens.Token"), mods), var14);
                if(this.macroNames.containsKey(var16)) {
                    ((List)this.macroNames.get(var16)).add(var20);
                } else {
                    ArrayList al = new ArrayList();
                    al.add(var20);
                    this.macroNames.put(var16, al);
                }

                this.macros.add(var20);
                var20.methods.add(var21);
            }
        }

    }

    void compileDefs() {
        for(int i = 0; i != this.newClasses.size(); ++i) {
            ((ClassInfo)this.newClasses.get(i)).compileDefs();
        }

    }

    void compileMacros(ArrayClassLoader cl) {
        for(int i = 0; i != this.macros.size(); ++i) {
            ClassInfo macros = (ClassInfo)this.macros.get(i);
            ((AMethodInfo)macros.methods.get(0)).compileMethodBody();
            String cname = macros.c.getName();
            byte[] ba = macros.c.writeToArray();
            cl.addClass(cname, ba);

            try {
                macros.rc = cl.loadClass(cname, true);
            } catch (Throwable var7) {
                var7.printStackTrace();
            }
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

        ArrayClassLoader var8 = new ArrayClassLoader();
        var8.addClass("Includes", includes.c.writeToArray());

        try {
            this.includes.rc = var8.loadClass("Includes", true);
        } catch (Throwable var7) {
            var7.printStackTrace();
        }

    }

    int compileIncludes(List<Token> toks, int n) {
        for(int i = 0; i != toks.size(); ++i) {
            Token tok = (Token)toks.get(i);
            if(tok.toks != null && tok.toks.size() > 0) {
                if((Token)tok.toks.get(0) instanceof VToken && ((VToken)((Token)tok.toks.get(0))).val.equals("include")) {
                    String name = "$".concat(Integer.toString(n));
                    MethodInfo mi = new MethodInfo(this.includes, tok.toks.subList(1, tok.toks.size()), this.includes.c.addMethod(name, new Type[]{Main.getCompilerType("AMethodInfo"), Type.getType("gnu.bytecode.Type"), Type.intType, Main.getCompilerType("handlers.Handler")}, Main.getCompilerType("tokens.Token"), Access.PUBLIC | Access.STATIC), new LinkedHashMap());
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

    public void compileMethods(Handler h) {
        for(int i = 0; i != this.newClasses.size(); ++i) {
            ((ClassInfo)this.newClasses.get(i)).compileMethods(h);
        }

    }
}
