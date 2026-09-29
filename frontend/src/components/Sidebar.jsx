import chartIcon from "../assets/chart.png";
import monitorIcon from "../assets/monitor.png";
import checklistIcon from "../assets/checklist.png";
import calenderIcon from "../assets/calendar.png";
import chatIcon from "../assets/chat.png";
import driveIcon from "../assets/drive.png";
import usersIcon from "../assets/users.png";
import logoutIcon from "../assets/logout.png";

import { Link } from "react-router-dom";

const manu = [
  { label: "Dashboard", icon: chartIcon, path: "/dashboard" },
  { label: "Projects", icon: monitorIcon, path: "/projects" },
  // { label: "My Tasks", icon: checklistIcon, path: "/my-tasks" },
  { label: "Calender", icon: calenderIcon, path: "/calendar" },
  // { label: "Chat", icon: chatIcon, path: "/chat" },
  // { label: "Drive", icon: driveIcon, path: "/drive" },
  { label: "Users", icon: usersIcon, path: "/users" },
];

const Sidebar = () => {
  return (
    <>
      <div
        className="bg-white fixed left-0 top-0 h-screen w-76.25 z-50"
        style={{
          boxShadow: "2px 0 16px rgba(0,0,0,0.06)",
          transition: "box-shadow 0.3s ease",
        }}
      >
        <div className="flex items-center gap-12 border-[#efefef] border-r pl-3 pr-14 border-b h-17.25 shadow ">
          <Link
            className="text-[21px] font-bold"
            style={{
              transition: "opacity 0.2s ease, letter-spacing 0.2s ease",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.opacity = "0.7";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.opacity = "1";
            }}
          >
            <img
              src="https://a2groups.org/assets/a2glogo-cf360e03.png"
              alt="A2 Groups Logo"
              className="w-28 h-18 object-contain"
            />
          </Link>
        </div>

        <ul className="px-6 mt-10 space-y-1">
          {manu.map((item, idx) => {
            const isActive = location.pathname.startsWith(item.path);

            return (
              <li
                key={idx}
                className={`sidebar-item pl-5 py-3 relative rounded-lg ${isActive ? "bg-[#EFEFEF] sidebar-item-active" : ""}`}
                style={{
                  animationDelay: `${idx * 0.06}s`,
                }}
              >
                <Link to={item.path} className="flex items-center gap-3">
                  <img
                    className="sidebar-icon w-5"
                    src={item.icon}
                    alt=""
                    style={{
                      filter: isActive ? "brightness(0.7)" : "brightness(0.9)",
                      transition: "filter 0.2s ease",
                    }}
                  />
                  <span
                    className="text-[15px] "
                    style={{
                      fontWeight: isActive ? 600 : 400,
                      transition: "font-weight 0.15s ease",
                    }}
                  >
                    {item.label}
                  </span>
                </Link>
              </li>
            );
          })}
        </ul>

        <div className="px-6 absolute bottom-6 w-full border-[#efefef] border-t pt-6">
          <div
            className="logout-btn pl-5 py-3 relative cursor-pointer w-full rounded-lg bg-[#EFEFEF]"
            style={{
              opacity: 1,
              transition: "opacity 0.3s ease, transform 0.3s ease",
            }}
          >
            <div className="flex items-center gap-3">
              <img
                className="w-5"
                src={logoutIcon}
                alt=""
                style={{ transition: "transform 0.2s ease" }}
              />
              <span className="text-[15px]">Logout</span>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Sidebar;
