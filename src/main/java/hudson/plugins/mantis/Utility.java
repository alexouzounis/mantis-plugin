package hudson.plugins.mantis;

import com.google.common.collect.Lists;
import hudson.MarkupText;
import hudson.Util;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;

/**
 * Utility class.
 *
 * @author Seiji Sogabe
 */
public final class Utility {
    
    private static final Logger LOGGER = Logger.getLogger(Utility.class.getName());

    private static final char[] REGEXP_CHARS = new char[]{
        '\\', '[', ']', '(', ')', '{', '}', '^', '$', '|', '?', '*', '+', '-', ':', ',', '.', '&'
    };

    private static final Pattern patternInt = Pattern.compile("([0-9]+)");
    
    static {
        Arrays.sort(REGEXP_CHARS);
    }

    private Utility() {
        //
    }

    public static String escape(final String str) {
        if (str == null) {
            return null;
        }

        final int len = str.length();
        final StringBuffer buf = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            final char c = str.charAt(i);

            switch (c) {
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                case '&':
                    if ((i < len - 1) && (str.charAt(i + 1) == '#')) {
                        buf.append(c);
                    } else {
                        buf.append("&amp;");
                    }
                    break;
                case '"':
                    buf.append("&quot;");
                    break;
                case '\'':
                    buf.append("&#039;");
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }

        return buf.toString();
    }

    public static void log(final PrintStream logger, final String message) {
        final StringBuffer buf = new StringBuffer();
        buf.append("[MANTIS] ").append(message);
        logger.println(buf.toString());
    }

    public static String escapeRegexp(final String str) {
        if (str == null) {
            return null;
        }

        final StringBuffer buf = new StringBuffer();
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            final char c = str.charAt(i);
            if (Arrays.binarySearch(REGEXP_CHARS, c) >= 0) {
                buf.append("\\");
            }
            buf.append(c);
        }

        return buf.toString();
    }
    
    public static List<Integer> getUniqueIds(Pattern p,String text){
        return Lists.newArrayList(new HashSet<Integer>(getIds(p,text)));
    }
    
    public static List<Integer> getIds(Pattern p, String text) {
        List<Integer> ids = new ArrayList<Integer>();

        for (Range r : getIdOffsets(p, text)) {
            ids.add(Integer.parseInt(text.substring(r.getMinimumInteger(), r.getMaximumInteger())));
        }
        
        return ids;
    }
    
     public static List<Range> getIdOffsets(Pattern p, String text) {
        List<Range> ranges = new ArrayList<Range>();

        final Matcher matcher = p.matcher(text);
        // get all matches
        while (matcher.find()) {
            String match = matcher.group();
            // for each match of group 1 extract all ints 
            if(match!=null && !match.isEmpty()){
                // get all ints
                Matcher intMatcher = patternInt.matcher(match);
                while (intMatcher.find()) {
                    int s = matcher.start() + intMatcher.start();
                    int e = matcher.start() + intMatcher.end();
                    ranges.add(new IntRange(s, e));
                }
            }
        }
        return ranges;
    }
}
