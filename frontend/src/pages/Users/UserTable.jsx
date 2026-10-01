import userAvatar from "../../assets/userAvatar.png";
import { Pagination } from "@mui/material";
import { IconButton, Tooltip } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { useSelector } from "react-redux";

const UserTable = () => {
  const { users, loading, error } = useSelector((state) => state.adminUsers);

  return (
    <>
      <div className="overflow-x-auto mt-5">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="text-[13px] font-semibold border-b border-gray-300">
              <th className="pb-4 pr-4">Name</th>
              <th className="pb-4 px-4">User Id</th>
              <th className="pb-4 px-4">Email</th>
              <th className="pb-4 px-4">Role</th>
              <th className="pb-4 px-4">Status</th>
              <th className="pb-4 pl-4">Designation</th>
              <th className="pb-4 pl-4 text-right">Action</th>
            </tr>
          </thead>

          <tbody>
            {users?.map((user) => (
              <tr className="group hover:bg-gray-50 transition-colors">
                <td className="py-3 flex items-center gap-3">
                  <img
                    src={userAvatar}
                    alt={"User"}
                    className="w-8 h-8 rounded-full object-cover border border-gray-200"
                  />
                  <span className="text-[14px] font-medium text-gray-800">
                    User
                  </span>
                </td>

                <td className="py-4 px-4 text-[13px] text-gray-600">1</td>

                <td className="py-4 px-4 text-[13px] text-gray-600">
                  user@example.com
                </td>

                <td className="py-4 px-4 text-[13px] text-gray-600">MEMBER</td>

                <td className="py-4 px-4 text-[13px] text-gray-600">
                  <span className="text-[#F55600] py-1 bg-[rgba(245,86,0,.2)] text-[11px] px-2 rounded-md">
                    ACTIVE
                  </span>
                </td>

                <td className="py-4 px-4 text-[13px] text-gray-600">
                  Software Engineer
                </td>
                <td className="py-4 px-4 text-right ">
                  <Tooltip title="Delete User">
                    <IconButton
                      className="min-w-7.5 min-h-7.5"
                      size="small"
                      sx={{
                        color: "#FA2626",
                        "&:hover": {
                          backgroundColor: "rgba(250,38,38,0.1)",
                        },
                      }}
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex justify-center mt-10 mb-2">
        <Pagination
          count={4}
          page={1}
          shape="rounded"
          sx={{
            "& .MuiPaginationItem-root": {
              "&:hover": {
                backgroundColor: "#f5f5f5",
              },
            },
            "& .Mui-selected": {
              backgroundColor: "black !important",
              color: "white !important",
              "&:hover": {
                backgroundColor: "#333 !important",
              },
            },
          }}
        />
      </div>
    </>
  );
};

export default UserTable;
