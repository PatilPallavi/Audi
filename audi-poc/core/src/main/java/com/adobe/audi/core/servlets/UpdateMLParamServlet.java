package com.adobe.audi.core.servlets;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
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
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 579458
 *
 */
@SlingServlet(paths = "/bin/audi/updateMLParam", metatype = true)
public class UpdateMLParamServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(UpdateMLParamServlet.class);
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.error("START: UpdateMLParamServlet.doGet method");
		String userId = request.getParameter("userId");
		String mlparam = request.getParameter("mlparam");
		JSONObject jsonobj = new JSONObject();
		response.setContentType("application/json");
		
		try {
			if(userId != null && userId != "" && mlparam != null && mlparam != "") {
				ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
				String resourcePath = "/content/usergenerated/Audi/"+userId+"/machine_learning_input";
				Resource resource = resourceResolver.getResource(resourcePath);
				if (null != resource) {
	        		Node userNode = resource.adaptTo(Node.class);
	        		String paramStringVal = userNode.getProperty(mlparam).getString();
	        		if(paramStringVal != null && paramStringVal != "") {
	        			userNode.setProperty(mlparam, Long.parseLong(paramStringVal) + 1);
					} else {
						userNode.setProperty(mlparam, 1);
					}
	        		resourceResolver.commit();
	        		log.error("\nParam value after = " + userNode.getProperty(mlparam).getString());
	        		jsonobj.put("success", "ML Parameter Updated");
	        	} else {
	        		log.error("Resource not found for the path");
	        	}
			} else {
				log.error("Invalid input: UserID = " + userId + " ML Parameter = " + mlparam);
				jsonobj.put("error", "Error while saving ML Parameter");
			}
		} catch (JSONException e) {
			log.error("JSONException in UpdateMLParamServlet.doGet: " + e);
		} catch (LoginException e) {
			log.error("LoginException in UpdateMLParamServlet.doGet: " + e);
		} catch (ValueFormatException e) {
			log.error("ValueFormatException in UpdateMLParamServlet.doGet: " + e);
		} catch (VersionException e) {
			log.error("VersionException in UpdateMLParamServlet.doGet: " + e);
		} catch (LockException e) {
			log.error("LockException in UpdateMLParamServlet.doGet: " + e);
		} catch (ConstraintViolationException e) {
			log.error("ConstraintViolationException in UpdateMLParamServlet.doGet: " + e);
		} catch (RepositoryException e) {
			log.error("RepositoryException in UpdateMLParamServlet.doGet: " + e);
		} catch (Exception e) {
			log.error("Error in SaveMLPredictionServlet.doGet: " + e);
		}
		
		response.getWriter().write(jsonobj.toString());
		response.setStatus(SlingHttpServletResponse.SC_OK);
		log.error("END: UpdateMLParamServlet.doGet method");
	}

}
