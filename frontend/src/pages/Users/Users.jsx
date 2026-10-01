import { IconButton, Pagination } from "@mui/material";
import plusIcon from "../../assets/plus.png";
import searchIcon from "../../assets/search.png";
import filterIcon from "../../assets/filter.png";
import UserTable from "./UserTable";
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { getAllUsers } from "../../store/admin/userSlice";

const Users = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getAllUsers());
  }, [dispatch]);

  return (
    <>
      <div className=" mt-4 mx-8 relative">
        <div className="bg-white shadow flex justify-between items-center rounded-lg px-5 py-2.5">
          <div className="flex gap-2 items-center">
            <div className="bg-[#EFEFEF] h-8 w-8 rounded-lg flex justify-center items-center">
              <img className="w-3" src={searchIcon} alt="" />
            </div>
            <input
              onChange={(e) => setSearch(e.target.value)}
              className="placeholder:text-[#000000] w-125 border-0 mt-1 outline-0 text-[13px] placeholder:text-[13px] opacity-80"
              type="text"
              placeholder="Search for names, emails or designations..."
            />
          </div>

          <div className="flex gap-2">
            <IconButton
              sx={{
                width: 36,
                height: 36,
                backgroundColor: "#EFEFEF",
                cursor: "pointer",
                borderRadius: "8px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                "&:hover": {
                  backgroundColor: "#EFEFEF",
                },
              }}
            >
              <img src={filterIcon} alt="" className="w-4" />
            </IconButton>

            <IconButton
              sx={{
                width: 36,
                height: 36,
                backgroundColor: "#EFEFEF",
                cursor: "pointer",
                borderRadius: "8px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                "&:hover": {
                  backgroundColor: "#EFEFEF",
                },
              }}
            >
              <img className="w-3.5" src={plusIcon} alt="" />
            </IconButton>
          </div>
        </div>

        <div className="bg-white shadow mt-4 rounded-lg px-5 py-4">
          <div className="flex justify-between items-center">
            <h3 className="text-[16px] font-semibold">Users List</h3>
          </div>

          <UserTable />
        </div>
      </div>
    </>
  );
};

export default Users;
