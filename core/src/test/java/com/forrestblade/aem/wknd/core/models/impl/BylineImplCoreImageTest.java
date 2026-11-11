package com.forrestblade.aem.wknd.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;
import com.forrestblade.aem.wknd.core.models.Byline;
import com.forrestblade.aem.wknd.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * Runs BylineImpl with no mocks: the real Sling ModelFactory and the real
 * Core Components Image v3 model, resolved through the component's
 * sling:resourceSuperType chain, decide whether the image is usable.
 */
@ExtendWith(AemContextExtension.class)
class BylineImplCoreImageTest {

    private static final String BYLINE_RT = "wknd/components/byline";
    private static final String CORE_IMAGE_RT = "core/wcm/components/image/v3/image";
    private static final String HEADSHOT = "/content/dam/wknd/headshot.jpg";

    private final AemContext ctx = AppAemContext.newAemContext();

    private Page page;

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(BylineImpl.class);

        // Component definitions so Sling can walk byline -> Core Image v3 when picking the Image model.
        ctx.create().resource("/apps/" + CORE_IMAGE_RT, "jcr:primaryType", "cq:Component");
        ctx.create().resource("/apps/" + BYLINE_RT,
                "jcr:primaryType", "cq:Component",
                "sling:resourceSuperType", CORE_IMAGE_RT);

        ctx.create().asset(HEADSHOT, 200, 200, "image/jpeg");
        page = ctx.create().page("/content/wknd/us/en/magazine/article");
        ctx.currentPage(page);
    }

    private Byline adapt(Resource resource) {
        ctx.currentResource(resource);
        return ctx.request().adaptTo(Byline.class);
    }

    @Test
    void fullyAuthoredBylineIsNotEmpty() {
        Resource resource = ctx.create().resource(page, "byline",
                "sling:resourceType", BYLINE_RT,
                "name", "Jane Doe",
                "occupations", new String[] { "Photographer", "Blogger" },
                "fileReference", HEADSHOT);

        Byline byline = adapt(resource);

        assertNotNull(byline, "model must construct through the real ModelFactory");
        assertEquals("Jane Doe", byline.getName());
        assertEquals(List.of("Blogger", "Photographer"), byline.getOccupations());
        assertFalse(byline.isEmpty(), "real Core Image resolved a src for the fileReference");
    }

    @Test
    void bylineWithoutImageIsEmpty() {
        Resource resource = ctx.create().resource(page, "byline-no-image",
                "sling:resourceType", BYLINE_RT,
                "name", "Jane Doe",
                "occupations", new String[] { "Photographer" });

        Byline byline = adapt(resource);

        assertNotNull(byline);
        assertTrue(byline.isEmpty(), "no fileReference, so the real Core Image has no src");
    }
}
