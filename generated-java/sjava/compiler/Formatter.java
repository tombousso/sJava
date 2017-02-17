package sjava.compiler;

import java.util.Iterator;
import java.util.List;
import sjava.compiler.Lexer;
import sjava.compiler.Main;
import sjava.compiler.Parser;
import sjava.compiler.tokens.ArrayToken;
import sjava.compiler.tokens.BlockToken;
import sjava.compiler.tokens.ColonToken;
import sjava.compiler.tokens.CommentToken;
import sjava.compiler.tokens.GenericToken;
import sjava.compiler.tokens.LexedParsedToken;
import sjava.compiler.tokens.QuoteToken;
import sjava.compiler.tokens.SingleQuoteToken;
import sjava.compiler.tokens.UnquoteToken;

public class Formatter {
    public static String formatCode(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatToks(Main.parse(code, new Lexer(), new Parser(false))));
        sb.append('\n');
        return sb.toString();
    }

    static int formatToks(LexedParsedToken block, int tabs, String before, String after, StringBuffer sb, List<LexedParsedToken> toks) {
        sb.append(before);
        int line = 0;
        boolean cont = true;
        Iterator it = toks.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            LexedParsedToken tok = (LexedParsedToken)it.next();
            boolean indent = false;
            int toLine = tok.line;
            boolean multiline = tok.firstLine() != tok.lastLine();
            if(i == 0) {
                if(multiline) {
                    toLine = line + 1;
                    indent = true;
                } else {
                    toLine = line;
                }
            } else if(tok.line == line && multiline) {
                toLine = tok.line + 1;
                indent = true;
            } else {
                if(tok.line == line) {
                    sb.append(" ");
                }

                indent = tok.line != block.line;
            }

            line = formatTok(tok, line, toLine, tabs + (indent?1:0), sb);
        }

        if(block.firstLine() != block.lastLine()) {
            sb.append("\n");

            for(int i1 = tabs; i1 > 0; --i1) {
                sb.append("\t");
            }
        }

        sb.append(after);
        line = block.endLine;
        return line;
    }

    static int formatToks(LexedParsedToken block, int tabs, String before, String after, StringBuffer sb) {
        return formatToks(block, tabs, before, after, sb, block.toks);
    }

    static String formatToks(List<LexedParsedToken> toks) {
        StringBuffer sb = new StringBuffer();
        int line = toks.size() != 0?((LexedParsedToken)toks.get(0)).line:1;
        Iterator it = toks.iterator();

        for(int i = 0; it.hasNext(); ++i) {
            LexedParsedToken tok = (LexedParsedToken)it.next();
            int off = tok.line == line && i != 0?1:0;
            line = formatTok(tok, line, tok.line + off, 0, sb);
        }

        return sb.toString();
    }

    static int formatTok(LexedParsedToken tok, int line, int toLine, int tabs, StringBuffer sb) {
        for(int i = toLine - line; i > 0; --i) {
            sb.append("\n");
        }

        if(toLine != line) {
            for(int i1 = tabs; i1 > 0; --i1) {
                sb.append("\t");
            }
        }

        line = tok.line;
        if(tok instanceof BlockToken) {
            BlockToken tok1 = (BlockToken)tok;
            formatToks(tok1, tabs, "(", ")", sb);
        } else if(tok instanceof GenericToken) {
            GenericToken tok2 = (GenericToken)tok;
            formatTok((LexedParsedToken)tok2.toks.get(0), line, line, tabs, sb);
            formatToks(tok2, tabs, "{", "}", sb, tok2.toks.subList(1, tok2.toks.size()));
        } else if(tok instanceof ArrayToken) {
            ArrayToken tok3 = (ArrayToken)tok;
            formatTok((LexedParsedToken)tok3.toks.get(0), line, line, tabs, sb);
            formatToks(tok3, tabs, "[", "]", sb, tok3.toks.subList(1, tok3.toks.size()));
        } else if(tok instanceof ColonToken) {
            ColonToken tok4 = (ColonToken)tok;
            line = formatTok((LexedParsedToken)tok4.toks.get(0), line, line, tabs, sb);
            sb.append(":");
            formatTok((LexedParsedToken)tok4.toks.get(1), line, line, tabs, sb);
        } else if(tok instanceof SingleQuoteToken) {
            SingleQuoteToken tok5 = (SingleQuoteToken)tok;
            sb.append("\'");
            formatTok((LexedParsedToken)tok5.toks.get(0), line, line, tabs, sb);
        } else if(tok instanceof QuoteToken) {
            QuoteToken tok6 = (QuoteToken)tok;
            sb.append("`");
            formatTok((LexedParsedToken)tok6.toks.get(0), line, line, tabs, sb);
        } else if(tok instanceof UnquoteToken) {
            UnquoteToken tok7 = (UnquoteToken)tok;
            sb.append(tok7.var?",$":",");
            formatTok((LexedParsedToken)tok7.toks.get(0), line, line, tabs, sb);
        } else if(tok instanceof CommentToken) {
            CommentToken tok8 = (CommentToken)tok;
            sb.append(";");
            sb.append(tok8.val);
        } else {
            if(tok.toks != null) {
                throw new RuntimeException();
            }

            sb.append(tok.toString());
        }

        return tok.endLine;
    }

    public static int checkFormatted(String code) {
        String formatted = formatCode(code);
        int line = 1;
        int len = Math.min(code.length(), formatted.length());

        for(int i = 0; i < len; ++i) {
            if(code.charAt(i) != formatted.charAt(i)) {
                return line;
            }

            if(code.charAt(i) == 10) {
                ++line;
            }
        }

        return code.length() != formatted.length()?line:-1;
    }
}
