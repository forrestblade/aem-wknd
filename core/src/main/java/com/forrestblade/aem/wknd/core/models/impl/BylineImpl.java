package com.forrestblade.aem.wknd.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;

import com.adobe.cq.wcm.core.components.models.Image;
import com.forrestblade.aem.wknd.core.models.Byline;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {Byline.class},
        resourceType = {BylineImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class BylineImpl implements Byline {

    protected static final String RESOURCE_TYPE = "wknd/components/byline";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private ModelFactory modelFactory;

    @ValueMapValue
    private String name;

    @ValueMapValue
    private List<String> occupations;

    private Image image;

    /**
     * Runs after injection. Adapts this request and resource to the Core
     * Components Image model so the inherited image can be inspected without
     * reimplementing any of its logic.
     */
    @PostConstruct
    private void init() {
        image = modelFactory.getModelFromWrappedRequest(request, request.getResource(), Image.class);
    }

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

    /**
     * @return the Core Components Image model for this resource, or null if
     *         one could not be created.
     */
    private Image getImage() {
        return image;
    }
}
