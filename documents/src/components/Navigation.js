import React from "react";
import { useKeycloak } from "@react-keycloak/web";
import PropTypes from "prop-types";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import HomeIcon from "@mui/icons-material/Home";
import { useNavigate } from "react-router-dom";

const Navigation = () => {
  const { keycloak, initialized } = useKeycloak();
  let navigate = useNavigate();

  return (
    <AppBar component="nav">
      <Toolbar>
        <IconButton
          aria-label="add"
          onClick={() => {
            navigate("/documents");
          }}
        >
          <HomeIcon color="secondary" fontSize="large" />
        </IconButton>
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, display: { xs: "none", sm: "block" } }}
        >
          Documents managment
        </Typography>
        <Box sx={{ display: { xs: "none", sm: "block" } }}>
          {!keycloak.authenticated && (
            <Button onClick={() => keycloak.login()} sx={{ color: "#fff" }}>
              Login
            </Button>
          )}
          {!!keycloak.authenticated && (
            <Button onClick={() => keycloak.logout()} sx={{ color: "#fff" }}>
              Logout
            </Button>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navigation;
