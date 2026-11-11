package com.forrestblade.aem.wknd.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;
import com.forrestblade.aem.wknd.core.models.impl.BylineImpl;
import com.forrestblade.aem.wknd.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class BylineVCardServletTest {

    private static final String BYLINE_RT = "wknd/components/byline";
    private static final String CORE_IMAGE_RT = "core/wcm/components/image/v3/image";
    private static final String HEADSHOT = "/content/dam/wknd/headshot.jpg";

    private final AemContext ctx = AppAemContext.newAemContext();
    private final BylineVCardServlet servlet = new BylineVCardServlet();

    private Page page;

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(BylineImpl.class);
        ctx.create().resource("/apps/" + CORE_IMAGE_RT, "jcr:primaryType", "cq:Component");
        ctx.create().resource("/apps/" + BYLINE_RT, "jcr:primaryType", "cq:Component", "sling:resourceSuperType", CORE_IMAGE_RT);
        ctx.create().asset(HEADSHOT, 200, 200, "image/jpeg");
        page = ctx.create().page("/content/wknd/us/en/magazine/article");
        ctx.currentPage(page);
        ctx.requestPathInfo().setSelectorString("vcard");
        ctx.requestPathInfo().setExtension("vcf");
        ctx.request().setScheme("https");
        ctx.request().setServerName("www.example.com");
        ctx.request().setServerPort(443);
    }

    @Test
    void writesAVCardForAnAuthoredByline() throws Exception {
        Resource byline = ctx.create().resource(page, "byline",
                "sling:resourceType", BYLINE_RT,
                "name", "Stacey Roswells, Jr.",
                "occupations", new String[] { "Photographer", "Blogger" },
                "fileReference", HEADSHOT);
        ctx.currentResource(byline);

        servlet.doGet(ctx.request(), ctx.response());

        assertEquals(HttpServletResponse.SC_OK, ctx.response().getStatus());
        assertEquals("text/vcard;charset=UTF-8", ctx.response().getContentType());
        assertEquals("attachment; filename=\"Stacey-Roswells-Jr.vcf\"", ctx.response().getHeader("Content-Disposition"));
        assertEquals(
                "BEGIN:VCARD\r\n"
                        + "VERSION:3.0\r\n"
                        + "FN:Stacey Roswells\\, Jr.\r\n"
                        + "N:;;;;\r\n"
                        + "TITLE:Blogger\\, Photographer\r\n"
                        + "PHOTO;VALUE=URI:https://www.example.com/content/wknd/us/en/magazine/article/_jcr_content/byline.coreimg.jpeg\r\n"
                        + "END:VCARD\r\n",
                ctx.response().getOutputAsString());
    }

    @Test
    void returns404WhenTheBylineIsNotFullyAuthored() throws Exception {
        Resource byline = ctx.create().resource(page, "byline", "sling:resourceType", BYLINE_RT, "name", "No Image");
        ctx.currentResource(byline);

        servlet.doGet(ctx.request(), ctx.response());

        assertEquals(HttpServletResponse.SC_NOT_FOUND, ctx.response().getStatus());
    }

    @Test
    void escapesVCardSpecialCharacters() {
        assertEquals("a\\\\b\\,c\\;d\\ne", BylineVCardServlet.escape("a\\b,c;d\ne"));
        assertEquals("a\\nb\\nc", BylineVCardServlet.escape("a\r\nb\rc"));
    }

    @Test
    void usesAFallbackFilenameForNonAsciiNames() throws Exception {
        Resource byline = ctx.create().resource(page, "byline",
                "sling:resourceType", BYLINE_RT,
                "name", "李明",
                "occupations", new String[] { "Photographer" },
                "fileReference", HEADSHOT);
        ctx.currentResource(byline);

        servlet.doGet(ctx.request(), ctx.response());

        assertEquals(HttpServletResponse.SC_OK, ctx.response().getStatus());
        assertEquals("attachment; filename=\"byline.vcf\"", ctx.response().getHeader("Content-Disposition"));
    }
}
