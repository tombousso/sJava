package sjava.compiler.tokens;

import org.apache.commons.lang3.StringEscapeUtils;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class SToken extends Token implements Transformed {
    public String val;

    public SToken(int line, String val) {
        super(line);
        this.val = val;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(StringEscapeUtils.escapeJava(this.val));
        sb.append("\"");
        return sb.toString();
    }

    public SToken() {
    }
}
