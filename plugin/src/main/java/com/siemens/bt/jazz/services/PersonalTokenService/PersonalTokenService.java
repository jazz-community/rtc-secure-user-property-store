package com.siemens.bt.jazz.services.PersonalTokenService;

import com.ibm.team.jfs.app.http.util.HttpConstants.HttpMethod;
import com.siemens.bt.jazz.services.base.BaseService;
import com.siemens.bt.jazz.services.base.router.factory.RestFactory;
import com.siemens.bt.jazz.services.PersonalTokenService.builder.TokenWriterService;
import com.siemens.bt.jazz.services.PersonalTokenService.builder.TokenReaderService;

/**
 * Entry point for the Service, called by the Jazz class loader.
 * 
 * <p>This class must be implemented for enabling plug-ins to run inside Jazz. The implemented interface corresponds to
 * the component in {@code plugin.xml}, and this service is therefore the provided service by the interface.</p>
 * 
 */
public class PersonalTokenService extends BaseService implements IPersonalTokenService {
	/**
	 * Constructs a new Service
	 * <p>This constructor is only called by the Jazz class loader.</p>
	 */
	public PersonalTokenService() {
		super();
		router.addService(HttpMethod.GET, "tokenStore", new RestFactory(TokenReaderService.class));
		router.addService(HttpMethod.POST, "tokenStore", new RestFactory(TokenWriterService.class));
	}
}
