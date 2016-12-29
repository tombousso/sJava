package sjava.compiler.tokens;

import org.apache.commons.lang3.StringEscapeUtils;
import sjava.compiler.tokens.LexedToken;

public class SToken extends LexedToken {
    public String val;
    public boolean tripleQuote;

    public SToken(int line, String val, boolean tripleQuote) {
        super(line);
        this.val = val;
        this.tripleQuote = tripleQuote;
    }

    public String toString() {
        String var10000;
        if(this.tripleQuote) {
            String escaped = this.val;
            if(escaped.endsWith("\"")) {
                StringBuilder sb = new StringBuilder();
                sb.append(escaped.substring(0, escaped.length() - 1));
                sb.append("\\\"");
                escaped = sb.toString();
            } else if(escaped.endsWith("\\")) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(escaped.substring(0, escaped.length() - 1));
                sb1.append("\\\\");
                escaped = sb1.toString();
            }

            escaped = escaped.replace("\"\"\"", "\\\"\"\"");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("\"\"\"");
            sb2.append(escaped);
            sb2.append("\"\"\"");
            var10000 = sb2.toString();
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("\"");
            sb3.append(StringEscapeUtils.escapeJava(this.val));
            sb3.append("\"");
            var10000 = sb3.toString();
        }

        return var10000;
    }

    public SToken() {
    }
}
