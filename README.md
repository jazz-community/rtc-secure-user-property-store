# Secure User Property Store for RTC
This plugin is used to store additional properties that are associated to an RTC user account in a secure manner. It is used by the [RTC Git Connector](https://github.com/jazz-community/rtc-git-connector) project to store the _Personal Access Token_ that is needed to access GitLab data. The service uses strong, tamper-proof cryptography to make sure that stored information is safe. It uses a built-in mechanism of the Jazz Platform the information directly into the database.

## Setup Instructions
The most convenient way to install this service is to download the current stable release from the [Releases](https://github.com/jazz-community/rtc-secure-user-property-store/releases) page. If you not already know, head over to the [RTC Git Connector](https://github.com/jazz-community/rtc-git-connector) page to learn how to install RTC plug-ins (it's always the same procedure).

Once the plug-in is installed and the server rebooted, open the **Advanced Properties** page of your server and scroll down to the following section that allows to configure this service. Specify a _16 digit_ private key that is used for encrpytion.
![Work Item Bulk Mover Version 1.0.0 demonstration](https://github.com/jazz-community/rtc-secure-user-property-store/blob/master/private_key_setup_in_AdvancedProperties.PNG)

Important notes:
- Without providing a private key, the service will not wwork
- Do not share this key with anyone except a secure password manager, you may want to keep it for restore scenarios
- Make sure that the private key is absolutely random so that no one could guess it

## API Reference
This section contains the API definition for this project. While we try to do our best to keep this accurate, it is always best to have a look at the code in order to make sure that everything is covered.

### base path
You'll see the term `BASE` within the following API doc a few times. It represents the root (or base) path for this service. Assuming that your CCM server is being referred to as `localhost:7443/ccm`, the `BASE` path for this service is the following:

> https://localhost:7443/ccm/service/com.siemens.bt.jazz.services.PersonalTokenService.IPersonalTokenService

### Store new Token to Store
This will add a new entry for the currently logged-in user to the token store. <br>
The implementing application must somehow remember the `key` of a user as it is required to get the `token` of the user.

**Request URL**
> POST `BASE`/tokenStore

**Example Request Body**
```javascript
{
    "key": "gitlab.com",
    "token": "<this placeholder would hold my secret personal access token for GitLab (or any other service)>"
}
```

**Response Status Code**

 | HTTP Status Code  | When |
 | ----------------- | ---- |
 | 201               | The token was successfully added to the database |
 | 400               | Bad Request. Your request must have a body like the one described in _Example Request_. Make sure that it is sent along the network as `application/json` |
 | 401               | The user is not authenticated with RTC Jazz |
 | 500               | An internal server error has occured. Review the CCM log file to find more information | 

### Read token from Store
Read the token associated to the passed in `key` for the currently logged-in user.

**Request URL**
> GET `BASE`/tokenStore?key=`gitlab.com`

**Response Status Code**

 | HTTP Status Code  | When |
 | ----------------- | ---- | 
 | 200               | Found the token associated to the passed in `key`, the value is availabe in the response body (see below) |
 | 401               | The user is not authenticated with RTC Jazz |
 | 404               | The currently logged-in user has no token stored yet for the passed in `key`
 | 500               | An internal server error has occured. Review the CCM log file to find more information | 

**Response Body**
```javascript
{
    "token": "<this is my secure GitLab accees token>"
}
```

## Contributing
Please use the [Issue Tracker](https://github.com/jazz-community/rtc-secure-user-property-store/issues) of this repository to report issues or suggest enhancements.

For general contribution guidelines, please refer to [CONTRIBUTING.md](https://github.com/jazz-community/rtc-secure-user-property-store/blob/master/CONTRIBUTING.md)

## Licensing
Copyright (c) Siemens AG. All rights reserved.<br>
Licensed under the [MIT](LICENSE) License.
