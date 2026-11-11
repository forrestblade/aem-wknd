# aem-wknd

[![build](https://github.com/forrestblade/aem-wknd/actions/workflows/build.yml/badge.svg)](https://github.com/forrestblade/aem-wknd/actions/workflows/build.yml)

AEM as a Cloud Service project with a custom Byline component and unit tests, from Adobe's WKND tutorial ([component](https://experienceleague.adobe.com/en/docs/experience-manager-learn/getting-started-wknd-tutorial-develop/project-archetype/custom-component), [tests](https://experienceleague.adobe.com/en/docs/experience-manager-learn/getting-started-wknd-tutorial-develop/project-archetype/unit-testing)) on an Archetype 56 project. One commit per tutorial step.

The Byline shows a contributor's headshot, name, and occupations. It extends Core Components Image v3 for the image, adds a dialog for name and occupations, and a Sling Model that sorts the occupations and decides when there is enough content to render.

- `ui.apps/.../components/byline/` component, dialog, policy dialog, HTL
- `core/.../models/Byline.java`, `impl/BylineImpl.java` Sling Model
- `core/src/test/.../BylineImplTest.java` 8 tests, AEM Mocks + Mockito; `BylineImplCoreImageTest.java` 2 tests against the real Core Components Image model, no mocks
- `ui.frontend/.../components/_byline.scss` styles

Differences from the tutorial: Image v3 instead of v2, own Java package, and the archetype's unused `junit-addons` removed because its Xerces breaks AEM Mocks JSON loading.

## Build

Java 11 or 17, Maven 3.9.

    mvn clean install
    mvn -pl core test -Dtest=BylineImplTest
