package sjava.compiler;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import sjava.compiler.Main;
import sjava.compiler.tokens.CToken;
import sjava.compiler.tokens.CommentToken;
import sjava.compiler.tokens.ConstToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.LexedToken;
import sjava.compiler.tokens.NToken;
import sjava.compiler.tokens.SToken;
import sjava.compiler.tokens.VToken;

public class Lexer {
    String code;
    int i;
    int len;
    String s;
    int line;

    int getprec() {
        int p = -1;

        for(int l = Main.ML; p == -1 && l != 0; --l) {
            if(this.i + l <= this.len) {
                this.s = this.peek(0, l);
                if(Main.s2prec.containsKey(this.s)) {
                    p = ((Integer)Main.s2prec.get(this.s)).intValue();
                }
            }
        }

        return p;
    }

    String peek(int n, int l) {
        return this.code.substring(this.i + n, this.i + n + l);
    }

    char peek(int n) {
        return this.code.charAt(this.i + n);
    }

    char peek() {
        return this.peek(0);
    }

    void skip(int n) {
        for(int i = 0; i < n; ++i) {
            if(this.peek() == 10) {
                ++this.line;
            }

            ++this.i;
        }

    }

    void skip() {
        this.skip(1);
    }

    void nextTok() {
        while(this.i != this.len && this.getprec() == -1 && !Character.isWhitespace(this.peek())) {
            this.skip(1);
        }

    }

    LexedToken token() {
        while(this.i != this.len && this.getprec() == -1 && Character.isWhitespace(this.peek())) {
            this.skip(1);
        }

        int p = this.getprec();
        int oline = this.line;
        Object var10000;
        if(p == -1) {
            char c = this.i == this.len?0:this.peek();
            int oi = this.i;
            if(c != 35) {
                if(Character.isDigit(c) || c == 45 && Character.isDigit(this.peek(1))) {
                    this.nextTok();
                    var10000 = new NToken(this.line, this.code.substring(oi, this.i));
                } else {
                    this.nextTok();
                    String s = this.code.substring(oi, this.i);
                    var10000 = s.equals("")?(LexedToken)null:(!s.equals("null") && !s.equals("true") && !s.equals("false")?new VToken(this.line, s):new ConstToken(this.line, s));
                }
            } else {
                while(this.peek() != 32 && this.peek() != 41) {
                    this.skip(1);
                }

                String schar = this.code.substring(oi + 2, this.i);
                var10000 = new CToken(this.line, schar.length() == 1?Character.valueOf(schar.charAt(0)):(Character)Main.specialChars.get(schar));
            }
        } else {
            this.skip(this.s.length());
            int oi1 = this.i;
            if(!this.s.equals("\"\"\"")) {
                if(!this.s.equals("\"")) {
                    if(!this.s.equals(";")) {
                        var10000 = new LexedToken(this.line, p, this.s);
                    } else {
                        while(this.i != this.len && this.peek() != 10) {
                            this.skip();
                        }

                        var10000 = new CommentToken(this.line, this.code.substring(oi1, this.i));
                    }
                } else {
                    while(this.peek() != 34) {
                        if(this.peek() == 92) {
                            this.skip(2);
                        } else {
                            this.skip();
                        }
                    }

                    this.skip();
                    var10000 = new SToken(this.line, StringEscapeUtils.unescapeJava(this.code.substring(oi1, this.i - 1)), false);
                }
            } else {
                while(true) {
                    while(!this.peek(0, 3).equals("\"\"\"")) {
                        if(this.peek(0, 7).equals("\\\"\"\"\"\"\"")) {
                            this.skip(4);
                        } else if(!this.peek(0, 5).equals("\\\\\"\"\"") && !this.peek(0, 5).equals("\\\"\"\"\"")) {
                            if(this.peek(0, 4).equals("\\\"\"\"")) {
                                this.skip(4);
                            } else {
                                this.skip();
                            }
                        } else {
                            this.skip(2);
                        }
                    }

                    String str = this.code.substring(oi1, this.i).replace("\\\"\"\"", "\"\"\"");
                    if(str.endsWith("\\\\")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str.substring(0, str.length() - 2));
                        sb.append("\\");
                        str = sb.toString();
                    } else if(str.endsWith("\\\"")) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append(str.substring(0, str.length() - 2));
                        sb1.append("\"");
                        str = sb1.toString();
                    }

                    this.skip(3);
                    var10000 = new SToken(oline, str, true);
                    break;
                }
            }
        }

        Object out = var10000;
        if(out != null) {
            ((LexedParsedToken)out).endLine = this.line;
        }

        return (LexedToken)out;
    }

    List<LexedToken> lex(String code) {
        this.code = code;
        this.i = 0;
        this.len = code.length();
        this.line = 1;
        ArrayList out = new ArrayList();

        while(this.i != this.len) {
            LexedToken tok = this.token();
            if(tok != null) {
                out.add(tok);
            }
        }

        return out;
    }
}
