package ru.lanwen.tests.jersey.heroku;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.LoggerFactory;
import ru.lanwen.tests.jersey.heroku.misc.Defaults;
import ru.lanwen.tests.jersey.heroku.misc.Github;
import ru.lanwen.tests.jersey.heroku.misc.Shield;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.String.format;

@Path("")
public class RootResource {

    public static final String RELEASE_BADGE_PATTERN = "/github/{owner}/{repo}/release.{format}";


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response showHelp() {
        return Response.status(HttpStatus.NOT_FOUND_404).
                entity(format("Usage: %s[?style=flat]", RELEASE_BADGE_PATTERN)).type("text/plain").build();
    }


    @GET
    @Path(RELEASE_BADGE_PATTERN)
    public Response createBadgeForRelease(@PathParam("owner") String owner,
                          @PathParam("repo") String repo,
                          @PathParam("format") String format,
                          @QueryParam("style") @DefaultValue("default") String style) {

        Client client = ClientBuilder.newClient();
        try {
            Github.Releases releases = Github.github(owner, repo).releases(client);

            Shield shield = Shield.shield()
                    .withStatus(releases.tagName(0))
                    .withColor(releases.isPrerelease(0) ? Defaults.ORANGE_COLOR : Defaults.GREEN_COLOR)
                    .withFormat(format)
                    .withStyle(style);

            return shield.getWith(client);
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).error("Exception: {}", e.getMessage());
            return Shield.shield().withFormat(format).withStyle(style).getWith(client);
        } finally {
            client.close();
        }
    }
}
