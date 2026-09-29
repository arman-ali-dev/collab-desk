import searchIcon from "../../assets/search.png";
import chevronIcon from "../../assets/chevron.png";
import { IconButton } from "@mui/material";
import DateAndTask from "./DateAndTask";

const Calendar = () => {
  const monthNames = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ];

  return (
    <>
      <div className="mt-4 mx-8 relative">
        <div className="bg-white shadow flex justify-between items-center rounded-lg px-5 py-2.5">
          <div className="flex gap-2 items-center">
            <div className="bg-[#EFEFEF] h-8 w-8 rounded-lg flex justify-center items-center">
              <img className="w-3" src={searchIcon} alt="" />
            </div>
            <input
              className="placeholder:text-[#000000] border-0 mt-1 outline-0 text-[13px] placeholder:text-[13px] opacity-80"
              type="text"
              placeholder="Search task, project..."
            />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow mt-6 px-6 py-5">
          <div className="flex gap-6 items-center justify-center">
            <IconButton
              sx={{
                width: 35,
                height: 35,
                backgroundColor: "#000",
                cursor: "pointer",
                borderRadius: "8px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                "&:hover": {
                  backgroundColor: "#000",
                },
              }}
            >
              <img className="w-6" src={chevronIcon} alt="" />
            </IconButton>
            <p className="text-[15px] font-medium">April 2026</p>
            <IconButton
              sx={{
                width: 35,
                height: 35,
                backgroundColor: "#000",
                cursor: "pointer",
                borderRadius: "8px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                "&:hover": {
                  backgroundColor: "#000",
                },
              }}
            >
              <img className="w-6 rotate-180" src={chevronIcon} alt="" />
            </IconButton>
          </div>

          <div className="flex justify-between mt-6 border-[rgba(200,200,200,.6)] border-t pt-5">
            {["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].map((day) => (
              <p
                key={day}
                className="opacity-70 font-medium text-[13px] uppercase w-[14.28%] text-center"
              >
                {day}
              </p>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-lg shadow mt-4 px-5 py-6">
          <div className="flex gap-3 mb-3 last:mb-0">
            <DateAndTask />
          </div>
        </div>
      </div>
    </>
  );
};

export default Calendar;
