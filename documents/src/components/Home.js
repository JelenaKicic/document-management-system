import React from "react";
import { useKeycloak } from "@react-keycloak/web";
import config from "../config/config.json";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { useEffect } from "react";

const Home = () => {
  const { keycloak, initialized } = useKeycloak();
  useEffect(() => {
    if (keycloak.authenticated) keycloak.logout();
  }, [keycloak]);
  return (
    <Box sx={{ mt: "100px", width: "100%" }}>
      <Typography variant="h3" component="div" sx={{ flexGrow: 1 }}>
        Welcome to document managment!
      </Typography>
    </Box>
  );
};

export default Home;
