package hudson.plugins.helpers;

import hudson.model.BuildListener;
import hudson.remoting.Callable;

import java.io.IOException;

/**
 * A helper class that is used when passing Ghostwriter between the slave and master and invoking the appropriate
 * actions on the slave or the master.
 *
 * @author Stephen Connolly
 * @since 28-Jan-2008 22:12:29
 */
class BuildProxyCallableHelper implements Callable<BuildProxy, Exception>{
    // ------------------------------ FIELDS ------------------------------

    private final BuildProxy buildProxy;
    private final Ghostwriter ghostwriter;
    private final BuildListener listener;

    // --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Creates a new BuildProxyCallableHelper.
     *
     * @param buildProxy  The buildProxy.
     * @param ghostwriter The ghostwriter.
     * @param listener    The listener.
     */
    BuildProxyCallableHelper(BuildProxy buildProxy,
                             Ghostwriter ghostwriter,
                             BuildListener listener) {
        this.buildProxy = buildProxy;
        this.ghostwriter = ghostwriter;
        this.listener = listener;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface Callable ---------------------

    /**
     * {@inheritDoc}
     */
    public BuildProxy call() throws Exception {
        if (ghostwriter instanceof Ghostwriter.SlaveGhostwriter) {
            final Ghostwriter.SlaveGhostwriter slaveBuildStep =
                    (Ghostwriter.SlaveGhostwriter) ghostwriter;
            try {
                buildProxy.setContinueBuild(slaveBuildStep.performFromSlave(buildProxy, listener));
                return buildProxy;
            } catch (IOException e) {
                throw new Exception(e);
            } catch (InterruptedException e) {
                throw new Exception(e);
            }
        }
        return buildProxy;
    }
}
