/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.pandora.ldpclient;

import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static javax.ws.rs.core.HttpHeaders.LINK;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.dropwizard.testing.DropwizardTestSupport;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.jena.JenaRDF;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trellisldp.app.TrellisApplication;
import org.trellisldp.app.config.TrellisConfiguration;

/**
 * @author christopher-johnson
 */
public class H2ClientTest {
    private static final DropwizardTestSupport<TrellisConfiguration> APP = new DropwizardTestSupport<>(
            TrellisApplication.class, resourceFilePath("trellis-config.yml"),
            config("server.applicationConnectors[1].port", "8445"),
            config("binaries", resourceFilePath("data") + "/binaries"),
            config("mementos", resourceFilePath("data") + "/mementos"),
            config("namespaces", resourceFilePath("data/namespaces.json")),
            config("server.applicationConnectors[1].keyStorePath", resourceFilePath("keystore/trellis.jks")));
    private static final JenaRDF rdf = new JenaRDF();
    private static String baseUrl;
    private static String pid;
    private static SSLContext sslContext;

    @BeforeAll
    static void initAll() {
        APP.before();
        baseUrl = "https://localhost:8445/";
    }

    @AfterAll
    static void tearDownAll() {
        APP.after();
    }

    @BeforeEach
    void init() {
        pid = "ldp-test-" + UUID.randomUUID().toString();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetH2Head() throws Exception {
        try {
            final IRI identifier = rdf.createIRI(Objects.requireNonNull(baseUrl));
            final SimpleSSLContext sslct = new SimpleSSLContext();
            sslContext = sslct.get();
            final LdpClient h2client = new LdpClientImpl(sslContext);
            final Map<String, List<String>> headers = h2client.head(identifier);
            assertTrue(headers.containsKey(LINK));
        } catch (Throwable e) {
            System.err.println("Throwing now");
            e.printStackTrace();
            throw e;
        }
    }

}
