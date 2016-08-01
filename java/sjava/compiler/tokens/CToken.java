package sjava.compiler.tokens;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import sjava.compiler.Main;
import sjava.compiler.tokens.Token;
import sjava.compiler.tokens.Transformed;

public class CToken extends Token implements Transformed {
    public Character val;

    public CToken(int line, Character val) {
        super(line);
        this.val = val;
    }

    public String toString() {
        String c = this.val.toString();
        Set sb = Main.specialChars.entrySet();
        Iterator it = sb.iterator();

        for(int notused = 0; it.hasNext(); ++notused) {
            Entry entry = (Entry)it.next();
            if(this.val.equals((Character)entry.getValue())) {
                c = (String)entry.getKey();
            }
        }

        StringBuilder var6 = new StringBuilder();
        var6.append("#\\");
        var6.append(c);
        return var6.toString();
    }

    public CToken() {
    }
}
