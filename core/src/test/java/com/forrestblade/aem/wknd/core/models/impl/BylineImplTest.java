package com.forrestblade.aem.wknd.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.Constants;

import com.adobe.cq.wcm.core.components.models.Image;
import com.forrestblade.aem.wknd.core.models.Byline;
import com.forrestblade.aem.wknd.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class BylineImplTest {

    private final AemContext ctx = AppAemContext.newAemContext();

    @Mock
    private Image image;

    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(BylineImpl.class);
        ctx.load().json("/com/forrestblade/aem/wknd/core/models/impl/BylineImplTest.json", "/content");

        // BylineImpl.init() asks the ModelFactory for a Core Components Image. Hand it a mock
        // so each test can decide what the image looks like. lenient: some tests override this.
        lenient().when(modelFactory.getModelFromWrappedRequest(eq(ctx.request()), any(Resource.class), eq(Image.class)))
                .thenReturn(image);

        // Highest ranking so @OSGiService ModelFactory in BylineImpl receives the mock.
        ctx.registerService(ModelFactory.class, modelFactory, Constants.SERVICE_RANKING, Integer.MAX_VALUE);
    }

    @Test
    void testGetName() {
        final String expected = "Jane Doe";

        ctx.currentResource("/content/byline");
        Byline byline = ctx.request().adaptTo(Byline.class);

        assertEquals(expected, byline.getName());
    }

    @Test
    void testGetOccupations() {
        // Authored out of order in the JSON; the model must sort them.
        final List<String> expected = List.of("Blogger", "Photographer", "YouTuber");

        ctx.currentResource("/content/byline");
        Byline byline = ctx.request().adaptTo(Byline.class);

        assertEquals(expected, byline.getOccupations());
    }

    @Test
    void testIsEmpty() {
        // A freshly dropped component with nothing authored.
        ctx.currentResource("/content/empty");
        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    void testIsEmpty_WithoutName() {
        ctx.currentResource("/content/without-name");
        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    void testIsEmpty_WithoutOccupations() {
        ctx.currentResource("/content/without-occupations");
        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    void testIsEmpty_WithoutImage() {
        ctx.currentResource("/content/byline");
        // Override the setUp stub: the ModelFactory cannot produce an Image at all.
        lenient().when(modelFactory.getModelFromWrappedRequest(eq(ctx.request()), any(Resource.class), eq(Image.class)))
                .thenReturn(null);
        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    void testIsEmpty_WithoutImageSrc() {
        ctx.currentResource("/content/byline");
        // An Image model exists but resolves to no renderable source.
        when(image.getSrc()).thenReturn("");
        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }
}
