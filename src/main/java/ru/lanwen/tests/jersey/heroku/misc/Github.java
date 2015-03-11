package ru.lanwen.tests.jersey.heroku.misc;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static com.jayway.jsonpath.JsonPath.read;
import static java.lang.String.format;
import static javax.ws.rs.core.UriBuilder.fromUri;
import static org.glassfish.jersey.client.authentication.HttpAuthenticationFeature.basic;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * User: lanwen
 * Date: 24.06.14
 * Time: 13:44
 */
public class Github {
    private String token = Defaults.TOKEN;
    private String owner = "";
    private String repo = "";

    private Github(String owner, String repo) {
        this.owner = owner;
        this.repo = repo;
    }

    public static Github github(String owner, String repo) {
        return new Github(owner, repo);
    }

    public Releases releases(Client client) {
        Response github = client
                .target(fromUri(Defaults.GITHUB_BASE_URI)
                        .path("/repos/{owner}/{repo}/releases").build(owner, repo))
                .register(basic(token, "x-oauth-basic"))
                .request()
                .header(HttpHeaders.USER_AGENT, Defaults.USER_AGENT_VALUE)
                .get();
        getLogger(this.getClass()).trace("[Remain: {}]", github.getHeaderString("X-RateLimit-Remaining"));
        return new Releases(github.readEntity(String.class));
    }

    public class Releases {
        private String content;

        public Releases(String content) {
            this.content = content;
        }

        public String tagName(int index) {
            getLogger(this.getClass()).info("[Content: {}]", content);
            String tag = read(content, format("$.[%d].tag_name", index));
            getLogger(this.getClass()).info("[Tag got: {} (from {}...)]", tag, content.substring(0, 50));
            return tag;
        }

        public boolean isPrerelease(int index) {
            return read(content, format("$.[%d].prerelease", index));
        }
    }
}
