package sjava.compiler.handlers;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import org.apache.commons.lang3.StringEscapeUtils;
import sjava.compiler.AMethodInfo;
import sjava.compiler.Main;
import sjava.compiler.handlers.GenHandler;
import sjava.compiler.tokens.BeginToken;
import sjava.compiler.tokens.BinOpToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.CToken;
import sjava.compiler.tokens.CallToken;
import sjava.compiler.tokens.ColonToken;
import sjava.compiler.tokens.CompareToken;
import sjava.compiler.tokens.ConstToken;
import sjava.compiler.tokens.DefineToken;
import sjava.compiler.tokens.NToken;
import sjava.compiler.tokens.SToken;
import sjava.compiler.tokens.SetToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

public class JavaHandler extends GenHandler {
    public StringBuffer buf;
    public boolean emit = true;
    int varNum;

    public void newMethod() {
        this.varNum = 0;
    }

    int varNum() {
        int ret = this.varNum++;
        return ret;
    }

    public Type compile(SToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        if(emit) {
            this.buf.append('\"');
        }

        if(emit) {
            this.buf.append(StringEscapeUtils.escapeJava(tok.val));
        }

        if(emit) {
            this.buf.append('\"');
        }

        return super.compile(tok, mi, code, needed);
    }

    public Type compile(CToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        if(emit) {
            this.buf.append('\'');
        }

        if(emit) {
            this.buf.append(tok.val.charValue() == 39?"\\\'":tok.val.toString());
        }

        if(emit) {
            this.buf.append('\'');
        }

        return super.compile(tok, mi, code, needed);
    }

    public Type compile(NToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        if(emit) {
            this.buf.append(tok.val);
        }

        return super.compile(tok, mi, code, needed);
    }

    public Type compile(ColonToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        Token first = (Token)tok.toks.get(0);
        Type t = mi.getType(first);
        if(t != null && emit) {
            this.buf.append(t.getName());
        }

        Type out = super.compile(tok, mi, code, needed);
        if(emit) {
            this.buf.append('.');
        }

        if(emit) {
            this.buf.append(((VToken)((Token)tok.toks.get(1))).val);
        }

        return out;
    }

    public Type compile(ConstToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        if(emit) {
            this.buf.append(tok.what);
        }

        return super.compile(tok, mi, code, needed);
    }

    public Type compile(VToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        if(emit) {
            this.buf.append(tok.val);
        }

        return super.compile(tok, mi, code, needed);
    }

    public Type compile(CallToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        int num = this.varNum;

        for(int i = 1; i != tok.toks.size(); ++i) {
            Token arg = (Token)tok.toks.get(i);
            if(arg instanceof BlockToken) {
                this.compile((Token)tok.toks.get(i), mi, (CodeAttr)null, Main.unknownType);
            }
        }

        ColonToken method = (ColonToken)((Token)tok.toks.get(0));
        Token obj = (Token)method.toks.get(0);
        if(obj instanceof VToken && ((VToken)obj).val.equals("super")) {
            if(emit) {
                this.buf.append("super");
            }
        } else if(mi.getType(obj) == null) {
            this.compile((Token)method.toks.get(0), mi, (CodeAttr)null, Main.unknownType);
        } else if(emit) {
            this.buf.append(((VToken)obj).javaName());
        }

        String mname = ((VToken)((Token)method.toks.get(1))).val;
        if(!mname.equals("<init>")) {
            if(emit) {
                this.buf.append('.');
            }

            if(emit) {
                this.buf.append(mname);
            }
        }

        if(emit) {
            this.buf.append("(");
        }

        for(int i1 = 1; i1 != tok.toks.size(); ++i1) {
            Token arg1 = (Token)tok.toks.get(i1);
            if(arg1 instanceof BlockToken) {
                if(emit) {
                    this.buf.append("$");
                }

                if(emit) {
                    this.buf.append(num);
                }

                ++num;
            } else {
                this.compile((Token)tok.toks.get(i1), mi, (CodeAttr)null, Main.unknownType);
            }

            if(i1 + 1 != tok.toks.size() && emit) {
                this.buf.append(", ");
            }
        }

        if(emit) {
            this.buf.append(")");
        }

        this.emit = false;
        Type out = super.compile(tok, mi, code, needed);
        this.emit = emit;
        return out;
    }

    public Type compile(BinOpToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        if(emit) {
            this.buf.append("(");
        }

        for(int i = 1; i != tok.toks.size(); ++i) {
            this.compile((Token)tok.toks.get(i), mi, (CodeAttr)null, Main.unknownType);
            if(i + 1 != tok.toks.size() && emit) {
                this.buf.append((Token)tok.toks.get(0));
            }
        }

        if(emit) {
            this.buf.append(")");
        }

        this.emit = false;
        Type out = super.compile(tok, mi, code, needed);
        this.emit = emit;
        return out;
    }

    public Type compile(CompareToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        if(emit) {
            this.buf.append("(");
        }

        for(int i = 1; i != tok.toks.size(); ++i) {
            this.compile((Token)tok.toks.get(i), mi, (CodeAttr)null, Main.unknownType);
            if(i + 1 != tok.toks.size() && emit) {
                this.buf.append((Token)tok.toks.get(0));
            }
        }

        if(emit) {
            this.buf.append(")");
        }

        this.emit = false;
        Type out = super.compile(tok, mi, code, needed);
        this.emit = emit;
        return out;
    }

    public Type compile(BeginToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        this.emit = false;
        mi.pushScope((CodeAttr)null, tok.labels);
        this.compileAll(tok.toks, 1, tok.toks.size() - 1, mi, (CodeAttr)null, Type.voidType);
        Type type = this.compile((Token)tok.toks.get(tok.toks.size() - 1), mi, (CodeAttr)null, needed);
        mi.popScope((CodeAttr)null);
        this.emit = emit;
        int num = emit?this.varNum():-999;
        if(type != Type.voidType) {
            if(emit) {
                this.buf.append(type.getName());
            }

            if(emit) {
                this.buf.append(" $");
            }

            if(emit) {
                this.buf.append(num);
            }

            if(emit) {
                this.buf.append(";\n");
            }
        }

        if(emit) {
            this.buf.append("{\n");
        }

        mi.pushScope(code, tok.labels);

        for(int i = 1; i + 1 < tok.toks.size(); ++i) {
            this.compile((Token)((Token)tok.toks.get(i)), mi, code, Type.voidType);
            if(emit) {
                this.buf.append(";\n");
            }
        }

        Token last = (Token)tok.toks.get(tok.toks.size() - 1);
        if(last instanceof BlockToken) {
            this.compile(last, mi, code, needed);
        }

        if(type != Type.voidType) {
            if(emit) {
                this.buf.append("$");
            }

            if(emit) {
                this.buf.append(num);
            }

            if(emit) {
                this.buf.append(" = ");
            }
        }

        if(last instanceof BlockToken) {
            if(emit) {
                this.buf.append("$");
            }

            if(emit) {
                this.buf.append(this.varNum - 1);
            }
        } else {
            this.compile(last, mi, code, needed);
        }

        if(emit) {
            this.buf.append(";\n");
        }

        if(emit) {
            this.buf.append("}\n");
        }

        mi.popScope(code);
        if(emit) {
            this.varNum = num + 1;
        }

        return type;
    }

    public Type compile(SetToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        Token out = (Token)tok.toks.get(1);
        if(out instanceof ColonToken) {
            Token first = (Token)out.toks.get(0);
            Type t = mi.getType(first);
            if(t == null) {
                this.compile(first, mi, (CodeAttr)null, Main.unknownType);
            } else if(emit) {
                this.buf.append(((VToken)first).javaName());
            }

            if(emit) {
                this.buf.append('.');
            }

            if(emit) {
                this.buf.append(((VToken)((Token)out.toks.get(1))).val);
            }
        } else if(emit) {
            this.buf.append(((VToken)out).javaName());
        }

        if(emit) {
            this.buf.append(" = ");
        }

        this.compile((Token)tok.toks.get(2), mi, (CodeAttr)null, needed);
        this.emit = false;
        Type type = super.compile(tok, mi, code, needed);
        this.emit = emit;
        return type;
    }

    public Type compile(DefineToken tok, AMethodInfo mi, CodeAttr code, Type needed) {
        boolean emit = this.emit;
        Type type = mi.getType((Token)tok.toks.get(2));
        byte n = 2;
        if(type == null) {
            this.emit = false;
            type = this.compile((Token)tok.toks.get(2), mi, (CodeAttr)null, Main.unknownType);
            this.emit = emit;
        } else if(tok.toks.size() == 4) {
            n = 3;
        }

        if(emit) {
            this.buf.append(type.getName());
        }

        if(emit) {
            this.buf.append(' ');
        }

        if(emit) {
            this.buf.append(((VToken)((Token)tok.toks.get(1))).javaName());
        }

        if(emit) {
            this.buf.append(" = ");
        }

        this.compile((Token)tok.toks.get(n), mi, (CodeAttr)null, Main.unknownType);
        this.emit = false;
        Type type1 = super.compile(tok, mi, code, needed);
        this.emit = emit;
        return type1;
    }
}
