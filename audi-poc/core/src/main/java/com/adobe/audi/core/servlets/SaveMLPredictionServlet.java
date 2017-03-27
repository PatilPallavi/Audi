package com.adobe.audi.core.servlets;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 579458
 *
 */
@SlingServlet(paths = "/bin/audi/saveMLPrediction", metatype = true)
public class SaveMLPredictionServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(SaveMLPredictionServlet.class);
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.error("START: SaveMLPredictionServlet.doGet method");
		String userId = request.getParameter("userId");
		String recommendation = request.getParameter("recommendation");
		JSONObject jsonobj = new JSONObject();
		response.setContentType("application/json");
		try {
			if(userId != null && userId != "" && recommendation != null && recommendation != "") {
				ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
				String resourcePath = "/content/usergenerated/Audi/"+userId+"/machine_learning_rec";
				Resource resource = resourceResolver.getResource(resourcePath);
				if (null != resource) {
	        		Node userNode = resource.adaptTo(Node.class);
	        		log.error("\nRecommendation before = " + userNode.getProperty("predictiveAction").getString());
	        		userNode.setProperty("predictiveAction", recommendation);
	        		resourceResolver.commit();
	        		log.error("\nRecommendation after = " + userNode.getProperty("predictiveAction").getString());
	        		jsonobj.put("success", "Recommendation Saved");
	        	}
			} else {
				log.error("Invalid input: UserID = " + userId + " Recommendation = " + recommendation);
				jsonobj.put("error", "Error while saving recommendation");
			}
		} catch (LoginException e) {
			log.error("LoginException in SaveMLPredictionServlet.doGet: " + e);
		} catch (ValueFormatException e) {
			log.error("ValueFormatException in SaveMLPredictionServlet.doGet: " + e);
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException in SaveMLPredictionServlet.doGet: " + e);
		} catch (RepositoryException e) {
			log.error("RepositoryException in SaveMLPredictionServlet.doGet: " + e);
		} catch (Exception e) {
			log.error("Error in SaveMLPredictionServlet.doGet: " + e);
		}
		response.getWriter().write(jsonobj.toString());
		response.setStatus(SlingHttpServletResponse.SC_OK);
		log.error("END: SaveMLPredictionServlet.doGet method");
	}

}
