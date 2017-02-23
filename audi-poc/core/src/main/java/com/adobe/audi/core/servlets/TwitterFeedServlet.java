package com.adobe.audi.core.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@SlingServlet(paths = "/bin/audi/twitterFeed", metatype = true)
@Properties({ @Property(name = "Access Token", value = "823520623116845057-lF46MnCrHRv2QcH8n3x1RHPVOW1rcDS"),
		@Property(name = "Access Token Secret", value = "v8AGMbvXU4ohzHqt5It1SfI941AIuREkaCflKURkgSuOM"),
		@Property(name = "Consumer Key", value = "voGxhi6xvRkrDuUBKsLIht6ZV"),
		@Property(name = "Consumer Secret (API Secret)", value = "OUaa07jx7Ywx4XDK7x2lUnnFtVMRi1jVX8QxWBWJrrf0AJwKL2")
})

public class TwitterFeedServlet extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Reference
	private ConfigurationAdmin configAdmin;
	private final Logger Log = LoggerFactory.getLogger(TwitterFeedServlet.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Log.error("START: TwitterFeedServlet.doGet");
		// Please comment these lines where proxy is not required
		//System.setProperty("http.proxyHost", "10.155.103.176");
		//System.setProperty("http.proxyPort", "6050");

		Configuration config = configAdmin.getConfiguration("com.adobe.audi.core.servlets.TwitterFeedServlet");

		Log.debug("AUDI=======Config" + config);
		Dictionary<String, Object> properties = config.getProperties();

		String accessToken = PropertiesUtil.toString(properties.get("Access Token"),
				"823520623116845057-lF46MnCrHRv2QcH8n3x1RHPVOW1rcDS");
		String accessTokenSecret = PropertiesUtil.toString(properties.get("Access Token Secret"),
				"v8AGMbvXU4ohzHqt5It1SfI941AIuREkaCflKURkgSuOM");
		String consumerKey = PropertiesUtil.toString(properties.get("Consumer Key"), "voGxhi6xvRkrDuUBKsLIht6ZV");
		String consumerSecret = PropertiesUtil.toString(properties.get("Consumer Secret (API Secret)"),
				"OUaa07jx7Ywx4XDK7x2lUnnFtVMRi1jVX8QxWBWJrrf0AJwKL2");

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		String s = request.getParameter("model");
		String[] splited = s.split("\\s+");
		String model = "Audi_Online #" + splited[0];
		// Log.error("AUDI=======model" + model);
		Query query = new Query(model);
		Log.error("AUDI=======query" + query);

		QueryResult result = null;
		String tweet = null;
		String formattedDate = null;

		try {
			result = twitter.search(query);
			// Log.debug("AUDI=======result"+result);
		} catch (TwitterException e) {
			Log.error("AUDI=======stack trace" + e);
		}
		int c = 0;
		for (Status status : result.getTweets()) {
			if (c == 0) {
				tweet = status.getText();
				Log.error("AUDI=======tweet" + tweet);
				Date myDate = status.getCreatedAt();
				formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(myDate);
				c++;
				break;
			}
		}

		response.setContentType("application/json");
		JSONObject obj = new JSONObject();
		try {
			obj.put("date", formattedDate);
			obj.put("tweet", tweet);

		} catch (JSONException e) {
			Log.error("AUDI=======stack trace" + e);
		}
		response.getWriter().write(obj.toString());
		response.setStatus(SlingHttpServletResponse.SC_OK);
		Log.error("AUDI=======Json object" + obj);
		Log.error("END: TwitterFeedServlet.doGet");
	}
}
