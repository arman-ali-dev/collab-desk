import { Button, MenuItem, Select } from "@mui/material";
import { useSelector } from "react-redux";
import CreateNewTaskForm from "./CreateNewTaskForm";
import { useState } from "react";

const TaskStatusCard = () => {
  const { tasks } = useSelector((state) => state.adminTasks);

  const toDoTasks = tasks?.filter((task) => task.status === "TO_DO");
  const inProgressTasks = tasks?.filter(
    (task) => task.status === "IN_PROGRESS",
  );

  const reviewTasks = tasks?.filter((task) => task.status === "REVIEW");
  const doneTasks = tasks?.filter((task) => task.status === "DONE");

  const totalTasks = tasks?.length - reviewTasks?.length || 0;

  const toDoPercentage =
    totalTasks > 0 ? (toDoTasks.length / totalTasks) * 100 : 0;

  const inProgressPercentage =
    totalTasks > 0 ? (inProgressTasks.length / totalTasks) * 100 : 0;

  const donePercentage =
    totalTasks > 0 ? (doneTasks.length / totalTasks) * 100 : 0;

  const [open, setOpen] = useState(false);

  const toggleDrawer = (value) => () => {
    setOpen(value);
  };

  return (
    <>
      <div
        className="rounded-2xl flex flex-col justify-between px-6 py-5 bg-white h-full"
        style={{
          transition:
            "opacity 0.55s ease 0.2s, transform 0.55s cubic-bezier(0.22,1,0.36,1) 0.2s, box-shadow 0.25s ease",
          boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
        }}
      >
        <div>
          <div className="flex justify-between items-center">
            <h3 className="text-[16px] font-medium" style={{ opacity: 0.85 }}>
              Task Status
            </h3>
            <Select
              defaultValue=""
              displayEmpty
              className="outline-none text-[13px] font-medium w-21.5"
              sx={{
                height: "32px",
                borderRadius: "8px",
                backgroundColor: "#ffffff",
                "& .MuiOutlinedInput-notchedOutline": {
                  border: "1px solid #E0E0E0",
                  transition: "border-color 0.2s ease",
                },
                "&:hover .MuiOutlinedInput-notchedOutline": {
                  borderColor: "#BCBCBC",
                },
                "& .MuiSelect-select": {
                  paddingLeft: "15px",
                  paddingRight: "25px",
                  display: "flex",
                  alignItems: "center",
                  fontSize: "13px",
                  fontWeight: "500",
                  opacity: ".8",
                },
                "& .MuiSvgIcon-root": {
                  right: "2px",
                  fontSize: "18px",
                },
              }}
            >
              <MenuItem value="" sx={{ fontSize: "13px" }}>
                All Time
              </MenuItem>
            </Select>
          </div>

          <div className="mt-2">
            <h3
              className="font-semibold text-[25px]"
              style={{
                opacity: 0.85,
                transition: "color 0.2s ease",
                color: "#111",
              }}
            >
              {tasks?.length}
            </h3>
            <p
              className="font-medium text-[12px] -mt-0.5"
              style={{ opacity: 0.85 }}
            >
              Total Tasks
            </p>
          </div>

          <div className="flex flex-1 w-full gap-1 mt-5 overflow-hidden rounded-sm">
            <span
              className="inline-block h-1.5"
              style={{
                width: toDoPercentage + "%",
                backgroundColor: "#18A322",
                transition: `width 0.8s`,
                borderRadius: "2px 0 0 2px",
              }}
            />

            <span
              className="inline-block h-1.5"
              style={{
                width: inProgressPercentage + "%",
                backgroundColor: "#157FD7",
                transition: `width 0.8s  `,
                borderRadius: "2px 0 0 2px",
              }}
            />

            <span
              className="inline-block h-1.5"
              style={{
                width: donePercentage + "%",
                backgroundColor: "#F55600",
                transition: `width 0.8s `,
                borderRadius: "2px 0 0 2px",
              }}
            />
          </div>

          <div className="mt-7 space-y-3">
            <div
              className={
                "flex justify-between items-center pb-2 border-b border-[#CEC6C6]"
              }
              style={{
                transition: `opacity 0.4s , transform 0.4s `,
              }}
            >
              <div className="flex gap-1.5 items-center">
                <span
                  className="inline-block h-4 w-4 rounded-sm"
                  style={{
                    backgroundColor: "rgba(21,127,215)",
                    transition: "transform 0.2s ease",
                  }}
                />
                <p className="text-[13px] mt-0.5 font-medium">To-Do</p>
              </div>
              <div
                className="text-[11px] px-2 py-0.5 rounded-sm font-medium"
                style={{
                  color: "#157FD7",
                  backgroundColor: "rgba(21,127,215, .2)",
                  transition: "transform 0.2s ease",
                }}
              >
                {toDoTasks?.length}+
              </div>
            </div>

            <div
              className={
                "flex justify-between items-center pb-2 border-b border-[#CEC6C6]"
              }
              style={{
                transition: `opacity 0.4s , transform 0.4s `,
              }}
            >
              <div className="flex gap-1.5 items-center">
                <span
                  className="inline-block h-4 w-4 rounded-sm"
                  style={{
                    backgroundColor: "rgba(245,86,0)",
                    transition: "transform 0.2s ease",
                  }}
                />
                <p className="text-[13px] mt-0.5 font-medium">In Progress</p>
              </div>
              <div
                className="text-[11px] px-2 py-0.5 rounded-sm font-medium"
                style={{
                  color: "#F55600",
                  backgroundColor: "rgba(245,86,0,.2)",
                  transition: "transform 0.2s ease",
                }}
              >
                {inProgressTasks?.length}+
              </div>
            </div>

            <div
              className={
                "flex justify-between items-center pb-2 border-b border-[#CEC6C6]"
              }
              style={{
                transition: `opacity 0.4s , transform 0.4s `,
              }}
            >
              <div className="flex gap-1.5 items-center">
                <span
                  className="inline-block h-4 w-4 rounded-sm"
                  style={{
                    backgroundColor: "rgba(24,163,34)",
                    transition: "transform 0.2s ease",
                  }}
                />
                <p className="text-[13px] mt-0.5 font-medium">Done</p>
              </div>
              <div
                className="text-[11px] px-2 py-0.5 rounded-sm font-medium"
                style={{
                  color: "#18A322",
                  backgroundColor: "rgba(24,163,34,.2)",
                  transition: "transform 0.2s ease",
                }}
              >
                {doneTasks?.length}+
              </div>
            </div>
          </div>
        </div>

        <div>
          <Button
            onClick={toggleDrawer(true)}
            fullWidth
            sx={{
              textTransform: "capitalize",
              backgroundColor: "#000",
              border: "1px solid #000",
              color: "#fff",
              paddingX: "20px",
              fontSize: "13px",
              borderRadius: "5px",
              transition:
                "background 0.2s ease, transform 0.15s ease, box-shadow 0.2s ease !important",
              "&:hover": {
                backgroundColor: "#222 !important",
                transform: "translateY(-1px)",
                boxShadow: "0 4px 14px rgba(0,0,0,0.2)",
              },
              "&:active": {
                transform: "scale(0.97)",
              },
            }}
          >
            <span>Create New Task</span>
          </Button>
        </div>
      </div>

      <CreateNewTaskForm toggleDrawer={toggleDrawer} open={open} />
    </>
  );
};

export default TaskStatusCard;
