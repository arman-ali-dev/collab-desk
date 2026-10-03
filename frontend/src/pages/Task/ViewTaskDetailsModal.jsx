import {
  Box,
  Modal,
  Typography,
  Chip,
  Avatar,
  Divider,
  IconButton,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import CalendarTodayIcon from "@mui/icons-material/CalendarToday";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import PriorityHighIcon from "@mui/icons-material/PriorityHigh";
import FolderIcon from "@mui/icons-material/Folder";
import PersonIcon from "@mui/icons-material/Person";

import userAvatar from "../../assets/userAvatar.png";

const labelSx = {
  fontWeight: 600,
  color: "#6B7280",
  textTransform: "uppercase",
  fontSize: "11px",
  letterSpacing: "0.05em",
};

const valueSx = {
  color: "#111827",
  fontSize: "14px",
  fontWeight: 500,
  mt: 0.5,
};

const ViewTaskDetailsModal = ({ task, open, handleClose }) => {
  return (
    <Modal
      open={open}
      aria-labelledby="task-modal-title"
      sx={{
        "& .MuiBackdrop-root": {
          backgroundColor: "rgba(0,0,0,0.35)",
          backdropFilter: "blur(3px)",
        },
      }}
    >
      <Box
        sx={{
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          width: 680,
          maxHeight: "90vh",
          bgcolor: "background.paper",
          borderRadius: "14px",
          boxShadow:
            "0 24px 64px rgba(0,0,0,0.18), 0 4px 16px rgba(0,0,0,0.08)",
          overflow: "hidden",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "flex-start",
            p: 3,
            borderBottom: "1px solid #E5E7EB",
          }}
        >
          <Box sx={{ flex: 1 }}>
            <Typography
              id="task-modal-title"
              variant="h5"
              sx={{ fontWeight: 600, color: "#111827", mb: 1.5 }}
            >
              {task.title}
            </Typography>
            <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap" }}>
              <span className="px-3 py-1 text-[12px] font-semibold rounded bg-[rgba(129,39,255,.2)] text-[#8127FF]">
                In Progress
              </span>
              <span
                style={{
                  color: "#497AF5",
                  backgroundColor: "rgba(73,122,245,0.2)",
                }}
                className="px-3 py-1.5 rounded-md text-[11px] font-medium inline-block"
              >
                {task.category}
              </span>
            </Box>
          </Box>

          <IconButton
            onClick={handleClose}
            size="small"
            sx={{ color: "#6B7280" }}
          >
            <CloseIcon />
          </IconButton>
        </Box>

        <Box sx={{ overflowY: "auto", p: 3, flex: 1 }}>
          <Box sx={{ mb: 3 }}>
            <Typography
              variant="subtitle2"
              sx={{
                fontWeight: 600,
                color: "#374151",
                mb: 1,
                fontSize: "13px",
                letterSpacing: "0.05em",
              }}
            >
              DESCRIPTION
            </Typography>
            <Typography
              sx={{ color: "#4B5563", fontSize: "14px", lineHeight: 1.7 }}
            >
              {task.description}
            </Typography>
          </Box>

          <Divider sx={{ my: 3 }} />

          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: 2.5,
              mb: 3,
            }}
          >
            <Box
              sx={{
                padding: "12px",
                borderRadius: "10px",
                border: "1px solid #F3F4F6",
              }}
            >
              <Box
                sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}
              >
                <PriorityHighIcon sx={{ fontSize: 16, color: "#6B7280" }} />
                <Typography variant="caption" sx={labelSx}>
                  Priority
                </Typography>
              </Box>
              <span className="px-3 py-1 text-[12px] font-semibold rounded bg-[rgba(129,39,255,.2)] text-[#8127FF]">
                {task.priority}
              </span>
            </Box>

            <Box
              sx={{
                padding: "12px",
                borderRadius: "10px",
                border: "1px solid #F3F4F6",
              }}
            >
              <Box
                sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}
              >
                <CalendarTodayIcon sx={{ fontSize: 16, color: "#6B7280" }} />
                <Typography variant="caption" sx={labelSx}>
                  Due Date
                </Typography>
              </Box>
              <Typography sx={valueSx}>{task.dueDate}</Typography>
            </Box>

            <Box
              sx={{
                padding: "12px",
                borderRadius: "10px",
                border: "1px solid #F3F4F6",
              }}
            >
              <Box
                sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}
              >
                <AccessTimeIcon sx={{ fontSize: 16, color: "#6B7280" }} />
                <Typography variant="caption" sx={labelSx}>
                  Estimated Time
                </Typography>
              </Box>
              <Typography sx={valueSx}>{task.estimatedTime} hours</Typography>
            </Box>

            <Box
              sx={{
                padding: "12px",
                borderRadius: "10px",
                border: "1px solid #F3F4F6",
              }}
            >
              <Box
                sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}
              >
                <FolderIcon sx={{ fontSize: 16, color: "#6B7280" }} />
                <Typography variant="caption" sx={labelSx}>
                  Project
                </Typography>
              </Box>
              <Typography sx={valueSx}>{task.projectName}</Typography>
            </Box>
          </Box>

          <Divider sx={{ my: 3 }} />

          <Box sx={{ mb: 3 }}>
            <Box
              sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1.5 }}
            >
              <PersonIcon sx={{ fontSize: 16, color: "#6B7280" }} />
              <Typography variant="caption" sx={labelSx}>
                Assigned To
              </Typography>
            </Box>
            <Box sx={{ display: "flex", gap: 1.5, flexWrap: "wrap" }}>
              {task.assignedTo.map((u) => (
                <Box
                  key={u.id}
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1,
                    backgroundColor: "#F9FAFB",
                    px: 1.5,
                    py: 1,
                    borderRadius: "8px",
                    border: "1px solid #E5E7EB",
                  }}
                >
                  <Avatar
                    sx={{
                      width: 28,
                      height: 28,
                      fontSize: "12px",
                      backgroundColor: "#6366F1",
                    }}
                  >
                    <img src={u.profileImage || userAvatar} alt="" />
                  </Avatar>
                  <Typography
                    sx={{ fontSize: "13px", fontWeight: 500, color: "#111827" }}
                  >
                    {u.fullName}
                  </Typography>
                </Box>
              ))}
            </Box>
          </Box>
        </Box>

        <Box
          sx={{
            borderTop: "1px solid #E5E7EB",
            px: 3,
            py: 2,
            backgroundColor: "#F9FAFB",
          }}
        >
          <Typography sx={{ fontSize: "12px", color: "#6B7280" }}>
            {task.createdAt.split("T")[0]}
          </Typography>
        </Box>
      </Box>
    </Modal>
  );
};

export default ViewTaskDetailsModal;
