package ru.lanwen.tests.jersey.heroku.misc;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.UriBuilder.fromUri;

/**
 * User: lanwen
 * Date: 24.06.14
 * Time: 13:25
 */
public class Shield {

    private String subject = "release";
    private String status = "unknown";
    private String color = "lightgray";
    private String format = "png";
    private String style = "default";

    private Shield() {
    }

    public static Shield shield() {
        return new Shield();
    }

    public Shield withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Shield withStatus(String status) {
        this.status = status;
        return this;
    }

    public Shield withColor(String color) {
        this.color = color;
        return this;
    }

    public Shield withFormat(String format) {
        this.format = format;
        return this;
    }

    public Shield withStyle(String style) {
        this.style = style;
        return this;
    }

    public Response getWith(Client client) {
        URI shieldUri = fromUri(Defaults.SHIELDS_IO_BASE_URI)
                .path("/badge/{subject}-{status}-{color}.{format}")
                .queryParam("style", style)
                .build(
                        subject,
                        escape(status),
                        color,
                        format
                );
         return client.target(shieldUri).request().get();
    }

    private String escape(String release) {
        return release.replaceAll("-", "--");
    }
}
