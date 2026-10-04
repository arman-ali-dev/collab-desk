import React, { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Avatar,
  Box,
  Typography,
  Chip,
  IconButton,
  Menu,
  MenuItem,
} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import InsertDriveFileIcon from "@mui/icons-material/InsertDriveFile";
import ImageIcon from "@mui/icons-material/Image";
import CodeIcon from "@mui/icons-material/Code";

const files = [
  {
    id: 1,
    fileName: "brand-guidelines.pdf",
    fileSize: 2400000,
    updatedAt: "2025-03-10T10:30:00Z",
    visibility: "PUBLIC",
  },
  {
    id: 2,
    fileName: "hero-reference.png",
    fileSize: 860000,
    updatedAt: "2025-03-08T09:15:00Z",
    visibility: "PUBLIC",
  },
  {
    id: 3,
    fileName: "requirements.docx",
    fileSize: 120000,
    updatedAt: "2025-03-05T16:45:00Z",
    visibility: "PRIVATE",
  },
  {
    id: 4,
    fileName: "index.html",
    fileSize: 8500,
    updatedAt: "2025-03-02T11:00:00Z",
    visibility: "PUBLIC",
  },
  {
    id: 5,
    fileName: "meeting-notes.txt",
    fileSize: 3200,
    updatedAt: "2025-02-27T14:20:00Z",
    visibility: "PRIVATE",
  },
];

const permissionStyles = {
  EDITOR: { color: "#09c015e6", bg: "#86878633" },
  Editor: { color: "#09c015e6", bg: "#86878633" },
  "View Only": { color: "#605d5de6", bg: "#60606033" },
  VIEWER: { color: "#605d5de6", bg: "#60606033" },
  ADMINISTRATOR: { color: "#fa2626e6", bg: "#de171733" },
  Administrator: { color: "#fa2626e6", bg: "#de171733" },
};

const getFileIcon = (fileName) => {
  if (!fileName) return <InsertDriveFileIcon />;
  const ext = fileName.split(".").pop()?.toLowerCase();
  if (["jpg", "jpeg", "png", "gif", "webp"].includes(ext)) return <ImageIcon />;
  if (["html", "css", "js", "jsx", "ts", "tsx", "java", "py"].includes(ext))
    return <CodeIcon />;
  return <InsertDriveFileIcon />;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
};

const formatSize = (bytes) => {
  if (!bytes) return "";
  if (bytes >= 1e9) return `${(bytes / 1e9).toFixed(0)} GB`;
  if (bytes >= 1e6) return `${(bytes / 1e6).toFixed(0)} MB`;
  if (bytes >= 1e3) return `${(bytes / 1e3).toFixed(0)} KB`;
  return `${bytes} B`;
};

const FileTable = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    event.stopPropagation();
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => setAnchorEl(null);

  return (
    <>
      <TableContainer
        component={Paper}
        elevation={0}
        sx={{ border: "1px solid #eee" }}
      >
        <Table sx={{ minWidth: 650 }}>
          <TableHead>
            <TableRow>
              <TableCell sx={{ fontWeight: "bold", color: "#333" }}>
                File Name
              </TableCell>
              <TableCell sx={{ fontWeight: "bold", color: "#333" }}>
                Last Modified
              </TableCell>
              <TableCell sx={{ fontWeight: "bold", color: "#333" }}>
                File Permission
              </TableCell>
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {files.map((row) => (
              <TableRow key={row.id} hover sx={{ cursor: "pointer" }}>
                {/* File Icon and Name */}
                <TableCell>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    <Avatar sx={{ bgcolor: "#f0f0f0", color: "#555" }}>
                      {getFileIcon(row.fileName)}
                    </Avatar>
                    <Box>
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        {row.fileName}
                      </Typography>
                      <Typography variant="caption" color="textSecondary">
                        {formatSize(row.fileSize)}
                      </Typography>
                    </Box>
                  </Box>
                </TableCell>

                {/* Date */}
                <TableCell>{formatDate(row.updatedAt)}</TableCell>

                {/* Permission Chip */}
                <TableCell>
                  <Chip
                    label={row.visibility || "PUBLIC"}
                    size="small"
                    sx={{
                      fontWeight: 600,
                      color: permissionStyles[row.visibility]?.color || "#555",
                      backgroundColor:
                        permissionStyles[row.visibility]?.bg || "#eee",
                      borderRadius: "12px",
                    }}
                  />
                </TableCell>

                <TableCell align="right">
                  <IconButton
                    size="small"
                    onClick={handleClick}
                    aria-controls={open ? "file-menu" : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? "true" : undefined}
                  >
                    <MoreVertIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Dropdown Menu */}
      <Menu
        id="file-menu"
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        MenuListProps={{ "aria-labelledby": "file-menu-button" }}
        PaperProps={{
          style: {
            boxShadow: "0px 5px 15px rgba(0,0,0,0.1)",
            minWidth: "140px",
          },
        }}
      >
        <MenuItem onClick={handleClose}>Share</MenuItem>
        <MenuItem onClick={handleClose}>Toggle Visibility</MenuItem>
        <MenuItem onClick={handleClose}>View Details</MenuItem>
        <MenuItem onClick={handleClose} sx={{ color: "error.main" }}>
          Delete
        </MenuItem>
      </Menu>
    </>
  );
};

export default FileTable;
