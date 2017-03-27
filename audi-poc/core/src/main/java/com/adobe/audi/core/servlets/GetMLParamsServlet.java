package com.adobe.audi.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 579458
 *
 */
@SlingServlet(paths = "/bin/audi/getMLParams", metatype = true)
public class GetMLParamsServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(GetMLParamsServlet.class);
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.error("START: GetMLParamsServlet.doGet method");
		String userEmail = request.getParameter("userEmail");
		JSONObject jsonobj = new JSONObject();
		response.setContentType("application/json");
		boolean validUser = false;
		try {
			if (userEmail != null && userEmail != "") {
				String audiUserPath = "/content/usergenerated/Audi";
				ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
				Resource audiUsersResource = resourceResolver.getResource(audiUserPath);;
				Node audiUsersNode = audiUsersResource.adaptTo(Node.class);
				NodeIterator audiUsers = (NodeIterator) audiUsersNode.getNodes();
				while (((Iterator) audiUsers).hasNext()) {
					Node userNode = audiUsers.nextNode();
					Resource userResource = resourceResolver.getResource(userNode.getPath());
					ValueMap valueMap = null;
					if (null != userResource) {
						valueMap = userResource.adaptTo(ValueMap.class);
						if (valueMap.get("email", "").equals(userEmail)) {
							validUser = true;
							String mlNodePath = "/content/usergenerated/Audi/" + userNode.getName() + "/machine_learning_input";
							log.error("ML Node Path = " + mlNodePath);
							Resource mlResource = resourceResolver.getResource(mlNodePath);
							Node mlNode = mlResource.adaptTo(Node.class);
							
							jsonobj.put("userId", userNode.getName());
							jsonobj.put("Browse_cnt", mlNode.getProperty("browse").getString());
							jsonobj.put("Inform_cnt", mlNode.getProperty("inform").getString());
							jsonobj.put("Configure_cnt", mlNode.getProperty("configure").getString());
							jsonobj.put("Dealer_visit_cnt", mlNode.getProperty("dealer").getString());
							jsonobj.put("Newsletter_cnt", mlNode.getProperty("newsletter").getString());
							
							/*HttpClient client = HttpClientBuilder.create().build();
				            HttpGet getRequest = new HttpGet("http://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver%20BC&destinations=San%20Francisco&sensor=false");
				            getRequest.addHeader("accept", "application/json");
				            HttpResponse response = httpClient.execute(getRequest);*/
						}
					}
				}
			} else {
				log.error("GetMLParamsServlet.doGet: Email ID is Empty");
			} 
			if (!validUser) {
				jsonobj.put("mlerror", "User not found");
			}
		} catch (LoginException e) {
			log.error("LoginException in GetMLParamsServlet.doGet: " + e);
		} catch (ValueFormatException e) {
			log.error("ValueFormatException in GetMLParamsServlet.doGet: " + e);
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException in GetMLParamsServlet.doGet: " + e);
		} catch (RepositoryException e) {
			log.error("RepositoryException in GetMLParamsServlet.doGet: " + e);
		} catch (Exception e) {
			log.error("Error in GetMLParamsServlet.doGet: " + e);
		}
		
		response.getWriter().write(jsonobj.toString());
		response.setStatus(SlingHttpServletResponse.SC_OK);
		log.error("END: GetMLParamsServlet.doGet method");
	}
	
}
