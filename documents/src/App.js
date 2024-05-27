import React from "react";
import "./App.css";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Box from "@mui/material/Box";
import PrivateRoute from "./utilities/PrivateRoute";
import Home from "./components/Home";
import { useKeycloak } from "@react-keycloak/web";
import Navigation from "./components/Navigation";
import Loading from "./components/Loading";
import Documents from "./features/documents/Documents";

// Wrap everything inside KeycloakProvider
const App = () => {
  const { keycloak, initialized } = useKeycloak();

  if (!initialized) {
    return (
      <Box
        sx={{
          height: "100vh",
        }}
      >
        <Loading />
      </Box>
    );
  }

  return (
    <div className="App">
      <Router>
        <Navigation />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route
            path="documents"
            element={
              <PrivateRoute>
                <Documents />
              </PrivateRoute>
            }
          />
        </Routes>
      </Router>
    </div>
  );
};

export default App;
