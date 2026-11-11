package com.forrestblade.aem.wknd.core.services;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Site-wide presentation settings for the Byline component, set by OSGi
 * configuration per environment or run mode rather than by each author.
 */
@ProviderType
public interface BylineSettings {

    /**
     * @return the most occupations a byline shows; 0 means all of them.
     */
    int getMaxOccupations();

    /**
     * @return true to sort occupations alphabetically, false to keep the authored order.
     */
    boolean isSortOccupations();
}
