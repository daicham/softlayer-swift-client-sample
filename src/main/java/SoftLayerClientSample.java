import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.features.ContainerApi;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * @author daicham
 */
public class SoftLayerClientSample {
    private static final String USERID = "user";
    public static final String API_KEY = "secret";
    public static final String ENDPOINT = "https://tok02.objectstorage.softlayer.net/auth/v1.0/";
//    public static final String ENDPOINT = "https://tok02.objectstorage.service.networklayer.com/auth/v1.0/";
    public static final String SWIFT_USERID = "user";
    private static ImmutableSet<Module> modules;
    private final Properties overrides;

    public static void main(String... args) {
        SoftLayerClientSample client = new SoftLayerClientSample();
        try {
//            client.fetchVirtualServers();
//            client.accessOS();
            client.accessSwift();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SoftLayerClientSample() {
        overrides = new Properties();
//        overrides.setProperty("PROPERTY_PROXY_HOST", "proxy.example.com");
//        overrides.setProperty("PROPERTY_PROXY_PORT", "8080");
//        overrides.setProperty("PROPERTY_PROXY_USER", "user");
//        overrides.setProperty("PROPERTY_PROXY_PASSWORD", "secret");

        modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

    }

    private void fetchVirtualServers() {
        try (ComputeServiceContext computeServiceContext = ContextBuilder.newBuilder("softlayer")
                .credentials(USERID, API_KEY)
                .modules(modules)
                .overrides(overrides)
                .buildView(ComputeServiceContext.class)) {

            ComputeService computeService = computeServiceContext.getComputeService();
            Set<? extends ComputeMetadata> nodes = computeService.listNodes();
            nodes.stream().forEach(n -> System.out.println("Node Name: " + n.getName()));
        }
    }

    private void accessOS() {
        try (BlobStoreContext context = ContextBuilder.newBuilder("openstack-swift")
                .credentials(SWIFT_USERID, API_KEY)
                .endpoint(ENDPOINT)
                .modules(modules)
                .buildView(BlobStoreContext.class)) {
            context.getBlobStore().createContainerInLocation(null, "sample-container");
        }
    }
    private void accessSwift() throws IOException {
        try (SwiftApi swiftApi = ContextBuilder.newBuilder("openstack-swift")
                .credentials(SWIFT_USERID, API_KEY)
                .endpoint(ENDPOINT)
                .modules(modules)
                .buildApi(SwiftApi.class)) {

            ContainerApi containerApi = swiftApi.getContainerApi("RegionOne");
//        CreateContainerOptions options = CreateContainerOptions.Builder
//                .metadata(ImmutableMap.of(
//
//                ));
            Iterable<Container> containers = containerApi.list();
            containers.forEach(c -> System.out.printf("Container: %s¥n", c));
//            containerApi.create("jclouds-smaple");
        }
    }
}
