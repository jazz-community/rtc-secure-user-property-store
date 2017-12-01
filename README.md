# Secure User Property Store for RTC

> TODO: introduction, make a cross reference to git commit picker

## Setup Instructions
The most convenient way to install this service is to download the current stable release from the [Releases](https://github.com/jazz-community/rtc-secure-user-property-store/releases) page.

> TODO: installation and Advanced Properties Configuration instructions

## API Reference
This section contains the API definition for this project. While we try to do our best to keep this accurate, it is always best to have a look at the code in order to make sure that everything is covered.

### base path
You'll see the term `<root>` within the following API doc a few times. It represents the root (or base) path for this service. Assuming that your CCM server is being referred to as `localhost:7443/ccm`, the `<root>` path for this service is the following:

> https://localhost:7443/ccm/service/com.siemens.bt.jazz.services.PersonalTokenService.IPersonalTokenService

### Store new Token to Store
This will add a new entry for the currently logged-in user to the token store. <br>
The implementing application must somehow remember the `key` of a user as it is required to get the `token` of the user.

**Request URL**
> POST <root>/tokenStore

**Example Request Body**
```
{
    "key": "gitlab.com",
    "token": "<this is my secure github accees token>"
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
> GET <root>/tokenStore?key=`key`

**Response Status Code**

 | HTTP Status Code  | When |
 | ----------------- | ---- | 
 | 200               | Found the token associated to the passed in `key`, the value is availabe in the response body (see below) |
 | 401               | The user is not authenticated with RTC Jazz |
 | 404               | The currently logged-in user has no token stored yet for the passed in `key`
 | 500               | An internal server error has occured. Review the CCM log file to find more information | 

**Response Body**
```
{
    "token": "<this is my secure github accees token>"
}
```

## Contributing
Please use the [Issue Tracker](https://github.com/jazz-community/rtc-secure-user-property-store/issues) of this repository to report issues or suggest enhancements.

For general contribution guidelines, please refer to [CONTRIBUTING.md](https://github.com/jazz-community/rtc-secure-user-property-store/blob/master/CONTRIBUTING.md)

## Licensing
Copyright (c) Siemens AG. All rights reserved.<br>
Licensed under the [MIT](LICENSE) License.
