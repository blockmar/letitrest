package com.blockmar.letitrest.resolver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.blockmar.letitrest.resolver.impl.PatternUrlResolver;
import com.blockmar.letitrest.servlet.RequestMethod;

public class PatternUrlResolverTest {
	
	@Test
	public void findsRegexpUrlMatch() throws Exception {
		UrlResolver resolver = new PatternUrlResolver();
		resolver.registerUrl("/test/([0-9]+)", null, null, RequestMethod.GET);
		
		UrlResolverResult resolveUrl = resolver.resolveUrl("/test/1234", RequestMethod.GET);
		assertEquals("1234", resolveUrl.getUrlParameters()[0]);
	}
	
	@Test(expected=NotFoundException.class)
	public void throwsExceptionIfNoMatchFound() throws Exception {
		UrlResolver resolver = new PatternUrlResolver();
		resolver.registerUrl("/test", null, null, RequestMethod.GET);
		
		resolver.resolveUrl("/notfound", RequestMethod.GET);
	}
	
	@Test(expected=RequestMethodNotSupportedException.class)
	public void unsupportedRequestMethodThrowsException() throws Exception {
		UrlResolver resolver = new PatternUrlResolver();
		resolver.registerUrl("/get", null, null, RequestMethod.GET);
		
		resolver.resolveUrl("/get", RequestMethod.POST);		
	}
	
	@Test
	public void correctMethodIsReturnedForRequestMethod() throws Exception {
		UrlResolver resolver = new PatternUrlResolver();
		resolver.registerUrl("/test", this, this.getClass().getMethod("getReturn"), RequestMethod.GET);
		resolver.registerUrl("/test", this, this.getClass().getMethod("postReturn"), RequestMethod.POST);
		
		UrlResolverResult resolvGet = resolver.resolveUrl("/test", RequestMethod.GET);
		UrlResolverResult resolvPost = resolver.resolveUrl("/test", RequestMethod.POST);
		
		assertEquals("getReturn", resolvGet.getMethod().getName());
		assertEquals("postReturn", resolvPost.getMethod().getName());
	}
	
	public String getReturn() { return ""; }
	public String postReturn() { return ""; }
}
