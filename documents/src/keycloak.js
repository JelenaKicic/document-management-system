import Keycloak from "keycloak-js";
import config from "./config/config.json";

const keycloak = new Keycloak({
  url: config.keylock.url,
  realm: config.keylock.realm,
  clientId: config.keylock.clientId,
});

export default keycloak;
