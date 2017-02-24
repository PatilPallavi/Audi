package com.adobe.audi.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sukanya
 *
 */
@SlingServlet(paths = "/bin/audi/authentication", metatype = true)
public class Authentication extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private final Logger Log = LoggerFactory.getLogger(Authentication.class);

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Log.info("Start of authentication get method");
		response.setContentType("application/json");
		String userEmail = request.getParameter("userId");
		String password = request.getParameter("password");
		boolean isUser = false;
		try {
			String resourcePath = "/content/usergenerated/Audi";
			ResourceResolver adminResourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			Resource res = adminResourceResolver.getResource(resourcePath);
			Node node = res.adaptTo(Node.class);
			NodeIterator childrenNodes = (NodeIterator) node.getNodes();
			JSONObject jsonobj = new JSONObject();
			while (((Iterator) childrenNodes).hasNext()) {
				Node userNode = childrenNodes.nextNode();
				Resource userResource = adminResourceResolver.getResource(userNode.getPath());
				ValueMap valueMap = null;
				if (null != userResource) {
					Log.info("Current User : " + userResource);
					valueMap = userResource.adaptTo(ValueMap.class);
					if (valueMap.get("email", "").equals(userEmail) && valueMap.get("password", "").equals(password)) {
						isUser = true;
						String username = userNode.getProperty("./Name").getString();
						Log.info("Welcome " + username);
						
						jsonobj.put("user", username);
						jsonobj.put("isuser", isUser);
						jsonobj.put("userId", userNode.getName());
						jsonobj.put("userFunctions", userNode.getProperty("./userFunctions").getString());

						NodeIterator userDetailsNodes = userNode.getNodes();
						while (userDetailsNodes.hasNext()) {
							Node nextNode = userDetailsNodes.nextNode();
							if (!nextNode.getName().equals("area_of_interest")) {
								jsonobj.put(nextNode.getName(), getUserDetailsAsJSON(response, nextNode));
							} else if (nextNode.getName().equals("area_of_interest")) {
								PropertyIterator properties = nextNode.getProperties();
								while (properties.hasNext()) {
									Property nextProperty = properties.nextProperty();
									if (nextProperty.getName().equals("area_of_interest")) {
										Value[] values = nextProperty.getValues();
										List<String> valuesAsString = new ArrayList<String>();
										for (Value value : values) {
											valuesAsString.add(value.getString());
										}
										jsonobj.put(nextProperty.getName(), valuesAsString);
										break;
									}
								}
							}
						}
						break;
					}
				}
			}

			if (!isUser) {
				jsonobj.put("response", "no user found");
			}
			
			response.getWriter().write(jsonobj.toString());
			response.setStatus(SlingHttpServletResponse.SC_OK);

		} catch (JSONException e) {
			Log.error("JSONException in Authentication.doGet: " + e);
		} catch (Exception e) {
			Log.error("Error in Authentication.doGet: " + e);
		}
		Log.info("End of authentication get method");
	}

	private JSONObject getUserDetailsAsJSON(SlingHttpServletResponse response, Node node)
			throws RepositoryException, JSONException, IOException {
		JSONObject userDetailsObj = new JSONObject();
		PropertyIterator userDetailsNodeProperties = node.getProperties();
		while (userDetailsNodeProperties.hasNext()) {
			Property nextProperty = userDetailsNodeProperties.nextProperty();
			String nextPropertyName = nextProperty.getName();
			if (null != nextProperty && null != nextPropertyName && !nextPropertyName.equals("jcr:primaryType")) {
				if (nextPropertyName.equals("number_of_children") || nextPropertyName.equals("age")) {
					userDetailsObj.put(nextPropertyName, nextProperty.getValue().getLong());
				} else {
					userDetailsObj.put(nextPropertyName, nextProperty.getValue().getString());
				}
			}
		}
		Log.info("Authentication.getUserDetailsAsJSON - UserDetails = " + userDetailsObj.toString());
		return userDetailsObj;
	}
}