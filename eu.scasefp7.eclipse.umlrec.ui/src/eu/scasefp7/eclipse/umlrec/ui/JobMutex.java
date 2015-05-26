package eu.scasefp7.eclipse.umlrec.ui;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class JobMutex implements ISchedulingRule {

        public boolean contains(ISchedulingRule rule) {
            return (rule == this);
        }

        public boolean isConflicting(ISchedulingRule rule) {
            return (rule == this);
        }

}