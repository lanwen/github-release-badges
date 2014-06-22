package ru.lanwen.tests.jersey.heroku;

import org.glassfish.jersey.internal.util.Base64;
import ru.lanwen.tests.jersey.heroku.misc.Defaults;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.net.URI;

import static com.jayway.jsonpath.JsonPath.read;
import static java.lang.String.format;
import static javax.ws.rs.core.UriBuilder.fromUri;

@Path("/github/{owner}/{repo}/badge.{format}")
public class RootResource {

    @GET
    public Response getIt(@PathParam("owner") String owner,
                          @PathParam("repo") String repo,
                          @PathParam("format") String format,
                          @QueryParam("style") @DefaultValue("default") String style) {

        Client client = ClientBuilder.newClient();

        Response github = client
                .target(fromUri(Defaults.GITHUB_BASE_URI)
                        .path("/repos/{owner}/{repo}/releases").build(owner, repo))
                .request()
                .header(HttpHeaders.AUTHORIZATION, basicWith(Defaults.TOKEN))
                .header(HttpHeaders.USER_AGENT, Defaults.USER_AGENT_VALUE)
                .get();

        String releases = github.readEntity(String.class);

        String release = read(releases, "$.[0].tag_name");
        boolean isPrerelease = read(releases, "$.[0].prerelease");

        URI shieldUri = fromUri(Defaults.SHIELDS_IO_BASE_URI)
                .path("/badge/release-{status}-{color}.{format}")
                .queryParam("style", style)
                .build(
                        escape(release),
                        isPrerelease ? Defaults.ORANGE_COLOR : Defaults.GREEN_COLOR,
                        format
                );

        System.out.println(format("[Remain: %s] %s",
                github.getHeaderString("X-RateLimit-Remaining"),
                shieldUri));

        return client.target(shieldUri).request().get();
    }

    private String escape(String release) {
        return release.replaceAll("-", "--");
    }

    private String basicWith(String token) {
        return format("Basic %s", Base64.encodeAsString(token + ":x-oauth-basic"));
    }
}
