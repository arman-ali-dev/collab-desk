import userAvatar from "../../assets/userAvatar.png";
import {
  Alert,
  CircularProgress,
  Pagination,
  Skeleton,
  Snackbar,
} from "@mui/material";
import { IconButton, Tooltip } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { useDispatch, useSelector } from "react-redux";
import { use, useState } from "react";
import { deleteUser } from "../../store/admin/userSlice";

const UserTable = () => {
  const { users, deleteUserId, loading, searchResults, searchLoading } =
    useSelector((state) => state.adminUsers);
  const dispatch = useDispatch();

  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [snackType, setSnackType] = useState("success");

  const hasSearchResults = searchResults?.length > 0;
  const displayUsers = hasSearchResults ? searchResults : users;
  const isLoading = loading || searchLoading;

  // Pagination
  const [page, setPage] = useState(1);
  const rowsPerPage = 7;

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedUsers = displayUsers?.slice(startIndex, endIndex);
  const totalPages = Math.ceil(displayUsers?.length / rowsPerPage) || 1;

  // Delete User

  const handleDelete = async (id) => {
    await dispatch(deleteUser(id)).unwrap();
    setOpenSnack(true);
    setSnackType("success");
    setSnackMessage("User deleted");
  };
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
            {isLoading
              ? [1, 2, 3].map((i) => <TableRowSkeleton key={i} />)
              : paginatedUsers?.map((user) => (
                  <tr
                    key={user.id}
                    className="group hover:bg-gray-50 transition-colors"
                  >
                    <td className="py-3 flex items-center gap-3">
                      <img
                        src={user.profileImage || userAvatar}
                        alt={"User"}
                        className="w-8 h-8 rounded-full object-cover border border-gray-200"
                      />
                      <span className="text-[14px] font-medium text-gray-800">
                        {user.fullName}
                      </span>
                    </td>

                    <td className="py-4 px-4 text-[13px] text-gray-600">1</td>

                    <td className="py-4 px-4 text-[13px] text-gray-600">
                      {user.email}
                    </td>

                    <td className="py-4 px-4 text-[13px] text-gray-600">
                      MEMBER
                    </td>

                    <td className="py-4 px-4 text-[13px] text-gray-600">
                      <span className="text-[#F55600] py-1 bg-[rgba(245,86,0,.2)] text-[11px] px-2 rounded-md">
                        {user.status}
                      </span>
                    </td>

                    <td className="py-4 px-4 text-[13px] text-gray-600">
                      {user.designation}
                    </td>
                    <td className="py-4 px-4 text-right ">
                      <Tooltip title="Delete User">
                        <IconButton
                          onClick={() => handleDelete(user.id)}
                          disabled={deleteUserId === user.id}
                          className="min-w-7.5 min-h-7.5"
                          size="small"
                          sx={{
                            color: "#FA2626",
                            "&:hover": {
                              backgroundColor: "rgba(250,38,38,0.1)",
                            },
                          }}
                        >
                          {deleteUserId === user.id && (
                            <CircularProgress size={13} sx={{ color: "red" }} />
                          )}
                          {!deleteUserId && <DeleteIcon fontSize="small" />}
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
          count={totalPages}
          page={page}
          onChange={(event, value) => setPage(value)}
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

      <Snackbar
        open={openSnack}
        autoHideDuration={3000}
        onClose={() => setOpenSnack(false)}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={() => setOpenSnack(false)}
          severity={snackType}
          sx={{ width: "100%", fontSize: "13px" }}
        >
          {snackMessage}
        </Alert>
      </Snackbar>
    </>
  );
};

const TableRowSkeleton = () => (
  <tr>
    <td className="py-3 flex items-center gap-3">
      <Skeleton variant="circular" width={32} height={32} />
      <Skeleton variant="text" width={90} height={18} />
    </td>

    <td className="py-4 px-4">
      <Skeleton variant="text" width={25} />
    </td>

    <td className="py-4 px-4">
      <Skeleton variant="text" width={170} />
    </td>

    <td className="py-4 px-4">
      <Skeleton variant="text" width={80} />
    </td>

    <td className="py-4 px-4">
      <Skeleton
        variant="rectangular"
        width={70}
        height={22}
        sx={{ borderRadius: "6px" }}
      />
    </td>

    <td className="py-4 px-4">
      <Skeleton variant="text" width={120} />
    </td>

    <td className="py-4 px-4">
      <div className="flex justify-end items-center">
        <Skeleton variant="circular" width={22} height={22} />
      </div>
    </td>
  </tr>
);

export default UserTable;
