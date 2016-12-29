package sjava.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class Tester {
    public static void main(String[] args) {
        try {
            File dir = new File("examples/");
            WildcardFileFilter fileFilter = new WildcardFileFilter("*.expected.txt");
            File[] files = dir.listFiles((FileFilter)fileFilter);
            System.setProperty("line.separator", "\n");
            File[] array = files;

            for(int notused = 0; notused != array.length; ++notused) {
                File file = array[notused];
                String fname = file.getName();
                String name = fname.substring(0, fname.indexOf("."));
                StringBuilder sb = new StringBuilder();
                sb.append("examples.");
                sb.append(name);
                sb.append(".Main");
                Class c = Class.forName(sb.toString());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream old = System.out;
                System.setOut(ps);
                c.getMethod("main", new Class[]{String[].class}).invoke((Object)null, new Object[]{null});
                ps.flush();
                System.setOut(old);
                String expected = FileUtils.readFileToString(file);
                String out = baos.toString();
                boolean passed = out.equals(expected);
                PrintStream var10000 = System.out;
                StringBuilder sb1 = new StringBuilder();
                sb1.append(name);
                sb1.append(": ");
                sb1.append(passed?"PASSED":"FAILED");
                var10000.println(sb1.toString());
                if(!passed) {
                    return;
                }
            }
        } catch (Throwable var19) {
            var19.printStackTrace();
        }

    }
}
