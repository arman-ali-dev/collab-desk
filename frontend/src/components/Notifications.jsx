import { faBell } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, CircularProgress, Tooltip } from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import {
  clearNotifications,
  markAllNotificationsRead,
} from "../store/notificationSlice";

const Notifications = ({ notificationRef, showNotifications }) => {
  const dispatch = useDispatch();
  const { notifications, loading } = useSelector((state) => state.notification);

  const handleClearMessages = (e) => {
    e.stopPropagation();
    dispatch(markAllNotificationsRead());
    dispatch(clearNotifications());
  };

  return (
    <>
      <div
        ref={notificationRef}
        className={`dropdown ${
          showNotifications ? "show" : ""
        } bg-[#EFEFEF] z-999 max-w-73.5 h-77.5 w-65 absolute`}
      >
        {notifications.length === 0 ? (
          <p className="text-center text-[12px] text-gray-400 py-8">
            No notifications yet
          </p>
        ) : loading ? (
          <div className="flex justify-center items-center h-full">
            <CircularProgress color="#000" size={20} />
          </div>
        ) : (
          <>
            <div className="border-b border-b-[#ccc] px-3 py-1.25">
              <p className="text-black text-[14px] font-semibold">
                {notifications?.length} new notifications
              </p>
            </div>

            <div className="max-h-64 overflow-y-auto">
              {notifications?.map((n) => (
                <div
                  key={n.id}
                  className="notification-row flex relative p-3 border-b border-[#ECECEC] gap-3 items-start transition-colors hover:bg-[#F8F9FA] bg-[#FAFAFA]"
                >
                  <div className="min-w-8 min-h-8 w-8 h-8 rounded-full flex items-center justify-center bg-[#F3F4F6] text-[#555] text-[12px] border border-[#E5E7EB]">
                    <FontAwesomeIcon icon={faBell} />
                  </div>

                  <div className="flex-1 min-w-0 pr-12">
                    <Tooltip title={n.titl}>
                      <p className="text-[#111827] font-semibold text-[13px] truncate">
                        {n.title}
                      </p>
                    </Tooltip>

                    <Tooltip title={n.message}>
                      <p className="text-[#6B7280]  text-[12px] leading-relaxed mt-0.5">
                        {n.message.split(" ").slice(0, 7).join(" ")}
                        {n.message.split(" ").length > 7 && "..."}
                      </p>
                    </Tooltip>

                    <p className="text-[#9CA3AF] text-[11px] mt-1">
                      {n.createdAt
                        ? new Date(n.createdAt).toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                          })
                        : ""}
                    </p>
                  </div>

                  <p className="inline-block text-[10px] px-2 py-0.5 rounded-full absolute top-2 right-2 font-medium bg-[#111827] text-white">
                    {n.type.charAt(0).toUpperCase() +
                      n.type.slice(1).toLowerCase()}
                  </p>
                </div>
              ))}
            </div>

            <div className="notification-trigger absolute bottom-0 w-full">
              <Button
                onClick={handleClearMessages}
                sx={{
                  width: "100%",
                  color: "black",
                  fontSize: "12px",
                  textAlign: "center",
                  backgroundColor: "#dadada",
                  textTransform: "capitalize",
                  borderRadius: "0px",
                  transition: "background 0.18s ease !important",
                  "&:hover": {
                    backgroundColor: "#c8c8c8 !important",
                  },
                }}
              >
                <span className="font-medium">Clear All</span>
              </Button>
            </div>
          </>
        )}
      </div>
    </>
  );
};

export default Notifications;
