package sjava.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.ColonToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.QuoteToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.UnquoteToken;

class Parser {
    ArrayList<Token> toks;
    int i;

    Parser(ArrayList<Token> toks) {
        this.toks = toks;
        this.i = 0;
    }

    Token next() {
        Token ret = (Token)this.toks.get(this.i);
        ++this.i;
        return ret;
    }

    Token peek(int n) {
        return (Token)this.toks.get(this.i + n);
    }

    List<Token> subToks(String end) {
        ArrayList toks = new ArrayList();

        while(!this.peek(0).what.equals(end)) {
            Token t = this.parse(0);
            if(t != null) {
                toks.add(t);
            }
        }

        this.next();
        return toks;
    }

    Token parse(int prec) {
        Token t = this.next();
        String w = t.what;
        Object var10000;
        if(w.equals("(")) {
            var10000 = new BlockToken(t.line, this.subToks(")"));
        } else if(!w.equals("\'") && !w.equals("`") && !w.equals("~") && !w.equals(",$") && !w.equals(",")) {
            var10000 = t;
        } else {
            ArrayList al = new ArrayList(Arrays.asList(new Object[]{this.parse(0)}));
            var10000 = !w.equals(",") && !w.equals(",$")?(w.equals("\'")?new SingleQuoteToken(t.line, al):new QuoteToken(t.line, al, w.equals("`"))):new UnquoteToken(t.line, al, w.equals(",$"));
        }

        Object left = var10000;
        boolean cont = true;

        while(cont && this.i != this.toks.size() && this.prec() > prec) {
            String w1 = this.peek(0).what;
            if(w1.equals(":")) {
                this.next();
                Token right = this.parse(1);
                left = new ColonToken(t.line, new ArrayList(Arrays.asList(new Object[]{left, right})));
            } else if(w1.equals("{")) {
                this.next();
                left = new GenericToken(t.line, (Token)left, this.subToks("}"));
            } else {
                cont = false;
            }
        }

        return (Token)left;
    }

    int prec() {
        return this.peek(0).prec;
    }

    List<Token> parseAll() {
        ArrayList out = new ArrayList();

        while(this.i != this.toks.size()) {
            Token t = this.parse(0);
            if(t != null) {
                out.add(t);
            }
        }

        return out;
    }
}
