package sjava.compiler;

import gnu.bytecode.Access;
import gnu.bytecode.ArrayType;
import gnu.bytecode.ClassType;
import gnu.bytecode.Method;
import gnu.bytecode.ParameterizedType;
import gnu.bytecode.Type;
import gnu.bytecode.TypeVariable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Arg;
import sjava.compiler.FileScope;
import sjava.compiler.Main;
import sjava.compiler.MethodInfo;
import sjava.compiler.handlers.Handler;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class ClassInfo {
    public ClassType c;
    public FileScope fs;
    List<Token> toks;
    public List<AMethodInfo> methods;
    public int anonymous;
    public Class rc;
    HashMap<String, TypeVariable> tvs;

    public ClassInfo(ClassType c, FileScope fs) {
        this.fs = fs;
        this.c = c;
        this.toks = new ArrayList();
        this.methods = new ArrayList();
        this.anonymous = 1;
        if(c != null) {
            this.c.setClassfileVersion(ClassType.JDK_1_8_VERSION);
            this.c.setSuper(Type.javalangObjectType);
            TypeVariable[] args = c.getTypeParameters();
            if(args != null) {
                this.tvs = new HashMap();

                for(int i = 0; i != args.length; ++i) {
                    TypeVariable tv = args[i];
                    if(tv instanceof TypeVariable) {
                        this.tvs.put(tv.getName(), (TypeVariable)tv);
                    }
                }
            }
        }

    }

    public ClassInfo(String name, FileScope fs) {
        this(new ClassType(name), fs);
    }

    void writeFile(String dir) {
        String pre = dir.concat(this.fs.package_.replace(".", "/"));
        (new File(pre)).mkdirs();

        try {
            this.c.writeToFile(pre.concat(this.c.getSimpleName()).concat(".class"));
        } catch (Throwable var4) {
            var4.printStackTrace();
        }

    }

    Type getType(String name) {
        int i = 0;

        int dims;
        for(dims = -1; i != -1; ++dims) {
            i = name.indexOf("[]", i + 1);
        }

        name = name.replace("[", "");
        name = name.replace("]", "");
        boolean rel = name.contains(".");
        Object var10000;
        if(this.tvs != null && this.tvs.containsKey(name)) {
            var10000 = (TypeVariable)this.tvs.get(name);
        } else if(Main.constTypes.containsKey(name)) {
            var10000 = (Type)Main.constTypes.get(name);
        } else if(this.fs.locals.containsKey(name)) {
            var10000 = (Type)this.fs.locals.get(name);
        } else if(!rel && this.fs.locals.containsKey(this.fs.package_.concat(name))) {
            var10000 = (Type)this.fs.locals.get(this.fs.package_.concat(name));
        } else if(this.fs.imports.containsKey(name)) {
            var10000 = (Type)this.fs.imports.get(name);
        } else {
            Type type = (Type)null;

            for(int i1 = 0; !rel && type == null && i1 != this.fs.starImports.size(); ++i1) {
                String fullname = ((String)this.fs.starImports.get(i1)).concat(name);
                if(this.fs.locals.containsKey(fullname)) {
                    type = (Type)this.fs.locals.get(fullname);
                } else if(this.fs.classExists(fullname)) {
                    type = Type.getType(fullname);
                }
            }

            var10000 = type == null && this.fs.classExists(name)?Type.getType(name):type;
        }

        Object var8 = var10000;

        for(i = 0; i != dims; ++i) {
            var8 = new ArrayType((Type)var8);
        }

        return (Type)var8;
    }

    Type getType(Token tok) {
        Object var10000;
        if(tok instanceof GenericToken) {
            GenericToken tok1 = (GenericToken)tok;
            ClassType c = (ClassType)this.getType(((VToken)tok1.tok).val).getRawType();
            List params = tok1.toks;
            Type[] tparams = new Type[params.size()];

            for(int i = 0; i != params.size(); ++i) {
                tparams[i] = this.getType((Token)params.get(i));
            }

            var10000 = new ParameterizedType(c, tparams);
        } else {
            var10000 = tok instanceof VToken?this.getType(((VToken)tok).val):(Type)null;
        }

        return (Type)var10000;
    }

    public void compileDef(Token tok) {
        if(tok instanceof BlockToken) {
            Token first = (Token)tok.toks.get(0);
            if(first instanceof BlockToken) {
                LinkedHashMap mods = new LinkedHashMap();
                int end = 0;
                boolean i = false;
                int mod = 2;

                while(!i && mod != tok.toks.size()) {
                    Token nmod = (Token)tok.toks.get(mod);
                    if(nmod instanceof SingleQuoteToken) {
                        end |= ((Short)Main.accessModifiers.get(((VToken)((Token)nmod.toks.get(0))).val)).shortValue();
                        ++mod;
                    } else {
                        i = true;
                    }
                }

                byte var15 = 0;
                if((end & Access.STATIC) == 0) {
                    mods.put("this", new Arg(0, this.c));
                    var15 = 1;
                }

                Type[] types = Main.getParams(this, first, mods, 1, var15);
                Method m = this.c.addMethod(((VToken)((Token)first.toks.get(0))).val, types, this.getType((Token)tok.toks.get(1)), end);
                this.methods.add(new MethodInfo(this, tok.toks.subList(mod, tok.toks.size()), m, mods));
            } else {
                int var10 = 0;
                boolean var11 = false;

                for(int var12 = 2; var12 != tok.toks.size() && !var11; ++var12) {
                    Token var13 = (Token)tok.toks.get(var12);
                    if(var13 instanceof SingleQuoteToken) {
                        Short var16 = (Short)Main.accessModifiers.get(((VToken)((Token)var13.toks.get(0))).val);
                        var10 |= var16.shortValue();
                    } else {
                        var11 = true;
                    }
                }

                Type var14 = this.getType((Token)tok.toks.get(1));
                this.c.addField(((VToken)first).val, var14, var10);
            }
        }

    }

    void compileDefs() {
        ClassType c = this.c;
        List supers = ((Token)this.toks.get(2)).toks;

        int i;
        for(i = 0; i != supers.size(); ++i) {
            Type related = this.getType((Token)supers.get(i));
            if(related.isInterface()) {
                c.addInterface(related);
            } else {
                c.setSuper(related);
            }
        }

        for(i = 3; i != this.toks.size(); ++i) {
            this.compileDef((Token)this.toks.get(i));
        }

    }

    public void compileMethods(Handler h) {
        for(int i = 0; i != this.methods.size(); ++i) {
            AMethodInfo mi = (AMethodInfo)this.methods.get(i);
            mi.compileMethodBody(h);
        }

    }
}
