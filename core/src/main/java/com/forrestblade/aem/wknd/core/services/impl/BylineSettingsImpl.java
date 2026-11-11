package com.forrestblade.aem.wknd.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.forrestblade.aem.wknd.core.services.BylineSettings;

/**
 * OSGi Declarative Services component backing {@link BylineSettings}. The
 * metatype makes it editable in the web console and configurable from
 * ui.config as com.forrestblade.aem.wknd.core.services.impl.BylineSettingsImpl.cfg.json.
 */
@Component(service = BylineSettings.class)
@Designate(ocd = BylineSettingsImpl.Config.class)
public class BylineSettingsImpl implements BylineSettings {

    @ObjectClassDefinition(
            name = "WKND Byline Settings",
            description = "How the Byline component presents a contributor's occupations.")
    public @interface Config {

        @AttributeDefinition(
                name = "Maximum occupations",
                description = "Show at most this many occupations. 0 shows all of them.")
        int maxOccupations() default 0;

        @AttributeDefinition(
                name = "Sort occupations",
                description = "Sort occupations alphabetically. Off keeps the order the author entered.")
        boolean sortOccupations() default true;
    }

    private volatile int maxOccupations;
    private volatile boolean sortOccupations;

    @Activate
    @Modified
    protected void activate(Config config) {
        this.maxOccupations = Math.max(0, config.maxOccupations());
        this.sortOccupations = config.sortOccupations();
    }

    @Override
    public int getMaxOccupations() {
        return maxOccupations;
    }

    @Override
    public boolean isSortOccupations() {
        return sortOccupations;
    }
}
