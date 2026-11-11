package com.forrestblade.aem.wknd.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.models.jacksonexporter.impl.JacksonExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.export.json.ComponentExporter;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forrestblade.aem.wknd.core.models.Byline;
import com.forrestblade.aem.wknd.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The JSON a headless or SPA client gets from byline.model.json, produced by
 * the real Sling Models Jackson exporter with the real Core Components Image.
 */
@ExtendWith(AemContextExtension.class)
class BylineImplExportTest {

    private static final String BYLINE_RT = "wknd/components/byline";
    private static final String CORE_IMAGE_RT = "core/wcm/components/image/v3/image";
    private static final String HEADSHOT = "/content/dam/wknd/headshot.jpg";

    private final AemContext ctx = AppAemContext.newAemContext();

    private Resource byline;

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(BylineImpl.class);
        ctx.registerInjectActivateService(new JacksonExporter());

        ctx.create().resource("/apps/" + CORE_IMAGE_RT, "jcr:primaryType", "cq:Component");
        ctx.create().resource("/apps/" + BYLINE_RT, "jcr:primaryType", "cq:Component", "sling:resourceSuperType", CORE_IMAGE_RT);
        ctx.create().asset(HEADSHOT, 200, 200, "image/jpeg");

        Page page = ctx.create().page("/content/wknd/us/en/magazine/article");
        byline = ctx.create().resource(page, "byline",
                "sling:resourceType", BYLINE_RT,
                "name", "Jane Doe",
                "occupations", new String[] { "Photographer", "Blogger" },
                "fileReference", HEADSHOT);
        ctx.currentPage(page);
        ctx.currentResource(byline);
    }

    @Test
    void adaptsAsComponentExporterWithItsResourceTypeAsExportedType() {
        ComponentExporter exporter = ctx.request().adaptTo(ComponentExporter.class);

        assertNotNull(exporter);
        assertTrue(exporter instanceof Byline);
        assertEquals(BYLINE_RT, exporter.getExportedType());
    }

    @Test
    void exportsNameOccupationsImageSrcAndType() throws Exception {
        Byline model = ctx.request().adaptTo(Byline.class);
        assertNotNull(model);

        ModelFactory modelFactory = ctx.getService(ModelFactory.class);
        String json = modelFactory.exportModel(model, "jackson", String.class, Collections.emptyMap());
        JsonNode node = new ObjectMapper().readTree(json);

        assertEquals("Jane Doe", node.get("name").asText());
        assertEquals(List.of("Blogger", "Photographer"), List.of(node.get("occupations").get(0).asText(), node.get("occupations").get(1).asText()));
        assertEquals("/content/wknd/us/en/magazine/article/_jcr_content/byline.coreimg.jpeg", node.get("imageSrc").asText());
        assertEquals(BYLINE_RT, node.get(":type").asText());
        assertFalse(node.get("empty").asBoolean());
    }
}
