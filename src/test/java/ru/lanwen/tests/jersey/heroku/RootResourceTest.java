package ru.lanwen.tests.jersey.heroku;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import org.junit.Test;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class RootResourceTest extends JerseyTest {

    public static final String FORMAT_SVG = "svg";
    public static final String FORMAT_PNG = "png";

    public static final String HAS_RELEASES_ON_GITHUB = "allure-framework/allure-core";
    public static final String NOT_FOUND_ON_GITHUB = "adafads/adfs";
    public static final String DONT_HAVE_RELEASES = "lanwen/symro";
    public static final String NO_STYLE = "";
    public static final String STYLE_FLAT = "flat";

    @Override
    protected Application configure() {
        return new ResourceConfig(RootResource.class);
    }

    @Test
    public void shouldReturn404OnEmptyPath() {
        final Response responseMsg = target().path(NO_STYLE).request().get();
        assertThat("Should return 404 on empty path", responseMsg.getStatus(), is(HttpStatus.NOT_FOUND_404));
        assertThat("Should contain help",
                responseMsg.readEntity(String.class), containsString(RootResource.RELEASE_BADGE_PATTERN));
    }

    @Test
    public void shouldReturn200OnExistentRepoAndReleases() {
        final Response responseMsg = target().path(path(HAS_RELEASES_ON_GITHUB, FORMAT_SVG, NO_STYLE)).request().get();
        assertThat("Should return 200 if all ok", responseMsg.getStatus(), is(HttpStatus.OK_200));
    }

    @Test
    public void shouldReturn200OnUnExistentRepoAndReleases() {
        final Response responseMsg = target().path(path(NOT_FOUND_ON_GITHUB, FORMAT_SVG, NO_STYLE)).request().get();
        assertThat("Should return 200 and default badge if not found on github",
                responseMsg.getStatus(), is(HttpStatus.OK_200));
    }

    @Test
    public void shouldReturn200OnExistentRepoAndUnExistentReleases() {
        final Response responseMsg = target().path(path(DONT_HAVE_RELEASES, FORMAT_SVG, NO_STYLE)).request().get();
        assertThat("Should return 200 and default badge if no releases",
                responseMsg.getStatus(), is(HttpStatus.OK_200));
    }

    @Test
    public void shouldDifferFormatPngAndSvg() {
        final Response responseSvg = target().path(path(HAS_RELEASES_ON_GITHUB, FORMAT_SVG, NO_STYLE)).request().get();
        final Response responsePng = target().path(path(HAS_RELEASES_ON_GITHUB, FORMAT_PNG, NO_STYLE)).request().get();
        assertThat("Should create different badges with different formats",
                responseSvg.getMediaType(), not(responsePng.getMediaType()));
    }

    @Test
    public void shouldDifferStyle() {
        final Response responseSvgNoStyle = target()
                .path(path(HAS_RELEASES_ON_GITHUB, FORMAT_SVG, NO_STYLE)).request().get();
        final Response responseSvgFlatStyle = target()
                .path(path(HAS_RELEASES_ON_GITHUB, FORMAT_SVG, STYLE_FLAT)).request().get();

        assertThat("Should return different badges on different styles",
                responseSvgNoStyle.getLength(), not(responseSvgFlatStyle.getLength()));
    }



    private String path(String repoPath, String format, String style) {
        return format("/github/%s/release.%s%s",
                repoPath,
                format,
                isEmpty(style) ? NO_STYLE : format("?style=%s", style));
    }
}
