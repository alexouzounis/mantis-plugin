package hudson.plugins.mantis.changeset;

import hudson.model.AbstractBuild;
import hudson.model.User;
import hudson.scm.ChangeLogSet.Entry;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SCM;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Logger;

/**
 * AbstractChangeSet
 * @author Seiji Sogabe
 * @since 0.7
 */
public abstract class AbstractChangeSet<T extends Entry> implements ChangeSet, Serializable {

    protected int id;
    protected AbstractBuild<?, ?> build;
    protected T entry;

    public AbstractChangeSet(final int id, final AbstractBuild<?, ?> build, final T entry) {
        this.id = id;
        this.build = build;
        this.entry = entry;
    }

    public int getId() {
        return id;
    }

    public abstract String createChangeLog();
    
    public abstract String createChangeLogWithoutPaths();


    protected RepositoryBrowser getRepositoryBrowser() {
        if (build == null || build.getProject() == null) {
            return null;
        }
        final SCM scm = build.getProject().getScm();
        return scm.getBrowser();
    }

    protected String getChangeSetLink() {
        @SuppressWarnings("unchecked")
        final RepositoryBrowser<T> browser = getRepositoryBrowser();
        if (browser == null) {
            return UNKNOWN_CHANGESETLINK;
        }

        String link = UNKNOWN_CHANGESETLINK;
        try {
            @SuppressWarnings("unchecked")
            final URL url = browser.getChangeSetLink(entry);
            if (url != null) {
                link = url.toString();
            }
        } catch (final IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return link;
    }

    protected String getAuthor() {
        final User user = entry.getAuthor();
        if (user == null) {
            return UNKNOWN_AUTHOR;
        } 
        return user.getId();
    }

    protected String getMsg() {
        if (entry == null) {
            return UNKNOWN_MSG;
        }
        return entry.getMsg();
    }
    
    private static final Logger LOGGER = Logger.getLogger(AbstractChangeSet.class.getName());
}
