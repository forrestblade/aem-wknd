package com.forrestblade.aem.wknd.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Image;
import com.forrestblade.aem.wknd.core.models.Byline;
import com.forrestblade.aem.wknd.core.services.BylineSettings;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {Byline.class, ComponentExporter.class},
        resourceType = {BylineImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class BylineImpl implements Byline {

    protected static final String RESOURCE_TYPE = "wknd/components/byline";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private ModelFactory modelFactory;

    /** Optional so the model still works if the settings service is not yet active. */
    @OSGiService(injectionStrategy = InjectionStrategy.OPTIONAL)
    private BylineSettings settings;

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
        List<String> result = new ArrayList<>(occupations);
        if (settings == null || settings.isSortOccupations()) {
            Collections.sort(result);
        }
        final int max = settings == null ? 0 : settings.getMaxOccupations();
        if (max > 0 && result.size() > max) {
            return new ArrayList<>(result.subList(0, max));
        }
        return result;
    }

    @Override
    public String getImageSrc() {
        final Image componentImage = getImage();
        return componentImage == null ? null : componentImage.getSrc();
    }

    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    @Override
    public boolean isEmpty() {
        final Image componentImage = getImage();

        if (StringUtils.isBlank(name)) {
            // Name is missing, but required
            return true;
        } else if (occupations == null || occupations.isEmpty()) {
            // At least one occupation is required
            return true;
        } else if (componentImage == null || StringUtils.isBlank(componentImage.getSrc())) {
            // A valid image is required
            return true;
        } else {
            // Everything is populated, so this component is not considered empty
            return false;
        }
    }

    /**
     * @return the Core Components Image model for this resource, or null if
     *         one could not be created.
     */
    private Image getImage() {
        return image;
    }
}
