import React from "react";
import CircularProgress from "@mui/material/CircularProgress";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

const Loading = () => {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        width: "100%",
        height: "100%",
      }}
    >
      <CircularProgress sx={{ mt: "auto" }} />
      <Typography component="div" sx={{ mt: "10px", mb: "auto" }}>
        Loading
      </Typography>
    </Box>
  );
};

export default Loading;
