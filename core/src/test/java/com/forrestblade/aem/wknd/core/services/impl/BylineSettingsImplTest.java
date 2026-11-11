package com.forrestblade.aem.wknd.core.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.forrestblade.aem.wknd.core.services.BylineSettings;
import com.forrestblade.aem.wknd.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class BylineSettingsImplTest {

    private final AemContext ctx = AppAemContext.newAemContext();

    @Test
    void defaultsWhenNoConfigurationIsPresent() {
        ctx.registerInjectActivateService(new BylineSettingsImpl());

        BylineSettings settings = ctx.getService(BylineSettings.class);
        assertNotNull(settings);
        assertEquals(0, settings.getMaxOccupations());
        assertTrue(settings.isSortOccupations());
    }

    @Test
    void readsConfiguration() {
        ctx.registerInjectActivateService(new BylineSettingsImpl(), "maxOccupations", 2, "sortOccupations", false);

        BylineSettings settings = ctx.getService(BylineSettings.class);
        assertEquals(2, settings.getMaxOccupations());
        assertFalse(settings.isSortOccupations());
    }

    @Test
    void clampsNegativeMaxToZero() {
        ctx.registerInjectActivateService(new BylineSettingsImpl(), "maxOccupations", -5);

        assertEquals(0, ctx.getService(BylineSettings.class).getMaxOccupations());
    }
}
