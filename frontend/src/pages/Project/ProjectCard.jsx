import { useState } from "react";
import externalIcon from "../../assets/external.png";
import { Link, useNavigate } from "react-router-dom";
import clockIcon from "../../assets/clock.png";
import {
  Alert,
  CircularProgress,
  IconButton,
  Skeleton,
  Snackbar,
} from "@mui/material";
import editIcon from "../../assets/edit.png";
import deleteIcon from "../../assets/delete.png";
import userAvatar from "../../assets/userAvatar.png";
import { useDispatch, useSelector } from "react-redux";
import EditProjectForm from "./EditProjectForm";
import { deleteProject } from "../../store/admin/projectSlice";

const ProjectCard = ({ project }) => {
  const { profile } = useSelector((state) => state.profile);

  const progressColor =
    project.progress > 50
      ? "#18A322"
      : project.progress === 50
        ? "#157FD7"
        : "#FA2626";

  const getDaysAgo = (startDate) => {
    if (!startDate) return null;

    const today = new Date();
    const start = new Date(startDate);

    today.setHours(0, 0, 0, 0);
    start.setHours(0, 0, 0, 0);

    const msPerDay = 1000 * 60 * 60 * 24;
    return Math.round((today - start) / msPerDay);
  };

  const [open, setOpen] = useState(false);
  const toggleDrawer = (value) => (event) => {
    setOpen(value);
  };

  const dispatch = useDispatch();
  const { deleteProjectId } = useSelector((state) => state.adminProjects);
  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [snackType, setSnackType] = useState("success");

  const handleDelete = async () => {
    try {
      await dispatch(deleteProject(project.id)).unwrap();

      setSnackType("success");
      setSnackMessage("Project deleted");
      setOpenSnack(true);
    } catch (err) {
      setSnackType("error");
      setSnackMessage(err);
      setOpenSnack(true);
    }
  };

  return (
    <>
      <div>
        <div
          className="bg-white group relative cursor-pointer rounded-xl px-6 py-5"
          style={{
            transition:
              "opacity 0.45s ease 100ms, transform 0.45s cubic-bezier(0.34,1.2,0.64,1) 100ms, box-shadow 0.25s ease",
            boxShadow: "0 2px 8px rgba(0,0,0,0.07)",
          }}
        >
          {profile?.role === "ADMIN" && (
            <div
              className="absolute top-3 right-3 flex gap-1 z-50"
              style={{
                transition: "opacity 0.2s ease, transform 0.2s ease",
              }}
            >
              <IconButton
                onClick={toggleDrawer(true)}
                size="small"
                sx={{
                  transition:
                    "background 0.15s ease, transform 0.15s ease !important",
                  "&:hover": {
                    backgroundColor: "#f0f0f0 !important",
                    transform: "scale(1.1) !important",
                  },
                }}
              >
                <img className="w-4.5" src={editIcon} alt="Edit" />
              </IconButton>

              <IconButton
                disabled={deleteProjectId == project.id}
                onClick={handleDelete}
                size="small"
                sx={{
                  transition:
                    "background 0.15s ease, transform 0.15s ease !important",
                  "&:hover": {
                    backgroundColor: "#fff0f0 !important",
                    transform: "scale(1.1) !important",
                  },
                }}
              >
                {deleteProjectId == project.id ? (
                  <CircularProgress size={15} sx={{ color: "#000" }} />
                ) : (
                  <img className="w-4" src={deleteIcon} alt="Delete" />
                )}
              </IconButton>
            </div>
          )}

          <img
            className="w-16 h-16 object-contain"
            src={project.logo}
            alt=""
            style={{
              transition: "transform 0.3s cubic-bezier(0.34,1.56,0.64,1)",
            }}
          />

          <h3
            className="font-medium text-[14px] mt-2.5"
            style={{ opacity: 0.8 }}
          >
            {project.title}
          </h3>

          <Link
            to={project.url}
            onClick={(e) => e.stopPropagation()}
            className="font-medium -mt-1 text-[12px] flex gap-1 items-center"
            target="_blank"
            rel="noopener noreferrer"
            style={{
              color: "#747373",
              transition: "color 0.15s ease",
            }}
            onMouseEnter={(e) => (e.currentTarget.style.color = "#157FD7")}
            onMouseLeave={(e) => (e.currentTarget.style.color = "#747373")}
          >
            <img className="w-2.5" src={externalIcon} alt="" />
            <span>{project.url}</span>
          </Link>

          <p className="text-[13.5px] font-medium mt-4">
            {project?.description.split(" ").slice(0, 10).join(" ")}
            {project?.description.split(" ").length > 10 && "..."}
          </p>

          <div className="mt-5">
            <p
              className="text-[13px] text-right font-medium"
              style={{
                transition: "color 0.3s ease",
                color: "inherit",
              }}
            >
              {project.progress}%
            </p>
            <div className="h-1 w-full bg-[#D4D9D4] rounded-full overflow-hidden">
              <div
                style={{
                  width: project.progress + "%",
                  backgroundColor: progressColor,
                  height: "100%",
                  borderRadius: "inherit",
                  transition: "width 0.9s cubic-bezier(0.22,1,0.36,1)",
                }}
              />
            </div>
          </div>

          <div className="mt-6 flex justify-between items-center">
            <div
              className="bg-[#ebebeb] rounded-md flex gap-2 items-center py-1.5 px-3 text-[#605D5D] text-[11px]"
              style={{
                transition: "background 0.2s ease, transform 0.2s ease",
              }}
            >
              <img className="w-3" src={clockIcon} alt="" />
              <span className="font-medium">
                {getDaysAgo(project?.createdAt)} Days Ago
              </span>
            </div>

            <div className="flex">
              {project.members.map((u, index) => (
                <div
                  key={u.id}
                  className={`min-w-8 min-h-8 w-8 h-8 rounded-full object-cover flex items-center justify-center text-white text-[13px] font-semibold ${index != project.members.length - 1 && "-mr-3"}  z-50 border-white border-2`}
                  style={{
                    backgroundColor: "#9c9b9b",
                    transition: `transform 0.2s ease ${1 * 35}ms`,
                  }}
                >
                  <img className="rounded-full" src={userAvatar} alt="" />
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      <EditProjectForm
        project={project}
        toggleDrawer={toggleDrawer}
        open={open}
      />

      <Snackbar
        open={openSnack}
        autoHideDuration={3000}
        onClose={() => setOpenSnack(false)}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={() => setOpenSnack(false)}
          severity={snackType}
          sx={{ width: "100%", fontSize: "13px" }}
        >
          {snackMessage}
        </Alert>
      </Snackbar>
    </>
  );
};

export default ProjectCard;
