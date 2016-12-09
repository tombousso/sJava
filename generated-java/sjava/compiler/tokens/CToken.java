package sjava.compiler.tokens;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import sjava.compiler.Main;
import sjava.compiler.tokens.LexedToken;
import sjava.compiler.tokens.Transformed;

public class CToken extends LexedToken implements Transformed {
    public Character val;

    public CToken(int line, Character val) {
        super(line);
        this.val = val;
    }

    public String toString() {
        String c = this.val.toString();
        Set iterable = Main.specialChars.entrySet();
        Iterator it = iterable.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Entry entry = (Entry)it.next();
            if(this.val.equals((Character)entry.getValue())) {
                c = (String)entry.getKey();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("#\\");
        sb.append(c);
        return sb.toString();
    }

    public CToken() {
    }
}
