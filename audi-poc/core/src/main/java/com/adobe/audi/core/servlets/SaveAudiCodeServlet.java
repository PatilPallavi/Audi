package com.adobe.audi.core.servlets;

import java.io.IOException;

import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 579458
 *
 */
@SlingServlet(paths = "/bin/audi/saveAudiCode",metatype=true)
public class SaveAudiCodeServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(UserFunctionsServlet.class);
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.error("START: SaveAudiCodeServlet.doGet method");
		String userId = request.getParameter("userId");
		String audiCode = request.getParameter("audiCode");
		log.error("\nuserId = " + userId + "  audiCode = " + audiCode);
		if(userId != null) {
			String resourcePath = "/content/usergenerated/Audi/"+userId+"/stored_settings";
			try {
				ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
				Resource resource = resourceResolver.getResource(resourcePath);
				Node userNode = resource.adaptTo(Node.class);
				if(audiCode != null) {
					userNode.setProperty("configured_cars", audiCode);
				} else {
					userNode.setProperty("configured_cars", "");
				}
				resourceResolver.commit();
				log.error("\nConfigured Cars for " + userId + " = " + userNode.getProperty("configured_cars").getString());
			} catch (LoginException e) {
				log.error("LoginException in SaveAudiCodeServlet.doGet: " + e);
			} catch (Exception e) {
				log.error("Error in SaveAudiCodeServlet.doGet: " + e);
			}
		} else {
			log.error("Error in SaveAudiCodeServlet.doGet: Please provide userID");
		}
		log.error("END: SaveAudiCodeServlet.doGet method");
	}

}
