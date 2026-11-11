# aem-wknd

An AEM as a Cloud Service project with a custom **Byline** component, built by working through Adobe's [WKND custom component](https://experienceleague.adobe.com/en/docs/experience-manager-learn/getting-started-wknd-tutorial-develop/project-archetype/custom-component) and [unit testing](https://experienceleague.adobe.com/en/docs/experience-manager-learn/getting-started-wknd-tutorial-develop/project-archetype/unit-testing) chapters on top of a fresh AEM Project Archetype 56 project.

The git history is the point. Each commit is one step of the tutorial, so you can read the component being built from the first `.content.xml` through the last unit test.

## What the Byline component does

It shows an article contributor with a round headshot, their name, and a comma separated list of what they do. Authors fill in a dialog with an image, a name, and any number of occupations. The component sorts the occupations alphabetically and refuses to render until all three fields are present. In the page editor it shows the standard Core Components placeholder instead.

## How it is put together

| Piece | Where | What it does |
|---|---|---|
| Component definition | `ui.apps/.../components/byline/.content.xml` | Registers `wknd/components/byline` and sets `sling:resourceSuperType` to Core Components Image v3, so image authoring and rendering are inherited. |
| Authoring dialog | `.../byline/_cq_dialog/.content.xml` | Keeps the inherited Asset tab, hides Metadata, adds a Properties tab with a required Name textfield and an Occupations multifield. Uses the Sling Resource Merger. |
| Policy dialog | `.../byline/_cq_design_dialog/.content.xml` | Hides the inherited image policy options that make no sense for a headshot. |
| HTL | `.../byline/byline.html` | Adapts the request to the `Byline` model, renders name and joined occupations, includes the image by forcing the Core Components Image resource type on the current resource, and falls back to the placeholder when the model is empty. |
| Model interface | `core/.../models/Byline.java` | Public contract: `getName`, `getOccupations`, `isEmpty`. Package version bumped 1.0 to 1.1.0 for bnd baselining. |
| Sling Model | `core/.../models/impl/BylineImpl.java` | `@ValueMapValue` for name and occupations, `@Self` request plus `@OSGiService ModelFactory` to adapt the resource to the Core Components `Image` model in `@PostConstruct`, and the `isEmpty` rule. |
| Styles | `ui.frontend/src/main/webpack/components/_byline.scss` | 60px circular image floated left, serif name, small uppercase gray occupations. BEM class names. |
| Tests | `core/src/test/.../impl/BylineImplTest.java` | Eight JUnit 5 tests on an AEM Mocks `AemContext`, with a Mockito `ModelFactory` standing in for the Core Components Image. Mock content lives in `BylineImplTest.json`. |

## Where this departs from the tutorial

The tutorial was written against Core Components Image **v2**. Archetype 56 proxies v3, so the Byline extends v3 too. The authoring dialog carries over unchanged. The policy dialog only hides the fields that exist in v3 (`decorative`, `altValueFromDAM`, `titleValueFromDAM`, `displayCaptionPopup`); v2's `features` tab and `disableUuidTracking` are gone.

The archetype's `_variables.scss` does not have the WKND type scale, so the serif stack, `xsmall` and `medium` sizes, and `$gray` were added before the Byline styles.

The archetype ships `junit-addons 1.4` as a test dependency. Nothing uses it, and it pulls in Xerces 2.6.2, which shadows the JDK's SAX parser and makes `ctx.load().json()` fail with `Unable to enable secure processing`. It was removed.

The Java package is `com.forrestblade.aem.wknd` rather than Adobe's `com.adobe.aem.guides.wknd`.

## What was not done

I did not have access to an AEM SDK Quickstart, so the component was never deployed to a running author instance or authored in the page editor. Everything here is verified by the Maven build (compilation, bnd baselining, SCSS compile, content package validation, the AEMaaCS analyser) and by the unit tests. The tutorial page has screenshots of the component authored and rendered.

## Build and test

Needs Java 11 or 17 and Maven 3.9.

    mvn clean install

runs every module, including the tests. To run just the Byline tests

    mvn -pl core test -Dtest=BylineImplTest

With a local AEM author on port 4502

    mvn clean install -PautoInstallSinglePackage

## Modules

Standard archetype layout. `core` is the OSGi bundle, `ui.apps` holds components and clientlibs, `ui.frontend` is the webpack build that produces the site clientlib, `ui.content` and `ui.config` carry sample content and OSGi config, `all` embeds everything into one deployable package and runs the AEMaaCS analyser, `it.tests` and `ui.tests` are integration and Cypress tests, and `dispatcher` is the dispatcher config.
