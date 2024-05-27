import keycloak from "../../keycloak";

const authHeader = () => {
  const headers = {
    Accept: "*/*",
  };

  headers.Authorization = `Bearer ${keycloak.token}`;

  return headers;
};

export default authHeader;
