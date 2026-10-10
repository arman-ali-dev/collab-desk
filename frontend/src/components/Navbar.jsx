import searchIcon from "../assets/search.png";
import chronometerIcon from "../assets/chronometer.png";
import plusIcon from "../assets/plus.png";
import bellIcon from "../assets/bell.png";
import userAvatar from "../assets/userAvatar.png";

import { Avatar, Tooltip } from "@mui/material";
import { useEffect, useRef, useState } from "react";
import CreateNewTaskForm from "../pages/Dashboard/CreateNewTaskForm";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import Notifications from "./Notifications";
import Reminders from "./Reminders";

const Navbar = () => {
  const { profile } = useSelector((state) => state.profile);
  const navigate = useNavigate();

  const [open, setOpen] = useState(false);

  const toggleDrawer = (value) => () => {
    setOpen(value);
  };

  // Notifications
  const { notifications } = useSelector((state) => state.notification);
  const notificationRef = useRef(null);
  const [showNotifications, setShowNotifications] = useState(false);

  // Reminders
  const { reminders } = useSelector((state) => state.memberTasks);
  const remindersRef = useRef(null);
  const [showReminders, setShowReminders] = useState(false);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (
        notificationRef.current &&
        !notificationRef.current.contains(e.target) &&
        !e.target.closest(".notification-trigger")
      ) {
        setShowNotifications(false);
      }

      if (remindersRef.current && !remindersRef.current.contains(e.target)) {
        setShowReminders(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <>
      <div
        className="px-10 py-4 flex relative w-full justify-between bg-white"
        style={{
          boxShadow: "0 1px 0 #efefef, 0 2px 8px rgba(0,0,0,0.04)",
          transition: "box-shadow 0.2s ease",
        }}
      >
        <div
          className="search-input-wrap flex gap-2 items-center bg-[#EFEFEF] px-3 rounded-lg"
          style={{ minWidth: 200 }}
        >
          <div className="flex justify-center items-center">
            <img
              className="w-3.5"
              src={searchIcon}
              alt=""
              style={{ transition: "opacity 0.2s", opacity: 0.6 }}
            />
          </div>
          <input
            className="border-0 outline-0 py-2.5 text-[13px] placeholder:text-[13px]  bg-transparent w-full"
            style={{
              color: "#000",
              opacity: 0.8,
              transition: "opacity 0.2s",
            }}
            type="text"
            placeholder="Search here..."
          />
        </div>

        <div className="flex items-center gap-7">
          <div className="flex gap-2">
            <div className="relative">
              <Tooltip onClick={() => setShowReminders(true)} title="Reminder">
                <div className="nav-icon-btn w-9 h-9 relative cursor-pointer bg-[#EFEFEF] rounded-lg flex justify-center items-center">
                  <img
                    className="w-4.5"
                    src={chronometerIcon}
                    alt=""
                    style={{
                      transition: "transform 0.2s ease",
                    }}
                  />

                  {reminders?.length > 0 && (
                    <span className="badge-dot bg-[#FA2626] absolute -top-0.5 -right-1 opacity-80 flex justify-center items-center text-[9px] text-white h-3.5 w-3.5 rounded-full">
                      {Reminders.length}
                    </span>
                  )}
                </div>
              </Tooltip>

              <Reminders
                showReminders={showReminders}
                remindersRef={remindersRef}
              />
            </div>

            <div className="relative">
              <Tooltip
                onClick={() => setShowNotifications(true)}
                title="Notifications"
              >
                <div className="nav-icon-btn w-9 cursor-pointer relative h-9 bg-[#EFEFEF] rounded-lg flex justify-center items-center">
                  <img
                    className="w-4"
                    src={bellIcon}
                    alt=""
                    style={{
                      transition: "transform 0.3s ease",
                    }}
                  />

                  {notifications?.length > 0 && (
                    <span className="badge-dot bg-[#FA2626] absolute -top-0.5 -right-1 opacity-80 flex justify-center items-center text-[9px] text-white h-3.5 w-3.5 rounded-full">
                      {notifications?.length}
                    </span>
                  )}
                </div>
              </Tooltip>

              <Notifications
                notificationRef={notificationRef}
                showNotifications={showNotifications}
              />
            </div>

            <Tooltip
              onClick={(e) => {
                if (profile?.role === "MEMBER") return;
                e.stopPropagation();
                toggleDrawer(true)(e);
              }}
              title={
                profile?.role === "MEMBER"
                  ? "Only Admin can create tasks"
                  : "Create Task"
              }
            >
              <div
                className={`w-9 h-9 rounded-lg flex justify-center items-center ${
                  profile?.role === "MEMBER"
                    ? "bg-[#EFEFEF] opacity-50 cursor-not-allowed"
                    : "nav-icon-btn bg-[#EFEFEF] cursor-pointer"
                }`}
              >
                <img className="w-3.5" src={plusIcon} alt="" />
              </div>
            </Tooltip>
          </div>

          <div className="h-full w-px bg-[#efefef]"> </div>

          <Tooltip title="Profile">
            <div
              className="avatar-btn rounded-full cursor-pointer"
              onClick={() => navigate("/profile")}
            >
              <Avatar
                src={profile?.profileImage || userAvatar}
                alt="User Profile"
                sx={{
                  width: 35.5,
                  height: 35.5,
                  cursor: "pointer",
                  objectFit: "cover",
                }}
              />
            </div>
          </Tooltip>
        </div>
      </div>

      <CreateNewTaskForm toggleDrawer={toggleDrawer} open={open} />
    </>
  );
};

export default Navbar;
