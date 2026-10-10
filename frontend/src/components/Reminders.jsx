import { CircularProgress } from "@mui/material";
import { useSelector } from "react-redux";

const Reminders = ({ remindersRef, showReminders }) => {
  const { reminders, remindersLoading } = useSelector(
    (state) => state.memberTasks,
  );
  return (
    <>
      <div
        ref={remindersRef}
        className={`dropdown ${
          showReminders ? "show" : ""
        } bg-[#EFEFEF] z-999 max-w-73.5 h-77.5 w-65 absolute`}
      >
        {reminders.length === 0 ? (
          <p className="text-center text-[12px] text-gray-400 py-8">
            No reminders yet
          </p>
        ) : remindersLoading ? (
          <div className="flex justify-center items-center h-full">
            <CircularProgress color="#000" size={20} />
          </div>
        ) : (
          reminders?.map((r) => (
            <div
              key={r.id}
              className={`notification-row flex relative p-2.5 border-b border-b-[#e1e8ed] gap-3 items-start `}
            >
              <div>
                <p className="text-black font-semibold capitalize text-[14px]">
                  {r.title}
                </p>
                <p className="text-[#333333] text-[12px] -mt-1 font-medium">
                  {r.message}
                </p>
                <p className="text-[#666666] text-[13px] mt-1 font-medium">
                  {r.dueDate}
                </p>
              </div>

              <div>
                <p
                  className={`text-white inline-block text-[11px] px-1.5 absolute top-0 right-0 ${
                    r.level === "OVERDUE"
                      ? "bg-[rgba(250,38,38,.8)]"
                      : r.level === "TODAY"
                        ? "bg-[#18A322]"
                        : "bg-[#157FD7]"
                  }`}
                >
                  {r.level.charAt(0) + r.level.slice(1).toLowerCase()}
                </p>
              </div>
            </div>
          ))
        )}
      </div>
    </>
  );
};

export default Reminders;
