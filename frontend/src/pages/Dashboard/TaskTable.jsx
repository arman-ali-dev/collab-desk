import { IconButton, Tooltip } from "@mui/material";

import userAvatar from "../../assets/userAvatar.png";
import filterIcon from "../../assets/filter.png";

const TaskTable = () => {
  return (
    <>
      <div
        className="rounded-2xl px-6 py-5 my-4 bg-white"
        style={{
          transition:
            "opacity 0.5s ease 0.3s, transform 0.5s ease 0.3s, box-shadow 0.25s ease",
          boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
        }}
      >
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-[17px] font-semibold">Tasks</h2>

          <IconButton
            sx={{
              width: 36,
              height: 36,
              backgroundColor: "#EFEFEF",
              cursor: "pointer",
              borderRadius: "8px",
              transition:
                "background 0.18s ease, transform 0.15s ease !important",
              "&:hover": {
                backgroundColor: "#e0e0e0 !important",
                transform: "scale(1.07)",
              },
              "&:active": {
                transform: "scale(0.93) !important",
              },
            }}
          >
            <img
              src={filterIcon}
              alt=""
              className="w-4"
              style={{
                transition: "transform 0.2s ease",
              }}
            />
          </IconButton>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr
                className="text-[13px] font-semibold border-b border-gray-300"
                style={{
                  transition: "opacity 0.4s ease 0.4s",
                }}
              >
                <th className="pb-4 pr-4">Title</th>
                <th className="pb-4 px-4">Project Name</th>
                <th className="pb-4 px-4">Assigned Date</th>
                <th className="pb-4 px-4">Status</th>
                <th className="pb-4 px-4">Due Date</th>
                <th className="pb-4 px-4">Priority</th>
                <th className="pb-4 pl-4">Assigned To</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-gray-50">
              <tr
                style={{
                  transition: `opacity 0.35s ease 100ms, transform 0.35s ease 100ms, background 0.15s ease`,
                  cursor: "default",
                }}
              >
                <td className="py-4 pr-4 text-[13px] font-medium text-gray-700">
                  title
                </td>

                <td className="py-4 px-4 text-[13px] font-medium text-gray-700">
                  project name
                </td>

                <td className="py-4 px-4 text-[13px] text-gray-600">
                  created at
                </td>

                <td className="py-4 px-4">
                  <span
                    className={`px-3 py-1 text-[12px] font-semibold rounded ${
                      "IN_PROGRESS" === "IN_PROGRESS"
                        ? "bg-[rgba(245,86,0,.2)] text-[#F55600]"
                        : "TODO" === "TODO"
                          ? "bg-[rgba(21,127,215,.2)] text-[#157FD7]"
                          : "bg-[rgba(24,163,34,.2)] text-[#18A322]"
                    }`}
                    style={{
                      display: "inline-block",
                      transition: "transform 0.2s ease",
                    }}
                  >
                    IN_PROGRESS
                  </span>
                </td>

                <td className="py-4 px-4 text-[13px] text-gray-600">
                  due date
                </td>

                <td className="py-4 px-4">
                  <span
                    className={`px-3 py-1 text-[12px] font-semibold rounded ${
                      "HIGH" === "HIGH"
                        ? "bg-[rgba(129,39,255,.2)] text-[#8127FF]"
                        : "LOW" === "LOW"
                          ? "bg-[rgba(245,86,0,.2)] text-[#F55600]"
                          : "bg-[rgba(21,127,215,.2)] text-[#157FD7]"
                    }`}
                    style={{
                      display: "inline-block",
                      transition: "transform 0.2s ease",
                    }}
                  >
                    HIGH
                  </span>
                </td>

                <td className="py-4 px-4">
                  <div className="flex items-center -space-x-2">
                    <div
                      className="w-7 h-7 rounded-full border-2 border-white bg-gray-100 flex items-center justify-center text-gray-400 text-lg font-light"
                      style={{
                        transition:
                          "background 0.15s ease, transform 0.15s ease",
                        cursor: "pointer",
                      }}
                      onMouseEnter={(e) =>
                        (e.currentTarget.style.backgroundColor = "#e5e7eb")
                      }
                      onMouseLeave={(e) =>
                        (e.currentTarget.style.backgroundColor = "")
                      }
                    >
                      +
                    </div>
                    <Tooltip title={"user"}>
                      <img
                        src={userAvatar}
                        alt="user"
                        className="w-7 h-7 rounded-full border-2 border-white object-cover"
                        style={{
                          transition: "transform 0.2s ease",
                        }}
                      />
                    </Tooltip>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
};

export default TaskTable;
