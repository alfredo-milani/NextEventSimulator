package it.uniroma2.pmcsn.simulation.system.cloudlet.model;

import com.google.common.base.Preconditions;
import it.uniroma2.pmcsn.simulation.system.cloud.Cloud;
import it.uniroma2.pmcsn.simulation.system.cloudlet.Cloudlet;

import javax.annotation.Nonnull;

public final class TaskDispatcherConfig {

    private final Cloudlet cloudletNode;
    private final Cloud cloudNode;

    public TaskDispatcherConfig(@Nonnull Cloudlet cloudletNode, @Nonnull Cloud cloudNode) {
        Preconditions.checkNotNull(cloudletNode, "Cloudlet node can not be null (current: %s)", cloudletNode);
        Preconditions.checkNotNull(cloudNode, "Cloud node can not be null (current: %s)", cloudNode);

        this.cloudletNode = cloudletNode;
        this.cloudNode = cloudNode;
    }

    public Cloudlet getCloudletNode() {
        return cloudletNode;
    }

    public Cloud getCloudNode() {
        return cloudNode;
    }

    public static class TaskDispatcherConfigBuilder {

        private final Cloudlet cloudletNode;
        private final Cloud cloudNode;

        public TaskDispatcherConfigBuilder(Cloudlet cloudletNode, Cloud cloudNode) {
            this.cloudletNode = cloudletNode;
            this.cloudNode = cloudNode;
        }

        public TaskDispatcherConfig build() {
            return new TaskDispatcherConfig(cloudletNode, cloudNode);
        }

        @FunctionalInterface
        public interface CloudletNodeBuilder {
            CloudNodeBuilder cloudletNode(Cloudlet cloudletNode);
        }

        @FunctionalInterface
        public interface CloudNodeBuilder {
            TaskDispatcherConfigBuilder cloudNode(Cloud cloudNode);
        }

        public static CloudletNodeBuilder builder() {
            return cloudletNode -> cloudNode -> new TaskDispatcherConfigBuilder(cloudletNode, cloudNode);
        }

    }

}
