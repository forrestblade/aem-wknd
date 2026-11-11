package com.forrestblade.aem.wknd.core.models;

import java.util.List;

/**
 * Represents the Byline AEM Component for the WKND Site project.
 */
public interface Byline {

    /**
     * @return a string to display as the name.
     */
    String getName();

    /**
     * Occupations are sorted alphabetically in ascending order.
     *
     * @return a list of occupations.
     */
    List<String> getOccupations();

    /**
     * @return true if the component does not have enough content to display.
     */
    boolean isEmpty();
}
