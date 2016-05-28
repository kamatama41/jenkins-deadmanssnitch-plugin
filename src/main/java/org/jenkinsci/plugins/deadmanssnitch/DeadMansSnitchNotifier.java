package org.jenkinsci.plugins.deadmanssnitch;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;

public class DeadMansSnitchNotifier extends Notifier {

    private final String token;
    private final String message;

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    @DataBoundConstructor
    public DeadMansSnitchNotifier(String token, String message) {
        this.token = token;
        this.message = message;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();
        if(build.getResult() == Result.SUCCESS) {
            try {
                String m = this.message;
                if(m == null || "".equals(m)) {
                    m = build.getUrl();
                }
                Snitcher.snitch(token, m);
            } catch (IOException e) {
                logger.println("ERROR: Caught IOException while trying to snitch: " + e.getMessage());
                e.printStackTrace(logger);
            }
        }
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Dead Man's Snitch Notification";
        }
    }
}

