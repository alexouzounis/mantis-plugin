/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.mantis;

import hudson.MarkupText;
import hudson.model.Build;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.Run;
import hudson.plugins.mantis.model.MantisIssue;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.MockFolder;
import static org.mockito.Mockito.mock;

/**
 *
 * @author aouzounis
 */
public class MantisLinkAnnotatorTest extends HudsonTestCase{
 
    
     /**
     * Test method for
     * {@link hudson.plugins.mantis.MantisLinkAnnotator}.
     * @throws java.io.IOException
     */
    @Test
    public void testAnnotator() throws IOException {
        MantisSite s = new MantisSite(new URL("http://bacons.ddo.jp/mantis/"), "V120", "jenkinsci", "jenkinsci", null, null);
        MantisProjectProperty.DESCRIPTOR.addSite(s);        
        FreeStyleProject job = new FreeStyleProject(Jenkins.getInstance(), "testJob");
        MantisProjectProperty mantisProp = new MantisProjectProperty(null, 0, null, null, "(?:issue |M#)([0-9,]+)", true);
        job.addProperty(mantisProp);
        job.save();
        Build b = new FreeStyleBuild(job);
        b.addAction(new MantisBuildAction(Pattern.compile("(?:issue |M#)([0-9,]+)"), new MantisIssue[]{new MantisIssue(12345,"summary")}));
        b.save();
        MarkupText mtext = new MarkupText("mantis test M#0022560,0022540"
                + "M#0022560"
                + "blah blah blah blahhhh M#0022540 blah blah blah"
                + "blah blah blah blahhhh M#0022560,0022540 blah blah blah"
                + "issue 0022560,0022540"
                + "issue 0022560"
                + "blah blah blah blahhhh issue 0022540 blah blah blah"
                + "blah blah blah blahhhh issue 0022560,0022540 blah blah blah 123456 123457,14786");
        
        MantisLinkAnnotator annotator = new MantisLinkAnnotator();
        annotator.annotate(b, null, mtext);
        assertEquals(StringUtils.countMatches(mtext.toString(false), "href"),12);
        assertEquals(StringUtils.countMatches(mtext.toString(false), "tooltip"),12);
    }   
}
