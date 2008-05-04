package hudson.plugins.mantis.soap;

import hudson.plugins.mantis.MantisHandlingException;
import hudson.plugins.mantis.MantisSite;
import hudson.plugins.mantis.model.MantisIssue;
import hudson.plugins.mantis.model.MantisNote;

import java.math.BigInteger;
import java.rmi.RemoteException;

public final class MantisSession {

    private final MantisConnectPortType portType;

    private final MantisSite site;

    public MantisSession(final MantisSite site, final MantisConnectPortType portType) {
        this.site = site;
        this.portType = portType;
    }

    public String getConfigString(final String key) throws MantisHandlingException {
        String configString;
        try {
            configString = portType.mc_config_get_string(site.getUserName(), site
                    .getPassword(), key);
        } catch (final RemoteException e) {
            throw new MantisHandlingException(e);
        }

        return configString;
    }

    public MantisIssue getIssue(final Long id) throws MantisHandlingException {
        IssueData data;
        try {
            data = portType.mc_issue_get(site.getUserName(), site.getPassword(),
                    BigInteger.valueOf(id));
        } catch (final RemoteException e) {
            throw new MantisHandlingException(e);
        }

        return new MantisIssue(new Long(id.longValue()), data.getSummary());
    }

    public void addNote(final Long id, final MantisNote note)
            throws MantisHandlingException {
        final IssueNoteData data = new IssueNoteData();
        data.setText(note.getText());
        data.setView_state(new ObjectRef(BigInteger
                .valueOf(note.getViewState().getCode()), null));

        try {
            portType.mc_issue_note_add(site.getUserName(), site.getPassword(), BigInteger
                    .valueOf(id), data);
        } catch (final RemoteException e) {
            throw new MantisHandlingException(e);
        }
    }
}