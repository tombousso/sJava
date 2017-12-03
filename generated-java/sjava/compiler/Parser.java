package sjava.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sjava.compiler.tokens.ArrayToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.ColonToken;
import sjava.compiler.tokens.CommentToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.LexedToken;
import sjava.compiler.tokens.QuoteToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.UnquoteToken;

public class Parser {
    List<LexedToken> toks;
    int i;
    boolean ignoreComments;

    public Parser(boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
    }

    public Parser() {
        this(true);
    }

    LexedToken next() {
        LexedToken ret = (LexedToken)this.toks.get(this.i);
        ++this.i;
        return ret;
    }

    LexedToken peek(int n) {
        return (LexedToken)this.toks.get(this.i + n);
    }

    ArrayList<LexedParsedToken> subToks(String end) {
        ArrayList toks = new ArrayList();

        while(!this.peek(0).what.equals(end)) {
            LexedParsedToken t = this.parse(0);
            if(t != null) {
                toks.add(t);
            }
        }

        this.next();
        return toks;
    }

    LexedParsedToken parse(int prec) {
        LexedToken t = this.next();
        String w = t.what;
        Object var10000;
        if(w.equals("(")) {
            ArrayList toks = this.subToks(")");
            var10000 = new BlockToken(t.line, toks);
        } else if(!w.equals("\'") && !w.equals("`") && !w.equals(",$") && !w.equals(",")) {
            if(this.ignoreComments && t instanceof CommentToken) {
                return (LexedParsedToken)null;
            }

            var10000 = t;
        } else {
            List al = Arrays.asList(new Object[]{this.parse(0)});
            var10000 = !w.equals(",") && !w.equals(",$")?(w.equals("\'")?new SingleQuoteToken(t.line, al):new QuoteToken(t.line, al)):new UnquoteToken(t.line, al, w.equals(",$"));
        }

        Object left = var10000;
        if(((LexedParsedToken)left).endLine == 0) {
            ((LexedParsedToken)left).endLine = this.peek(-1).line;
        }

        boolean cont = true;

        while(cont && this.i != this.toks.size() && this.prec() > prec) {
            String w1 = this.peek(0).what;
            if(w1.equals(":")) {
                this.next();
                LexedParsedToken right = this.parse(1);
                left = new ColonToken(t.line, (LexedParsedToken)left, right);
            } else if(w1.equals("{")) {
                this.next();
                ArrayList toks1 = this.subToks("}");
                left = new GenericToken(t.line, (LexedParsedToken)left, toks1);
            } else if(w1.equals("[")) {
                this.next();
                ArrayList toks2 = this.subToks("]");
                toks2.add(0, left);
                left = new ArrayToken(t.line, toks2);
            } else {
                cont = false;
            }

            if(cont) {
                ((LexedParsedToken)left).endLine = this.peek(-1).line;
            }
        }

        return (LexedParsedToken)left;
    }

    int prec() {
        return this.peek(0).prec;
    }

    List<LexedParsedToken> parseAll(List<LexedToken> toks) {
        this.i = 0;
        this.toks = toks;
        ArrayList out = new ArrayList();

        while(this.i != this.toks.size()) {
            LexedParsedToken t = this.parse(0);
            if(t != null) {
                out.add(t);
            }
        }

        return out;
    }
}
