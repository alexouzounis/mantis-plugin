package hudson.plugins.mantis;

import java.util.List;
import java.util.regex.Pattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author sogabe
 *
 */
public class UtilityTest {

    /**
     * Test method for
     * {@link hudson.plugins.mantis.Utility#escapeRegExp(java.lang.String)}.
     */
    @Test
    public void testEscapeRegExp() {
        assertNull(Utility.escapeRegexp(null));
        assertEquals("\\\\", Utility.escapeRegexp("\\"));
        assertEquals("\\[\\]", Utility.escapeRegexp("[]"));
        assertEquals("\\{\\}", Utility.escapeRegexp("{}"));
        assertEquals("\\(\\)", Utility.escapeRegexp("()"));
        assertEquals("\\^\\,\\|\\&\\$", Utility.escapeRegexp("^,|&$"));
        assertEquals("\\+\\*\\,\\.", Utility.escapeRegexp("+*,."));
        assertEquals("ABCD", Utility.escapeRegexp("ABCD"));
        assertEquals("%ID%", Utility.escapeRegexp("%ID%"));
    }
    
    @Test
    public void testGetIds(){
        String text = "mantis test M#0022560,0022540\n"
                + "M#0022560\n"
                + "blah blah blah blahhhh M#0022540 blah blah blah\n"
                + "blah blah blah blahhhh M#0022560,0022540 blah blah blah\n"
                + "issue 0022560,0022540\n"
                + "issue 0022560\n"
                + "blah blah blah blahhhh issue 0022540 blah blah blah\n"
                + "blah blah blah blahhhh issue 0022560,0022540 blah blah blah 123456 123457,14786";
        
        List<Integer> ids=Utility.getIds(Pattern.compile("(?:issue |M#)([0-9,]+)"), text);
        assertEquals(12,ids.size());
    }

}
