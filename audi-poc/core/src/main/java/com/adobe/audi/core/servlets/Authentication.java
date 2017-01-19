package com.adobe.audi.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author sukanya
 *
 */
@SlingServlet(paths = "/bin/audi/authentication",metatype=true)
public class Authentication extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	private final Logger Log = LoggerFactory.getLogger(Authentication.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		Log.info("Inside Authentication Servlet :");
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		boolean isUser = false;
		String username= "";
		try {

			String resourcePath = "/content/usergenerated/Audi";
			Resource res = resourceResolverFactory.getAdministrativeResourceResolver(null).getResource(resourcePath);
			Node node = res.adaptTo(Node.class);
			NodeIterator childrenNodes = (NodeIterator) node.getNodes();
			
			while (((Iterator) childrenNodes).hasNext()) {
				Node next = childrenNodes.nextNode();

				if (next.getProperty("./email").getString().equals(userId)
						&& next.getProperty("./password").getString().equals(password)) {
					username = next.getProperty("./Name").getString();
					Log.info("Welcome" + " " + next.getProperty("./Name").getString());
					isUser = true;
					break;
				}
			}
			
				writeResponse(response,username,isUser);
				
			

		} catch (Exception e) {
			Log.error("Error while sending request"+e.getMessage());
		}
		Log.info("End of authentication get method :");
	}
	
	
	private void writeResponse(SlingHttpServletResponse response, String username, boolean isUser) throws IOException {

		Log.debug("Inside writeresponse**" + response + username + isUser);

		response.setContentType("application/json");
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("user", username);
			jsonobj.put("isuser", isUser);
		} catch (JSONException e) {
			Log.error("Error while creating JSONObject for writeResponse" + e.getMessage());
		}
		response.getWriter().write(jsonobj.toString());
		response.setStatus(SlingHttpServletResponse.SC_OK);
	}

}