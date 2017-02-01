package com.adobe.audi.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;


/**
 * @author sukanya
 *
 */
@SlingServlet(paths = "/bin/audi/tagSearch",metatype=true)
public class TagSearch extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private QueryBuilder builder;

	private final Logger Log = LoggerFactory.getLogger(TagSearch.class);
	private Session session;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		Log.info("Inside TagSearch Servlet :");
		String tagId = request.getParameter("tagId");
		tagId = tagId.replaceAll(" ", "");
		tagId = "audi:"+tagId;
		Log.info("Tag Id :"+tagId);
		try {

			String resourcePath = "/content/dam/audi";
			ResourceResolver resourceResolver = request.getResourceResolver();
			//Resource res = resourceResolver.resolve(resourcePath);
			session = resourceResolver.adaptTo(Session.class);
			Log.info("Done!!!");

			// create query description as hash map (simplest way, same as form post)
			Map<String, String> map = new HashMap<String, String>();

			// create query description as hash map (simplest way, same as form post)
			map.put("path","/content/dam/audi");
			map.put("type","dam:Asset");
			map.put("tagid.property", "jcr:content/metadata/cq:tags");
			map.put("tagid",tagId);
			// can be done in map or with Query methods
			map.put("p.offset", "0"); // same as query.setStart(0) below
			map.put("p.limit", "20"); // same as query.setHitsPerPage(20) below
			Query query = builder.createQuery(PredicateGroup.create(map), session);

			query.setStart(0);
			query.setHitsPerPage(20);

			SearchResult result = query.getResult();
			// paging metadata
			int hitsPerPage = result.getHits().size(); // 20 (set above) or lower
			Log.debug("resultsss"+hitsPerPage);
			long totalMatches = result.getTotalMatches();
			long offset = result.getStartIndex();
			long numberOfPages = totalMatches / 20;
			String path="";

			
			// iterating over the results
			for (Hit hit : result.getHits()) {
				 path = hit.getPath();
				Log.debug("Asset Path"+path);
				
		
			}

			//close the session
			session.logout();             
			
			writeResponse(response,path);



		} catch (Exception e) {
			Log.error("Error while sending request"+e.getMessage());
		}
		Log.info("End of authentication get method :");
	}


	private void writeResponse(SlingHttpServletResponse response, String path) throws IOException {

		Log.debug("Inside writeresponse**" + response + path );

		response.setContentType("application/json");
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("path", path);
			
		} catch (JSONException e) {
			Log.error("Error while creating JSONObject for writeResponse" + e.getMessage());
		}
		response.getWriter().write(jsonobj.toString());
		response.setStatus(SlingHttpServletResponse.SC_OK);
	}

}