package com.forrestblade.aem.wknd.core.models;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Represents the Byline AEM Component for the WKND Site project.
 *
 * Also a {@link ComponentExporter}, so the component can be served as JSON
 * through the Sling Model Exporter ({@code byline.model.json}) for headless
 * and SPA consumers.
 *
 * {@code @ProviderType}: only this project implements it, consumers call it.
 * Adding a method is therefore a minor version change, not a major one.
 */
@ProviderType
public interface Byline extends ComponentExporter {

    /**
     * @return a string to display as the name.
     */
    String getName();

    /**
     * Occupations are sorted alphabetically by default. The site-wide
     * BylineSettings service can preserve authored order or limit the list.
     *
     * @return a list of occupations.
     */
    List<String> getOccupations();

    /**
     * @return the rendered image URL from the inherited Core Components Image,
     *         or null if there is no usable image. The HTL does not need this
     *         (it includes the Image component), JSON consumers do.
     */
    String getImageSrc();

    /**
     * @return true if the component does not have enough content to display.
     */
    boolean isEmpty();
}
