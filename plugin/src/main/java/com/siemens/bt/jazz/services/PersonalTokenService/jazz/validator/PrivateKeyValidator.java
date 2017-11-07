package com.siemens.bt.jazz.services.PersonalTokenService.jazz.validator;

import com.ibm.team.repository.service.IConfigurationPropertyValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.util.HashMap;
import java.util.Map;

public class PrivateKeyValidator implements IConfigurationPropertyValidator {
    private final static String PRIVATE_KEY_PROP = "privateKey";
    private final static int KEY_LENGTH = 16;
    private final static String PLUGIN_ID = "com.siemens.bt.jazz.services.PersonalTokenService";
    private final static String KEY_LENGTH_ERROR_MSG = "Private Key must have a length of exsactly 16 digits!";

    public Map<String, IStatus> validateProperties(Map<String, Object> properties, boolean fast) {
        String el = (String)properties.get(PRIVATE_KEY_PROP);
        Map<String, IStatus> propStatus = new HashMap(1);
        IStatus s;
        if(el.length() == KEY_LENGTH) {
            s = Status.OK_STATUS;
        } else {
            s = new Status(4, PLUGIN_ID, KEY_LENGTH_ERROR_MSG);
        }
        propStatus.put(PRIVATE_KEY_PROP, s);
        return propStatus;
    }
}
