# Clients App

This is a frontend React application for managing user accounts in the Document Management System (DMS).

## Setup

1. Clone this repository.
2. Add a `config.json` file to `src/config` with the following JSON data:

```json
{
  "keylock": {
    "url": "",
    "realm": "",
    "clientId": "",
    "onLoad": "",
    "redirectUri": "",
    "admin_client": "",
    "admin_client_secret": ""
  },
  "api": "",
  "auth_url": ""
}
```

3. Ensure to add the actual configuration details required for your setup.

4. Run ```npm install to install``` dependencies.
5. Run ```npm start``` to start the development server.


## Usage
Once the setup is complete, you can access the application in your browser at http://localhost:3000.