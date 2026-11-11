package com.forrestblade.aem.wknd.core.models.impl;

import java.util.Collections;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.forrestblade.aem.wknd.core.models.Byline;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {Byline.class},
        resourceType = {BylineImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class BylineImpl implements Byline {

    protected static final String RESOURCE_TYPE = "wknd/components/byline";

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<String> getOccupations() {
        return Collections.emptyList();
    }

    @Override
    public boolean isEmpty() {
        // Nothing is wired up yet, so the component always shows its placeholder.
        return true;
    }
}
