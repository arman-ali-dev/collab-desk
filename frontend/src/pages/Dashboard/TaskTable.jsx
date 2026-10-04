import {
  Divider,
  IconButton,
  Menu,
  MenuItem,
  Pagination,
  Skeleton,
  Tooltip,
} from "@mui/material";

import userAvatar from "../../assets/userAvatar.png";
import filterIcon from "../../assets/filter.png";
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { filterTasks } from "../../store/admin/taskSlice";

const TaskTable = () => {
  const dispatch = useDispatch();
  const { tasks, loading } = useSelector((state) => state.adminTasks);

  // Pagination
  const [page, setPage] = useState(1);
  const rowsPerPage = 4;

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedTasks = tasks?.slice(startIndex, endIndex);
  const totalPages = Math.ceil(tasks?.length / rowsPerPage) || 1;

  // filter
  const [filterAnchorEl, setFilterAnchorEl] = useState(null);
  const openFilterDropDown = Boolean(filterAnchorEl);

  const handleClick = (event) => setFilterAnchorEl(event.currentTarget);
  const handleCloseFilterDropDown = () => setFilterAnchorEl(null);

  const [status, setStatus] = useState(null);
  const [priority, setPriority] = useState(null);

  useEffect(() => {
    console.log(status, priority);
    dispatch(filterTasks({ status, priority }));
  }, [dispatch, status, priority]);
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
            onClick={handleClick}
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

          <Menu
            anchorEl={filterAnchorEl}
            open={openFilterDropDown}
            onClose={handleCloseFilterDropDown}
            PaperProps={{
              sx: {
                width: 180,
                borderRadius: "10px",
                boxShadow: "0 8px 24px rgba(0,0,0,0.12)",
                animation: "fadeSlideDown 0.2s ease",
              },
            }}
            transformOrigin={{ horizontal: "right", vertical: "top" }}
            anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
          >
            {[
              {
                label: "All Tasks",
                action: () => {
                  setStatus(null);
                  setPriority(null);
                },
              },
            ].map((item, i) => (
              <MenuItem
                key={i}
                onClick={() => {
                  item.action();
                  handleCloseFilterDropDown();
                }}
                sx={{ fontSize: "13px", fontWeight: 700 }}
              >
                {item.label}
              </MenuItem>
            ))}
            <Divider />
            {[
              { label: "To-Do Tasks", status: "TO_DO" },
              { label: "Doing Tasks", status: "IN_PROGRESS" },
              { label: "Review Tasks", status: "REVIEW" },
              { label: "Done Tasks", status: "DONE" },
            ].map((item, i) => (
              <MenuItem
                key={i}
                onClick={() => {
                  setStatus(item.status);
                  setPriority(null);
                  handleCloseFilterDropDown();
                }}
                sx={{ fontSize: "13px", fontWeight: 700 }}
              >
                {item.label}
              </MenuItem>
            ))}
            <Divider />
            {[
              { label: "High Priority", priority: "HIGH" },
              { label: "Medium Priority", priority: "MEDIUM" },
              { label: "Low Priority", priority: "LOW" },
            ].map((item, i) => (
              <MenuItem
                key={i}
                onClick={() => {
                  setStatus(null);
                  setPriority(item.priority);
                  handleCloseFilterDropDown();
                }}
                sx={{ fontSize: "13px", fontWeight: 700 }}
              >
                {item.label}
              </MenuItem>
            ))}
          </Menu>
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
              {loading
                ? [1, 2, 3].map((i) => <TableSkeletonRow />)
                : paginatedTasks?.map((t) => (
                    <tr
                      key={t.id}
                      style={{
                        transition: `opacity 0.35s ease 100ms, transform 0.35s ease 100ms, background 0.15s ease`,
                        cursor: "default",
                      }}
                    >
                      <td className="py-4 pr-4 text-[13px] font-medium text-gray-700">
                        {t.title}
                      </td>

                      <td className="py-4 px-4 text-[13px] font-medium text-gray-700">
                        {t.description}
                      </td>

                      <td className="py-4 px-4 text-[13px] text-gray-600">
                        {t.createdAt.split("T")[0]}
                      </td>

                      <td className="py-4 px-4">
                        <span
                          className={`px-3 py-1 text-[12px] font-semibold rounded ${
                            t.status === "IN_PROGRESS"
                              ? "bg-[rgba(245,86,0,.2)] text-[#F55600]"
                              : t.status === "TO_DO"
                                ? "bg-[rgba(21,127,215,.2)] text-[#157FD7]"
                                : "bg-[rgba(24,163,34,.2)] text-[#18A322]"
                          }`}
                          style={{
                            display: "inline-block",
                            transition: "transform 0.2s ease",
                          }}
                        >
                          {t.status}
                        </span>
                      </td>

                      <td className="py-4 px-4 text-[13px] text-gray-600">
                        {t.dueDate}
                      </td>

                      <td className="py-4 px-4">
                        <span
                          className={`px-3 py-1 text-[12px] font-semibold rounded ${
                            t.priority === "HIGH"
                              ? "bg-[rgba(129,39,255,.2)] text-[#8127FF]"
                              : t.priority === "LOW"
                                ? "bg-[rgba(245,86,0,.2)] text-[#F55600]"
                                : "bg-[rgba(21,127,215,.2)] text-[#157FD7]"
                          }`}
                          style={{
                            display: "inline-block",
                            transition: "transform 0.2s ease",
                          }}
                        >
                          {t.priority}
                        </span>
                      </td>

                      <td className="py-4 px-4">
                        <div className="flex items-center -space-x-2">
                          {t.assignedTo.map((u) => (
                            <Tooltip key={u.id} title={u.fullName}>
                              <img
                                src={u.profileImage || userAvatar}
                                alt="user"
                                className="w-7 h-7 rounded-full border-2 border-white object-cover"
                                style={{
                                  transition: "transform 0.2s ease",
                                }}
                              />
                            </Tooltip>
                          ))}
                        </div>
                      </td>
                    </tr>
                  ))}
            </tbody>
          </table>

          {!loading && paginatedTasks?.length === 0 && (
            <p className="text-center mt-5 font-medium text-[14px]">No Tasks</p>
          )}
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
      </div>
    </>
  );
};

const TableSkeletonRow = () => (
  <tr>
    <td className="py-4 pr-4">
      <Skeleton variant="text" width="80%" height={20} />
    </td>
    <td className="py-4 px-4">
      <Skeleton variant="text" width="60%" height={20} />
    </td>
    <td className="py-4 px-4">
      <Skeleton variant="text" width="70%" height={20} />
    </td>
    <td className="py-4 px-4">
      <Skeleton variant="rounded" width={90} height={26} />
    </td>
    <td className="py-4 px-4">
      <Skeleton variant="text" width="70%" height={20} />
    </td>
    <td className="py-4 px-4">
      <Skeleton variant="rounded" width={70} height={26} />
    </td>
    <td className="py-4 px-4">
      <div className="flex gap-2">
        <Skeleton className="-mr-5" variant="circular" width={28} height={28} />
        <Skeleton variant="circular" width={28} height={28} />
      </div>
    </td>
  </tr>
);

export default TaskTable;
