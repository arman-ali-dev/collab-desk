import { useState } from "react";
import dragIcon from "../../assets/drag.png";
import messageIcon from "../../assets/mes.png";
import menuIcon from "../../assets/menu.png";
import userAvatar from "../../assets/userAvatar.png";
import { Divider, IconButton, Menu, MenuItem, Tooltip } from "@mui/material";
import { updateTaskStatus } from "../../store/member/taskSlice";
import { useDispatch } from "react-redux";
import ViewTaskDetailsModal from "./ViewTaskDetailsModal";

const Task = ({ task, isDragging = false, currentUserId, userRole }) => {
  const dispatch = useDispatch();
  const [hovered, setHovered] = useState(false);

  const [filterAnchorEl, setFilterAnchorEl] = useState(null);
  const openFilterDropDown = Boolean(filterAnchorEl);

  const categoryStyle = {
    color:
      task.category === "DESIGN"
        ? "#497AF5"
        : "DESIGN" === "DEVELOPMENT"
          ? "rgba(250,38,38,.7)"
          : "#09C015",
    backgroundColor:
      task.category === "DESIGN"
        ? "rgba(73,122,245,0.2)"
        : "lsnf" === "DEVELOPMENT"
          ? "rgba(222,23,23,.2)"
          : "rgba(1,255,18,.3)",
  };

  const getNextStatus = (status) => {
    switch (status) {
      case "TO_DO":
        return { label: "Move to Doing", value: "IN_PROGRESS" };
      case "IN_PROGRESS":
        return { label: "Move to Done", value: "DONE" };
      default:
        return null;
    }
  };

  const nextStatus = getNextStatus(task.status);

  const handleClick = (e) => setFilterAnchorEl(e.currentTarget);
  const handleCloseFilterDropDown = () => setFilterAnchorEl(null);

  const [openDetailsModal, setOpenDetailsModal] = useState(false);

  const handleOpenDetailsModal = () => setOpenDetailsModal(true);
  const handleCloseDetailsModal = () => setOpenDetailsModal(false);

  return (
    <>
      <div
        onMouseEnter={() => !isDragging && setHovered(true)}
        onMouseLeave={() => setHovered(false)}
        className="w-full px-5 flex justify-between items-center py-2 rounded-xl"
        style={{
          backgroundColor: isDragging
            ? "#e0e0e0"
            : hovered
              ? "#e8e8e8"
              : "#EFEFEF",
          boxShadow: isDragging
            ? "0 16px 40px rgba(0,0,0,0.2)"
            : hovered
              ? "0 4px 14px rgba(0,0,0,0.08)"
              : "none",
          transition: isDragging
            ? "none"
            : "background-color 0.18s ease, box-shadow 0.2s ease",
        }}
      >
        <div className="flex gap-7 items-center">
          <img
            className="w-5 cursor-grab active:cursor-grabbing"
            src={dragIcon}
            alt=""
            style={{
              opacity: isDragging || hovered ? 1 : 0.4,
              transition: "opacity 0.2s",
            }}
          />

          <p
            className="text-[13px] font-medium"
            style={{ color: hovered ? "#000" : "#222" }}
          >
            {task.title}
          </p>
        </div>

        <p className="text-[13px] text-gray-500 hidden sm:block">
          {task.projectName}
        </p>

        <button
          type="button"
          className="text-[11px] flex items-center gap-1.5 font-medium cursor-pointer hover:opacity-70"
          style={{
            opacity: hovered ? 1 : 0.7,
            transition: "opacity 0.2s",
          }}
        >
          <img src={messageIcon} alt="" className="w-3.5" />3 Conversations
        </button>

        <div className="flex">
          {task.assignedTo.map((u) => (
            <Tooltip key={u.id} title={u.fullName}>
              <img
                className="w-6.5 min-w-6.5 min-h-6.5 h-6.5 -mr-3.5 z-50 relative border-white border rounded-full object-cover"
                src={u.profileImage || userAvatar}
                alt=""
              />
            </Tooltip>
          ))}
        </div>

        <div
          style={{ ...categoryStyle }}
          className="px-3 py-1.5 rounded-md text-[11px] font-medium inline-block"
        >
          {task.category}
        </div>

        <div>
          <IconButton
            onClick={handleClick}
            sx={{ "&:hover": { backgroundColor: "#d8d8d8" } }}
          >
            <img
              src={menuIcon}
              alt="menu"
              className="w-4.5 h-4.5"
              style={{
                transition: "transform 0.2s ease",
              }}
            />
          </IconButton>

          <Menu
            anchorEl={filterAnchorEl}
            open={openFilterDropDown}
            onClose={handleCloseFilterDropDown}
            PaperProps={{
              sx: {
                width: 180,
                borderRadius: "10px",
                boxShadow: "0 8px 24px rgba(0,0,0,0.12)",
              },
            }}
            transformOrigin={{ horizontal: "right", vertical: "top" }}
            anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
          >
            <MenuItem
              onClick={() => {
                handleOpenDetailsModal();
                handleCloseFilterDropDown();
              }}
              sx={{ fontSize: "13px", fontWeight: 700 }}
            >
              View Details
            </MenuItem>

            {task.status !== "DONE" && <Divider />}

            {nextStatus && (
              <MenuItem
                sx={{ fontSize: "13px", fontWeight: 700 }}
                onClick={() => {
                  dispatch(
                    updateTaskStatus({
                      id: task.id,
                      status: nextStatus.value,
                    }),
                  );

                  handleCloseFilterDropDown();
                }}
              >
                {nextStatus.label}
              </MenuItem>
            )}
          </Menu>
        </div>
      </div>

      <ViewTaskDetailsModal
        task={task}
        open={openDetailsModal}
        handleClose={handleCloseDetailsModal}
      />
    </>
  );
};

export default Task;
