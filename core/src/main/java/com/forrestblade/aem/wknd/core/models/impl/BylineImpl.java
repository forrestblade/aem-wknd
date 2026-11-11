package com.forrestblade.aem.wknd.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.forrestblade.aem.wknd.core.models.Byline;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {Byline.class},
        resourceType = {BylineImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class BylineImpl implements Byline {

    protected static final String RESOURCE_TYPE = "wknd/components/byline";

    @ValueMapValue
    private String name;

    @ValueMapValue
    private List<String> occupations;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getOccupations() {
        if (occupations == null) {
            return Collections.emptyList();
        }
        List<String> sorted = new ArrayList<>(occupations);
        Collections.sort(sorted);
        return sorted;
    }

    @Override
    public boolean isEmpty() {
        // Nothing is wired up yet, so the component always shows its placeholder.
        return true;
    }
}
