import React from "react";
import { createRoot } from "react-dom/client";
import { Provider } from "react-redux";
import { store } from "./app/store";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import "./index.css";
import keycloak from "./keycloak";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import config from "./config/config.json";

const container = document.getElementById("root");
const root = createRoot(container);

root.render(
  // <React.StrictMode>
  <ReactKeycloakProvider
    authClient={keycloak}
    initOptions={{
      redirectUri: config.keylock.redirectUri,
      checkLoginIframe: false,
      onLoad: config.keylock.onLoad,
    }}
  >
    <Provider store={store}>
      <App />
    </Provider>
  </ReactKeycloakProvider>
  // </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
