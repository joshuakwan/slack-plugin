/**
 * 
 */
package jenkins.plugins.slack;

import hudson.ProxyConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;

/**
 * @author joshuaguan
 * 
 */
public class UrlObfuscatorService {

	private static final Logger logger = Logger
			.getLogger(UrlObfuscatorService.class.getName());

	private String serviceUrl;
	private String serviceToken;

	public UrlObfuscatorService(String serviceUrl, String serviceToken) {
		this.serviceUrl = serviceUrl;
		this.serviceToken = serviceToken;
	}

	public String getObfuscatedUrl(final String url) {
		HttpClient client = getHttpClient();
		PostMethod post = new PostMethod(this.serviceUrl);

		post.addParameter("api_key", this.serviceToken);
		post.addParameter("url", url);
		post.getParams().setContentCharset("UTF-8");

		String response = null;
		try {
			logger.log(Level.INFO, "Posting " + url + " to " + this.serviceUrl);
			int responseCode = client.executeMethod(post);
			response = post.getResponseBodyAsString();
			if (responseCode != HttpStatus.SC_OK) {
				logger.log(Level.WARNING,
						"URL Obfuscator post may have failed. Response: "
								+ response);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error posting to URL Obfuscator", e);
		} finally {
			logger.info("URL Obfuscator posting succeeded");
			post.releaseConnection();
		}

		if (response != null) {
			logger.info("URL Obfuscator response: " + response);
			JSONObject json = new JSONObject(response);
			return json.getString("url");
		} else {
			return null;
		}
	}

	private HttpClient getHttpClient() {
		HttpClient client = new HttpClient();
		if (Jenkins.getInstance() != null) {
			ProxyConfiguration proxy = Jenkins.getInstance().proxy;
			if (proxy != null) {
				client.getHostConfiguration().setProxy(proxy.name, proxy.port);
				String username = proxy.getUserName();
				String password = proxy.getPassword();
				// Consider it to be passed if username specified. Sufficient?
				if (username != null && !"".equals(username.trim())) {
					logger.info("Using proxy authentication (user=" + username
							+ ")");
					// http://hc.apache.org/httpclient-3.x/authentication.html#Proxy_Authentication
					// and
					// http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/examples/BasicAuthenticationExample.java?view=markup
					client.getState()
							.setProxyCredentials(
									AuthScope.ANY,
									new UsernamePasswordCredentials(username,
											password));
				}
			}
		}
		return client;
	}
}
