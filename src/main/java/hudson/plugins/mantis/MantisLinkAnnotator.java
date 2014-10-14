package hudson.plugins.mantis;

import hudson.Extension;
import hudson.MarkupText;
import hudson.Util;
import hudson.MarkupText.SubText;
import hudson.model.AbstractBuild;
import hudson.plugins.mantis.model.MantisIssue;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.lang.math.Range;

/**
 * Creates HTML link for Mantis issues.
 *
 * @author Seiji Sogabe
 */
@Extension
public final class MantisLinkAnnotator extends ChangeLogAnnotator {

    @Override
    public void annotate(final AbstractBuild<?, ?> build, final Entry change, final MarkupText text) {
        final MantisProjectProperty mpp = MantisProjectProperty.get(build);
        if (mpp == null || mpp.getSite() == null) {
            return;
        }
        if (!mpp.isLinkEnabled()) {
            return;
        }

        final MantisBuildAction action = build.getAction(MantisBuildAction.class);
        final String url = mpp.getSite().getUrl().toExternalForm();
        
        final Pattern pattern = findRegexPattern(action, mpp);
        
        for(Range r: Utility.getIdOffsets(pattern, text.getText())){
            // retrieve id from changelog            
            SubText st=text.subText(r.getMinimumInteger(), r.getMaximumInteger());
            int id;
            try {
                id = Integer.valueOf(st.getText());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.WARNING, Messages.MantisLinkAnnotator_IllegalMantisId(st.getText()));
                continue;
            }

            // get the issue from saved one or Mantis
            MantisIssue issue;
            if (action != null) {
                issue = action.getIssue(id);
            } else {
                issue = getIssue(build, id);
            }

            // add hyperlink to Mantis
            String newUrl = Util.encode(url + "view.php?id=$1");
            if (issue == null) {
                LOGGER.log(Level.WARNING, Messages.MantisLinkAnnotator_FailedToGetMantisIssue(id));
                st.surroundWith(String.format("<a href='%s'>", newUrl), "</a>");
            } else {
                final String summary = Utility.escape(issue.getSummary());
                st.surroundWith(String.format("<a href='%s' tooltip='%s'>", newUrl, summary), "</a>");
            }
        }
    }

    private Pattern findRegexPattern(final MantisBuildAction action, final MantisProjectProperty mpp) {
        Pattern pattern = null;
        if (action != null) {
            pattern = action.getPattern();
        }
        if (pattern == null) {
            pattern = mpp.getRegexpPattern();
        }
        return pattern;
    }

    private MantisIssue getIssue(final AbstractBuild<?, ?> build, final int id) {
        final MantisSite site = MantisSite.get(build.getProject());
        MantisIssue issue;
        try {
            issue = site.getIssue(id);
        } catch (final MantisHandlingException e) {
            issue = null;
        }
        return issue;
    }

    private static final Logger LOGGER = Logger.getLogger(MantisLinkAnnotator.class.getName());
}
