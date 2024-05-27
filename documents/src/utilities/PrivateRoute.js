import { useKeycloak } from "@react-keycloak/web";
import React from "react";
import { Navigate } from "react-router-dom";

const PrivateRoute = ({ children }) => {
  const { keycloak } = useKeycloak();

  return keycloak && keycloak.authenticated ? children : <Navigate to="/" />;
};

export default PrivateRoute;
