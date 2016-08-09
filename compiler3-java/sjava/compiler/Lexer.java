package sjava.compiler;

import java.util.ArrayList;
import org.apache.commons.lang3.StringEscapeUtils;
import sjava.compiler.Main;
import sjava.compiler.tokens.CToken;
import sjava.compiler.tokens.ConstToken;
import sjava.compiler.tokens.NToken;
import sjava.compiler.tokens.SToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.VToken;

class Lexer {
    String code;
    int i;
    int ml;
    String s;
    int line;

    Lexer(String code) {
        this.code = code;
        this.ml = code.length();
        this.line = 1;
    }

    int getprec() {
        int ii = this.i;
        int p = 0;

        for(int l = Main.ML; p == 0 && l != 0; --l) {
            if(ii + l <= this.ml) {
                this.s = this.code.substring(ii, ii + l);
                if(Main.s2prec.containsKey(this.s)) {
                    p = ((Integer)Main.s2prec.get(this.s)).intValue();
                }
            }
        }

        return p;
    }

    void nextTok() {
        while(this.i != this.ml && !Character.isWhitespace(this.code.charAt(this.i)) && this.getprec() <= 0) {
            ++this.i;
        }

    }

    Token token() {
        while(Character.isWhitespace(this.code.charAt(this.i)) && this.getprec() == 0) {
            ++this.i;
        }

        int p = this.getprec();
        Object var10000;
        if(p == 0) {
            int oi = this.i;
            char c = this.code.charAt(this.i);
            if(c == 34) {
                ++this.i;

                for(boolean esc = false; esc || this.code.charAt(this.i) != 34; ++this.i) {
                    esc = !esc && this.code.charAt(this.i) == 92;
                }

                ++this.i;
                var10000 = new SToken(this.line, StringEscapeUtils.unescapeJava(this.code.substring(oi + 1, this.i - 1)));
            } else if(c == 35) {
                this.i += 2;
                this.nextTok();
                String schar = this.code.substring(oi + 2, this.i);
                var10000 = new CToken(this.line, schar.length() == 1?Character.valueOf(schar.charAt(0)):(Character)Main.specialChars.get(schar));
            } else if(Character.isDigit(c) || c == 45 && Character.isDigit(this.code.charAt(this.i + 1))) {
                this.nextTok();
                var10000 = new NToken(this.line, this.code.substring(oi, this.i));
            } else {
                this.nextTok();
                String s = this.code.substring(oi, this.i);
                var10000 = s.equals("null") || s.equals("true") || s.equals("false")?new ConstToken(this.line, s):new VToken(this.line, s);
            }
        } else {
            this.i += this.s.length();
            if(this.s.contains("\n")) {
                ++this.line;
            }

            var10000 = new Token(this.line, p, this.s);
        }

        return (Token)var10000;
    }

    ArrayList<Token> lex() {
        this.i = 0;
        ArrayList out = new ArrayList();

        while(this.i != this.ml) {
            out.add(this.token());
        }

        return out;
    }
}
