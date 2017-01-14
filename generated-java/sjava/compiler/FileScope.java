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
import sjava.compiler.MacroInfo;
import sjava.compiler.Main;
import sjava.compiler.MethodMacroInfo;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.IncludeToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class FileScope {
    public String name;
    List<LexedParsedToken> toks;
    HashMap<String, ClassType> locals;
    HashMap<String, String> imports;
    ArrayList<String> starImports;
    HashMap<String, Boolean> found;
    public ClassInfo includes;
    public List<ClassInfo> newClasses;
    String package_;
    ArrayList<ClassInfo> macros;
    public HashMap<String, List<ClassInfo>> macroNames;
    ArrayList<ClassInfo> methodMacros;
    public HashMap<String, List<ClassInfo>> methodMacroNames;

    FileScope(String name, List<LexedParsedToken> toks, HashMap locals) {
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
        this.package_ = toks.size() > 0 && (LexedParsedToken)toks.get(0) instanceof BlockToken && (LexedParsedToken)((LexedParsedToken)toks.get(0)).toks.get(0) instanceof VToken && ((VToken)((LexedParsedToken)((LexedParsedToken)toks.get(0)).toks.get(0))).val.equals("package")?((VToken)((LexedParsedToken)((LexedParsedToken)toks.get(0)).toks.get(1))).val.concat("."):"";
        MacroInfo includes = new MacroInfo("Includes", this);
        this.includes = includes;
        includes.c.setModifiers(Access.PUBLIC);
        this.methodMacros = new ArrayList();
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
            ClassType c = new ClassType(this.package_.concat(((VToken)((LexedParsedToken)tok1.toks.get(0))).val));
            List params = tok1.toks.subList(1, tok1.toks.size());
            Type[] tparams = new Type[params.size()];

            for(int i = 0; i != params.size(); ++i) {
                String name = ((VToken)((LexedParsedToken)params.get(i))).val;
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

    void compileRoot() {
        for(int i = 0; i != this.toks.size(); ++i) {
            this.compileRoot((LexedParsedToken)this.toks.get(i));
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

    void compileRoot(LexedParsedToken tok) {
        if((LexedParsedToken)tok.toks.get(0) instanceof VToken) {
            VToken first = (VToken)((LexedParsedToken)tok.toks.get(0));
            if(first.val.equals("define-class")) {
                ClassType c = this.getNewType((LexedParsedToken)tok.toks.get(1));
                String name = c.getName();
                ClassInfo ci = new ClassInfo(c, this);
                ci.toks = tok.toks;
                this.newClasses.add(ci);
                this.locals.put(name, ci.c);
                boolean run = true;
                int i = 3;

                while(run && i != tok.toks.size()) {
                    run = Main.compileClassMod((LexedParsedToken)tok.toks.get(i), ci.c);
                    if(run) {
                        ++i;
                    }
                }
            } else if(first.val.equals("import")) {
                String var8 = ((VToken)((LexedParsedToken)tok.toks.get(1))).val;
                if(var8.equals("%tokens%")) {
                    this.starImports.add("sjava.compiler.tokens.");
                } else if(var8.contains("*")) {
                    this.starImports.add(var8.replace("*", ""));
                } else {
                    this.imports.put(var8.substring(var8.lastIndexOf(".") + 1), var8);
                }
            } else if(first.val.equals("define-macro")) {
                LinkedHashMap scope = new LinkedHashMap();
                BlockToken params = (BlockToken)((LexedParsedToken)tok.toks.get(1));
                String name1 = ((VToken)((LexedParsedToken)params.toks.get(0))).val;
                ArrayList types = new ArrayList(Arrays.asList(new Type[]{Main.getCompilerType("AMethodInfo"), Type.getType("gnu.bytecode.Type"), Type.intType, Main.getCompilerType("handlers.GenHandler")}));
                int mods = Access.PUBLIC | Access.STATIC;
                scope.put("mi", new Arg(Main.getCompilerType("AMethodInfo"), 0, 0));
                boolean varargs = this.getMacroParams(types, params, scope);
                if(varargs) {
                    mods |= Access.TRANSIENT;
                }

                String cname = "Macros$".concat(this.name.replace("/", "-")).concat("$").concat(Integer.toString(this.macros.size()));
                MacroInfo macros = new MacroInfo(cname, this);
                macros.c.setModifiers(Access.PUBLIC);
                macros.addMethod(name1, types, Main.getCompilerType("tokens.LexedParsedToken"), mods, tok.toks.subList(2, tok.toks.size()), scope);
                if(this.macroNames.containsKey(name1)) {
                    ((List)this.macroNames.get(name1)).add(macros);
                } else {
                    ArrayList al = new ArrayList();
                    al.add(macros);
                    this.macroNames.put(name1, al);
                }

                this.macros.add(macros);
            } else if(first.val.equals("define-method-macro")) {
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

                StringBuilder sb = new StringBuilder();
                sb.append("Macros$");
                sb.append(this.name.replace("/", "-"));
                sb.append("$");
                sb.append(this.methodMacros.size());
                String cname1 = sb.toString();
                MethodMacroInfo macros1 = new MethodMacroInfo(cname1, this);
                macros1.c.setModifiers(Access.PUBLIC);
                AMethodInfo macro = macros1.addMethod(name2, types1, Type.voidType, mods1, tok.toks.subList(2, tok.toks.size()), scope1);
                if(this.methodMacroNames.containsKey(name2)) {
                    ((List)this.methodMacroNames.get(name2)).add(macros1);
                } else {
                    ArrayList al1 = new ArrayList();
                    al1.add(macros1);
                    this.methodMacroNames.put(name2, al1);
                }

                this.methodMacros.add(macros1);
                macros1.methods.add(macro);
            } else if(!first.val.equals("package")) {
                throw new RuntimeException(first.val);
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

    void runMethodMacros() {
        List iterable = this.newClasses;
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            ClassInfo ci = (ClassInfo)it.next();
            ci.runMethodMacros();
        }

    }

    public void compileMethods(GenHandler h) {
        for(int i = 0; i != this.newClasses.size(); ++i) {
            ((ClassInfo)this.newClasses.get(i)).compileMethods(h);
        }

    }

    public void compileInclude(IncludeToken tok) {
        String name = "$".concat(Integer.toString(this.includes.c.getMethodCount()));
        LinkedHashMap scope = new LinkedHashMap();
        scope.put("mi", new Arg(Main.getCompilerType("AMethodInfo"), 0, 0));
        Type[] params = new Type[]{Main.getCompilerType("AMethodInfo"), Type.getType("gnu.bytecode.Type"), Type.intType, Main.getCompilerType("handlers.GenHandler")};
        AMethodInfo mi = this.includes.addMethod(name, Arrays.asList(params), Main.getCompilerType("tokens.LexedParsedToken"), Access.PUBLIC | Access.STATIC, tok.toks, scope);
        mi.compileMethodBody();
        tok.mi = mi;
    }
}
