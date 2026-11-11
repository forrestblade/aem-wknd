package com.forrestblade.aem.wknd.core.servlets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import com.forrestblade.aem.wknd.core.models.Byline;

/**
 * Serves a Byline as a downloadable vCard 3.0 at
 * {@code <byline resource>.vcard.vcf}. Bound to the component's resource type
 * rather than a path, which is the AEM as a Cloud Service recommendation, so
 * it works wherever a Byline is authored and inherits the page's permissions.
 */
@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = BylineVCardServlet.RESOURCE_TYPE,
        methods = HttpConstants.METHOD_GET,
        selectors = BylineVCardServlet.SELECTOR,
        extensions = BylineVCardServlet.EXTENSION)
@ServiceDescription("WKND Byline vCard download")
public class BylineVCardServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    static final String RESOURCE_TYPE = "wknd/components/byline";
    static final String SELECTOR = "vcard";
    static final String EXTENSION = "vcf";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        final Byline byline = request.adaptTo(Byline.class);
        if (byline == null || byline.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Byline is not fully authored");
            return;
        }

        final String name = byline.getName().trim();
        final String fileName = name.replaceAll("[^A-Za-z0-9_-]+", "-").replaceAll("^-+|-+$", "") + ".vcf";

        response.setContentType("text/vcard");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.getWriter().write(toVCard(byline));
    }

    static String toVCard(final Byline byline) {
        final StringBuilder vcf = new StringBuilder();
        vcf.append("BEGIN:VCARD\r\n");
        vcf.append("VERSION:3.0\r\n");
        vcf.append("FN:").append(escape(byline.getName().trim())).append("\r\n");
        if (!byline.getOccupations().isEmpty()) {
            vcf.append("TITLE:").append(escape(String.join(", ", byline.getOccupations()))).append("\r\n");
        }
        if (StringUtils.isNotBlank(byline.getImageSrc())) {
            vcf.append("PHOTO;VALUE=URI:").append(byline.getImageSrc()).append("\r\n");
        }
        vcf.append("END:VCARD\r\n");
        return vcf.toString();
    }

    /** RFC 2426 text escaping: backslash, comma, semicolon and newlines. */
    static String escape(final String value) {
        return value
                .replace("\\", "\\\\")
                .replace(",", "\\,")
                .replace(";", "\\;")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n");
    }
}
