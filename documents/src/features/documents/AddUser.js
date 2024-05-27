import React from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import { Formik } from "formik";
import TextField from "@mui/material/TextField";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import { useFormik } from "formik";
import MenuItem from "@mui/material/MenuItem";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import FormHelperText from "@mui/material/FormHelperText";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import * as Yup from "yup";
import TreeView from "@mui/lab/TreeView";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import TreeItem from "@mui/lab/TreeItem";
import {
  fetchDirectories,
  selectDirectories,
  addNewUser,
  addUserRoles,
  fetchUsersByUsername,
} from "./DocumentsSlice";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

const AddUser = () => {
  const formik = useFormik({
    initialValues: {
      username: "",
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      ip: "",
      rootdir: "",
      role: "client",
    },

    validationSchema: Yup.object({
      username: Yup.string().required("Required"),
      firstName: Yup.string().required("Required"),
      lastName: Yup.string().required("Required"),
      password: Yup.string().required("Required"),
      rootdir: Yup.string().required("Required"),
      role: Yup.string().required("Required"),
      email: Yup.string().email("Invalid email address").required("Required"),
      ip: Yup.string()
        .matches(
          /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
          "Invalid ip address"
        )
        .required("Required"),
    }),

    onSubmit: (values) => {
      dispatch(
        addNewUser({
          attributes: {
            ip: [values.ip],
            rootdir: [values.rootdir],
          },
          credentials: [
            { type: "password", value: values.password, temporary: false },
          ],
          email: values.email,
          enabled: true,
          firstName: values.firstName,
          lastName: values.lastName,
          realmRoles: [values.role],
          requiredActions: ["CONFIGURE_TOTP"],
          username: values.username,
        })
      ).then(() => {
        dispatch(fetchUsersByUsername(values.username)).then((response) => {
          console.log("useri username ", response);
          if (values.role == "client") {
            dispatch(
              addUserRoles({
                roleName: "client",
                userId: response.payload[0].id,
                roleId: "1e52d070-6842-4ed9-84bd-fe8c2843be71",
              })
            ).then(() => {
              navigate("/users");
            });
          } else {
            dispatch(
              addUserRoles({
                roleName: "documents_admin",
                userId: response.payload[0].id,
                roleId: "5a74442e-4c87-486a-8a67-e51a0e84885f",
              })
            ).then(() => {
              navigate("/users");
            });
          }
        });
      });
    },
  });

  const handleDirectorySelect = (event, nodeIds) => {
    formik.setFieldValue("rootdir", nodeIds);
  };

  const dispatch = useDispatch();
  const root = useSelector(selectDirectories);

  let navigate = useNavigate();

  useEffect(() => {
    dispatch(fetchDirectories());
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
        Add user
      </Typography>
      <Container sx={{ py: 15 }} maxWidth="md">
        <form onSubmit={formik.handleSubmit}>
          <Stack spacing={2}>
            <TextField
              label="Username"
              variant="standard"
              type="text"
              name="username"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.username}
              helperText={
                formik.errors.username &&
                formik.touched.username &&
                formik.errors.username
              }
              error={
                formik.errors.username &&
                formik.touched.username &&
                formik.errors.username
              }
            />
            <TextField
              label="Email"
              variant="standard"
              type="email"
              name="email"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.email}
              helperText={
                formik.errors.email &&
                formik.touched.email &&
                formik.errors.email
              }
              error={
                formik.errors.email &&
                formik.touched.email &&
                formik.errors.email
              }
            />
            <TextField
              label="Password"
              variant="standard"
              type="password"
              name="password"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.password}
              helperText={
                formik.errors.password &&
                formik.touched.password &&
                formik.errors.password
              }
              error={
                formik.errors.password &&
                formik.touched.password &&
                formik.errors.password
              }
            />
            <TextField
              label="First name"
              variant="standard"
              type="text"
              name="firstName"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.firstName}
              helperText={
                formik.errors.firstName &&
                formik.touched.firstName &&
                formik.errors.firstName
              }
              error={
                formik.errors.firstName &&
                formik.touched.firstName &&
                formik.errors.firstName
              }
            />
            <TextField
              label="Last name"
              variant="standard"
              type="text"
              name="lastName"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.lastName}
              helperText={
                formik.errors.lastName &&
                formik.touched.lastName &&
                formik.errors.lastName
              }
              error={
                formik.errors.lastName &&
                formik.touched.lastName &&
                formik.errors.lastName
              }
            />
            <TextField
              label="IP address"
              variant="standard"
              type="text"
              name="ip"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.ip}
              helperText={
                formik.errors.ip && formik.touched.ip && formik.errors.ip
              }
              error={formik.errors.ip && formik.touched.ip && formik.errors.ip}
            />

            <FormControl sx={{ m: 1, minWidth: 120 }}>
              <InputLabel id="demo-simple-select-standard-label">
                User Type
              </InputLabel>
              <Select
                labelId="demo-simple-select-standard-label"
                id="demo-simple-select-standard"
                name="role"
                label="User type"
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                value={formik.values.role}
                error={
                  formik.errors.role &&
                  formik.touched.role &&
                  formik.errors.role
                }
              >
                <MenuItem value={"client"}>Client</MenuItem>
                <MenuItem value={"documents_admin"}>
                  Document administrator
                </MenuItem>
              </Select>
              <FormHelperText>
                {formik.errors.role &&
                  formik.touched.role &&
                  formik.errors.role}
              </FormHelperText>
            </FormControl>
            <TextField
              label="Root directory"
              variant="standard"
              type="text"
              name="rootdir"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.rootdir}
              helperText={
                formik.errors.rootdir &&
                formik.touched.rootdir &&
                formik.errors.rootdir
              }
              error={
                formik.errors.rootdir &&
                formik.touched.rootdir &&
                formik.errors.rootdir
              }
            />
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
              }}
            >
              {renderTree(root)}
            </TreeView>
            <Button variant="outlined" type="submit">
              Submit
            </Button>
          </Stack>
        </form>
      </Container>
    </Box>
  );
};

export default AddUser;
