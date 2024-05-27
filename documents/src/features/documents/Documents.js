import React from "react";
import TreeView from "@mui/lab/TreeView";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import TreeItem from "@mui/lab/TreeItem";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  createDirectory,
  deleteDirectory,
  deleteFile,
  downloadFile,
  fetchFiles,
  moveFile,
  selectError,
  selectFiles,
  selectLogs,
  fetchLogs,
  updateFile,
  uploadFile,
} from "./DocumentsSlice";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";
import { saveAs } from "file-saver";
import TextField from "@mui/material/TextField";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const Documents = () => {
  const [openErrorSnackbar, setOpenErrorSnackbar] = React.useState(false);

  const handleDirectorySelect = (event, nodeIds) => {
    setSelectedDocument(nodeIds);
  };

  const [selectedDocument, setSelectedDocument] = React.useState("");
  const [newDirectoryName, setNewDirectoryName] = React.useState("");
  const [newFileLocation, setNewFileLocation] = React.useState("");

  const root = useSelector(selectFiles);
  const logs = useSelector(selectLogs);

  const error = useSelector(selectError);

  const dispatch = useDispatch();

  const handleDeleteFile = () => {
    dispatch(deleteFile(selectedDocument)).then((response) => {
      dispatch(fetchFiles());
      if (response.error.message) {
        setOpenErrorSnackbar(true);
      }
    });
  };

  const handleDeleteDirectory = () => {
    dispatch(deleteDirectory({ path: selectedDocument })).then((response) => {
      dispatch(fetchFiles());
      if (response.error.message) {
        setOpenErrorSnackbar(true);
      }
    });
  };

  const handleCreateDirectory = () => {
    dispatch(
      createDirectory({ path: selectedDocument, name: newDirectoryName })
    ).then((response) => {
      dispatch(fetchFiles());
      if (response.error.message) {
        setOpenErrorSnackbar(true);
      }
    });
  };

  const handleMoveFile = () => {
    dispatch(
      moveFile({ path: selectedDocument, newPath: newFileLocation })
    ).then((response) => {
      dispatch(fetchFiles());
      if (response.error.message) {
        setOpenErrorSnackbar(true);
      }
    });
  };

  const handleUploadFile = (event) => {
    if (event.target.value) {
      let formData = new FormData();
      formData.append("file", event.target.files[0]);

      dispatch(uploadFile({ path: selectedDocument, formData: formData })).then(
        (response) => {
          dispatch(fetchFiles());
          if (response.error.message) {
            setOpenErrorSnackbar(true);
          }
        }
      );
    }
  };

  const handleDownloadFile = (event) => {
    dispatch(downloadFile({ path: selectedDocument })).then((response) => {
      if (response.error.message) {
        setOpenErrorSnackbar(true);
      }

      let documentPath = selectedDocument.split("/");

      var blob = new Blob(response);
      var url = URL.createObjectURL(blob);
      window.open(url);

      // var downloadURL = window.URL.createObjectURL(response);
      // var link = document.createElement("a");
      // link.href = downloadURL;
      // link.download = documentPath[documentPath.length - 1];
      // link.click();
    });
  };

  const handleUpdateFile = (event) => {
    if (event.target.value) {
      let formData = new FormData();
      formData.append("file", event.target.files[0]);

      dispatch(updateFile({ path: selectedDocument, formData: formData })).then(
        (response) => {
          dispatch(fetchFiles());
          if (response.error.message) {
            setOpenErrorSnackbar(true);
          }
        }
      );
    }
  };

  const handleCloseSnackbar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setOpenErrorSnackbar(false);
  };

  useEffect(() => {
    dispatch(fetchFiles()).then((response) => {
      if (response.error.message) {
        setOpenErrorSnackbar(true);
      }
    });

    dispatch(fetchLogs());
  }, [dispatch]);

  const renderTree = (nodes) => (
    <TreeItem key={nodes.path} nodeId={nodes.path} label={nodes.name}>
      {Array.isArray(nodes.children)
        ? nodes.children.map((node) => renderTree(node))
        : null}
    </TreeItem>
  );

  return (
    <Box sx={{ mt: "100px", width: "100%" }}>
      <Typography variant="h3" component="div" sx={{ flexGrow: 1 }}>
        Documents
      </Typography>
      <Container sx={{ py: 15 }} maxWidth="md">
        <Typography
          variant="h5"
          component="div"
          sx={{ flexGrow: 1, textAlign: "left" }}
        >
          Selected: {selectedDocument}
        </Typography>
        <Stack spacing={2} direction="row" sx={{ mt: "50px", width: "100%" }}>
          <Button variant="outlined" onClick={handleDownloadFile}>
            Download file
          </Button>
          <Button variant="outlined" onClick={handleDeleteFile}>
            Delete file
          </Button>
          <Button variant="outlined" onClick={handleDeleteDirectory}>
            Delete Directory
          </Button>
          <Button variant="outlined" component="label">
            Upload File
            <input hidden multiple type="file" onChange={handleUploadFile} />
          </Button>
          <Button variant="outlined" component="label">
            Update File
            <input hidden multiple type="file" onChange={handleUpdateFile} />
          </Button>
        </Stack>
        <Stack spacing={2} direction="row" sx={{ mt: "50px", width: "100%" }}>
          <TextField
            id="outlined-basic"
            label="New directory name"
            variant="outlined"
            sx={{ width: "100%" }}
            onChange={(event) => {
              setNewDirectoryName(event.target.value);
            }}
          />
          <Button
            variant="outlined"
            component="label"
            onClick={handleCreateDirectory}
          >
            Create directory
          </Button>
        </Stack>
        <Stack spacing={2} direction="row" sx={{ mt: "50px", width: "100%" }}>
          <TextField
            id="outlined-basic"
            label="New file location starting from your root"
            variant="outlined"
            sx={{ width: "100%" }}
            onChange={(event) => {
              setNewFileLocation(event.target.value);
            }}
          />
          <Button variant="outlined" component="label" onClick={handleMoveFile}>
            Move file
          </Button>
        </Stack>
        <TreeView
          aria-label="rich object"
          defaultCollapseIcon={<ExpandMoreIcon />}
          defaultExpanded={["root"]}
          defaultExpandIcon={<ChevronRightIcon />}
          onNodeSelect={handleDirectorySelect}
          sx={{
            height: 110,
            flexGrow: 1,
            maxWidth: 400,
            overflowY: "auto",
            mt: "50px",
          }}
        >
          {renderTree(root)}
        </TreeView>
      </Container>
      <Container sx={{ py: 2 }} maxWidth="md">
        <Typography variant="h3" component="div" sx={{ flexGrow: 1 }}>
          {logs.length > 0 && "Logs"}
        </Typography>
        <List dense sx={{ width: "100%", bgcolor: "background.paper" }}>
          {logs.map((log) => {
            return (
              <ListItem key={log.id} disablePadding>
                <ListItemText id={log.id} primary={log.time} />
                <ListItemText id={log.id} primary={log.type} />
                <ListItemText id={log.id} primary={log.username} />
                <ListItemText id={log.id} primary={log.document} />
              </ListItem>
            );
          })}
        </List>
      </Container>
      <Snackbar
        open={openErrorSnackbar}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity="error"
          sx={{ width: "100%" }}
        >
          {error}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Documents;
