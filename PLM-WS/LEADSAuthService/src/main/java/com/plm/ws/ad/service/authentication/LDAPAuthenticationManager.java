package com.plm.ws.ad.service.authentication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

/*
  C H A N G E    H I S T O R Y
 ================================================================================================================+
 DATE       | REASON        | AUTHOR        | COMMENTS                                                           |
 ================================================================================================================+
  5/21/2018 | ALM #13252    | Emil          | Modified authentication flow to keep web service users session     |
            |               |               | active for the duration of timeout (60 min) for performance reason.|
            |               |               | The session info is stored in AUTHENTICATED_USERS hash map.        |
 ----------------------------------------------------------------------------------------------------------------+
 */

public class LDAPAuthenticationManager implements AuthenticationManager {
	
	private static final Log logger = LogFactory.getLog(LDAPAuthenticationManager.class);

	private static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	static {
		AUTHORITIES.add(new GrantedAuthorityImpl("ROLE_WEBSERVICE"));
	}
	private static final ConcurrentMap<String, LocalDateTime> AUTHENTICATED_USERS = new ConcurrentHashMap<>();

	private LdapTemplate ldapTemplate;
//	private static UsernamePasswordAuthenticationToken userToken;
	private String ldapFilterPath;
	private int ldapAuthSessionDuration;

	//(&(objectClass=user)(samAccountName=sacpd_leadsadmin1)(memberOf=cn=LEADS-Authors,cn=Users,dc=leadstest,dc=cdc,dc=local))
	@Override
	public Authentication authenticate(Authentication auth)	throws AuthenticationException {
		
//		logger.info(String.format("Received User Credentials: login=%s, password=%s", auth.getName(), auth.getCredentials()));

        // insert "unique" id for logging
        MDC.put("id", System.currentTimeMillis());

		String username = auth.getName().toLowerCase();
		String password = auth.getCredentials().toString();

		UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(auth.getName(), auth.getCredentials(), AUTHORITIES);

		boolean authenticationRequired = true;

		// if user has been authenticated
		if (AUTHENTICATED_USERS.containsKey(username)) {

			LocalDateTime authDateTime = AUTHENTICATED_USERS.get(username);
			LocalDateTime now = LocalDateTime.now();

			// check if user has authenticated within 1 hour
			if (now.minusMinutes(ldapAuthSessionDuration).compareTo(authDateTime) <= 0) {

				//logger.info(String.format("User %s is already authenticated. Session opened on %s", username,
				//		authDateTime.format(DATE_TIME_FORMATTER)));

				authenticationRequired = false;

			} else {
				logger.info(String.format("Session expired for user %s. Last session opened on %s", username,
						authDateTime.format(DATE_TIME_FORMATTER)));

				AUTHENTICATED_USERS.remove(username);
			}
		}

		if (authenticationRequired) {

			AndFilter filter = new AndFilter();
			filter.and(new EqualsFilter("objectclass", "user"));
			filter.and(new EqualsFilter("sAMAccountName", auth.getName()));

			boolean authenticated = false;
			try {
				//logger.info(String.format("Authenticating user %s", auth.getName()));
				authenticated = ldapTemplate.authenticate("", filter.toString(), password);
			} catch (Exception e) {
				logger.info(String.format("Authentication attempt failed for user %s", username));
				throw e;
			}

			if (!authenticated) {
				logger.info(String.format("Authentication attempt failed for user %s", username));
				QName authenticationResponse_QNAME = new QName("http://www.plm/ws/mtom/data", "ParoleeDataResponse");
				throw new com.sun.xml.wss.impl.WssSoapFaultException(authenticationResponse_QNAME, "Username or password is incorrect", "", null);
			} else {

				//filter.and(new EqualsFilter("memberOf", "cn=LEADS-Downloaders,cn=Users,DC=parole,DC=cdc,DC=plm"));
				filter.and(new EqualsFilter("memberOf", ldapFilterPath));

				SearchControls searchControls = new SearchControls();
				searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				List users = ldapTemplate.search("", filter.toString(), searchControls, new UserAttributesMapper());

				if (users.size() == 0) {
					logger.info(String.format("Insufficient access permissions for user %s", username));
					throw new InsufficientAuthenticationException("Insufficient access permissions");
				}
			}

			logger.info(String.format("Session opened for user %s on %s", username,
					LocalDateTime.now().format(DATE_TIME_FORMATTER)));
			// store authenticated user information in cache to prevent extra authentications for the duration of 1 hour
			AUTHENTICATED_USERS.put(username, LocalDateTime.now());

		}
		
		return userToken;
	}

	public void setLdapAuthSessionDuration(int ldapAuthSessionDuration) {
		this.ldapAuthSessionDuration = ldapAuthSessionDuration;
	}

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
	    this.ldapTemplate = ldapTemplate;
    }

    public void setLdapFilterPath(String ldapFilterPath) {
        this.ldapFilterPath = ldapFilterPath;
    }

	private class UserAttributesMapper implements AttributesMapper {
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException {
			return attributes.get("cn").get();
		}
	}
	
//	public static UsernamePasswordAuthenticationToken getUserToken(){
//		return userToken;
//	}

}
