import Box from "@mui/material/Box";

import {
  Select,
  MenuItem,
  Button,
  Drawer,
  Snackbar,
  Alert,
  CircularProgress,
} from "@mui/material";
import removeIcon from "../../assets/remove.png";
import uploadIcon from "../../assets/upload.png";
import userAvatar from "../../assets/userAvatar.png";
import { clearSearchResults, searchUser } from "../../store/admin/userSlice";
import { useDispatch, useSelector } from "react-redux";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { taskSchema } from "../../validations/taskSchema";
import { useEffect, useState } from "react";
import { createTask } from "../../store/admin/taskSlice";
import { addTasksInProjectTasks } from "../../store/member/taskSlice";

const inputClass =
  "w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm";

const selectClass =
  "border border-[#BCBCBC] w-full outline-none mt-1 rounded-sm h-10.5 box-border";

const labelClass = "text-[#616161] text-[14px]";

const selectSx = {
  "& .MuiOutlinedInput-notchedOutline": { border: "none" },
  "& .MuiSelect-select": {
    paddingLeft: "16px",
    display: "flex",
    alignItems: "center",
    fontSize: "13px",
    fontWeight: "400",
    color: "#000",
  },
};

const CreateNewTaskForm = ({ toggleDrawer, open, projectId, status }) => {
  console.log("projectId, ", projectId);

  const {
    register,
    handleSubmit,
    reset,
    watch,
    setValue,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(taskSchema),
    mode: "onBlur",
    defaultValues: {
      title: "",
      description: "",
      status,
      category: "",
      priority: "",
      dueDate: "",
      estimatedTime: "",
      projectId,
      assignedTo: [],
    },
  });

  const handleReset = () => {
    reset({
      title: "",
      description: "",
      status,
      category: "",
      priority: "",
      dueDate: "",
      estimatedTime: "",
      projectId,
      assignedTo: [],
    });
  };

  useEffect(() => {
    if (projectId) {
      handleReset();
    }
  }, [projectId, reset]);

  const dispatch = useDispatch();

  const [search, setSearch] = useState("");
  const [selectedUsers, setSelectedUsers] = useState([]);
  const assignedToIds = watch("assignedTo");

  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [snackType, setSnackType] = useState("success");

  const handleSearch = (e) => {
    const value = e.target.value;
    setSearch(value);

    if (value.trim().length < 2) {
      dispatch(clearSearchResults());
      return;
    }

    dispatch(searchUser(value.trim()));
  };

  const handleAddUser = (user) => {
    setSelectedUsers([...selectedUsers, user]);
    setValue("assignedTo", [...assignedToIds, user.id], {
      shouldValidate: true,
    });
    setSearch("");
    dispatch(clearSearchResults());
  };

  const handleRemoveUser = (id) => {
    setSelectedUsers(selectedUsers.filter((u) => u.id !== id));
    setValue(
      "assignedTo",
      assignedToIds.filter((m) => m !== id),
      { shouldValidate: true },
    );
  };

  const { searchResults, searchLoading } = useSelector(
    (state) => state.adminUsers,
  );

  const { createLoading } = useSelector((state) => state.adminTasks);

  const onSubmit = async (data) => {
    try {
      const createdTask = await dispatch(createTask(data)).unwrap();
      console.log("created Task", createTask);

      dispatch(addTasksInProjectTasks(createdTask));
      setSnackType("success");
      setSnackMessage("Task created");
      setOpenSnack(true);
      toggleDrawer(false)();
      reset();
      setSelectedUsers([]);
    } catch (err) {
      console.log(err);

      setSnackType("error");
      setSnackMessage(err);
      setOpenSnack(true);
    }
  };

  console.log(errors);

  const form = () => (
    <Box sx={{ width: 750 }} className="overflow-y-scroll" role="presentation">
      <div className="px-8 py-10">
        <h2 className="font-semibold ">Create New Task</h2>

        <form onSubmit={handleSubmit(onSubmit)} className="mt-5 space-y-4">
          <div className="flex gap-4 ">
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Task Title</label>
              <input
                {...register("title")}
                id="title"
                className={`${inputClass} ${
                  errors.title ? "border-red-500" : "border-[#BCBCBC]"
                }`}
                type="text"
              />

              {errors.title && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.title.message}
                </p>
              )}
            </div>
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Category</label>
              <Select
                fullWidth
                defaultValue=""
                displayEmpty
                name="category"
                className={selectClass}
                sx={selectSx}
                {...register("category")}
              >
                <MenuItem
                  defaultChecked
                  value=""
                  sx={{ fontSize: "13px", fontWeight: "600" }}
                >
                  Select Category
                </MenuItem>
                <MenuItem
                  value="DESIGN"
                  sx={{ fontSize: "13px", fontWeight: "600" }}
                >
                  Design
                </MenuItem>

                <MenuItem value="DEVELOPMENT" sx={{ fontSize: "13px" }}>
                  Development
                </MenuItem>

                <MenuItem value="TESTING" sx={{ fontSize: "13px" }}>
                  Testing
                </MenuItem>

                <MenuItem value="BUG" sx={{ fontSize: "13px" }}>
                  Bug
                </MenuItem>

                <MenuItem value="RESEARCH" sx={{ fontSize: "13px" }}>
                  Research
                </MenuItem>
              </Select>
              {errors.category && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.category.message}
                </p>
              )}
            </div>
          </div>

          <div className="flex gap-4 ">
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Description</label>

              <textarea
                {...register("description")}
                id="description"
                className={`${inputClass} resize-none ${
                  errors.description ? " border-red-500" : " border-[#B7B7B7]"
                }`}
              ></textarea>

              {errors.description && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.description.message}
                </p>
              )}
            </div>
          </div>

          <div className="flex gap-4 ">
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Priority</label>

              <Select
                name="priority"
                fullWidth
                defaultValue=""
                displayEmpty
                className={selectClass}
                sx={selectSx}
                {...register("priority")}
              >
                <MenuItem
                  defaultChecked
                  value=""
                  sx={{ fontSize: "13px", fontWeight: "600" }}
                >
                  Select Priority
                </MenuItem>
                <MenuItem
                  value="HIGH"
                  sx={{ fontSize: "13px", fontWeight: "600" }}
                >
                  High
                </MenuItem>
                <MenuItem value="MEDIUM" sx={{ fontSize: "13px" }}>
                  Medium
                </MenuItem>
                <MenuItem value="LOW" sx={{ fontSize: "13px" }}>
                  Low
                </MenuItem>
              </Select>
              {errors.priority && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.priority.message}
                </p>
              )}
            </div>
          </div>

          <div className="flex gap-4">
            <div className="flex-1">
              <label className={labelClass}>Estimated Time (hours)</label>
              <input
                name="estimatedTime"
                type="number"
                min="0"
                step="0.5"
                placeholder="e.g. 2.5"
                {...register("estimatedTime")}
                id="estimatedTime"
                className={`${inputClass} ${
                  errors.estimatedTime ? "border-red-500" : "border-[#BCBCBC]"
                }`}
              />
              <p className="text-[11px] text-[#9E9E9E] mt-1">
                Approx time required to complete the task
              </p>

              {errors.estimatedTime && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.estimatedTime.message}
                </p>
              )}
            </div>

            <div className="flex-1">
              <label className={labelClass}>Due Date</label>
              <input
                {...register("dueDate")}
                id="dueDate"
                className={`${inputClass} ${
                  errors.dueDate ? "border-red-500" : "border-[#BCBCBC]"
                }`}
                type="date"
              />
              {errors.dueDate && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.dueDate.message}
                </p>
              )}
            </div>
          </div>
          <div>
            <label className={labelClass}>Assigned To</label>

            <div className="relative">
              <input
                type="text"
                value={search}
                onChange={handleSearch}
                placeholder="Search user..."
                className={inputClass}
              />

              {search.trim().length >= 2 && (
                <div className="absolute w-full bg-white border border-[#BCBCBC] mt-1 z-20 max-h-56 overflow-y-auto rounded-sm shadow-lg">
                  {searchLoading && (
                    <p className="px-4 py-3 text-[13px]">Searching...</p>
                  )}

                  {!searchLoading && searchResults.length === 0 && (
                    <p className="px-4 py-3 text-[13px]">No users found</p>
                  )}

                  {!searchLoading &&
                    searchResults?.map((user) => {
                      const alreadyAdded = assignedToIds.includes(user.id);
                      return (
                        <div
                          key={user.id}
                          className="px-4 py-2 flex justify-between items-center hover:bg-[#f5f5f5]"
                        >
                          <div className="flex gap-2 items-center">
                            <img
                              src={user.profileImage || userAvatar}
                              className="w-6 h-6 rounded-full"
                              alt=""
                            />
                            <span className="text-sm">{user.fullName}</span>
                          </div>
                          <Button
                            type="button"
                            disabled={alreadyAdded}
                            onClick={() => handleAddUser(user)}
                            sx={{
                              textTransform: "capitalize",
                              fontSize: "12px",
                              backgroundColor: alreadyAdded ? "#aaa" : "#000",
                              color: "#fff",
                            }}
                          >
                            {alreadyAdded ? "Added" : "Add"}
                          </Button>
                        </div>
                      );
                    })}
                </div>
              )}
            </div>

            {errors.assignedTo && (
              <p className="text-red-500 text-[12px] mt-1">
                {errors.assignedTo.message}
              </p>
            )}

            <div className="flex gap-2.5 mt-4 flex-wrap">
              {selectedUsers.map((user) => (
                <div key={user.id} className="relative">
                  <img
                    className="w-8.5 h-8.5 rounded-full object-cover"
                    src={user.profileImage || userAvatar}
                    alt=""
                    title={user.fullName}
                  />
                  <img
                    className="w-3.5 cursor-pointer absolute top-0 -right-0.5"
                    src={removeIcon}
                    alt="Remove"
                    onClick={() => handleRemoveUser(user.id)}
                  />
                </div>
              ))}
            </div>
          </div>

          <div className="mt-5">
            <label className={labelClass}>Upload Document</label>
            <label className="text-[#616161] text-[14px] block">
              Drag and drop document to upload your support task
            </label>
            <div
              className="border-[#9F9F9F] text-center mt-3 border-dashed border-2 rounded-xl py-5 transition bg-[#e9e9e9] "
              //   bg-[#F4EFEF]
            >
              <img
                src={uploadIcon}
                className="w-14 mx-auto h-14 opacity-50"
                alt=""
              />

              <p className="text-[#252323] font-medium text-[14px]">
                Choose a file or drag & drop it here.
              </p>

              <p className="text-[#707070] text-[13px] mb-3">
                txt, docx, pdf, jpeg, xlsx — Up to 50MB
              </p>

              <Button
                onClick={() => document.getElementById("docInput").click()}
                sx={{
                  textTransform: "capitalize",
                  border: "1px solid #BCBCBC",
                  color: "#000",
                  paddingX: "20px",
                  fontSize: "12px",
                  borderRadius: "10px",
                }}
              >
                <span className="font-medium">Browse Files</span>
              </Button>

              <input
                id="docInput"
                type="file"
                hidden
                multiple
                accept=".txt,.doc,.docx,.pdf,.jpeg,.jpg,.xlsx,.png"
              />
            </div>

            <div className="mt-4 space-y-2">
              <div className="flex justify-between items-center bg-white border rounded-lg px-3 py-2">
                <span className="text-[13px] truncate max-w-[320px]">
                  file 1
                </span>

                <button
                  type="button"
                  className="text-red-500 text-[12px] font-medium"
                >
                  Remove
                </button>
              </div>
            </div>
          </div>

          <div className="flex gap-2 mt-10">
            <Button
              type="button"
              onReset={() => reset()}
              sx={{
                textTransform: "capitalize",
                border: "1px solid #BCBCBC",
                color: "#000",
                paddingX: "20px",
                fontSize: "14px",
              }}
            >
              <span className="font-medium"> Reset Data</span>
            </Button>

            <Button
              type="submit"
              sx={{
                textTransform: "capitalize",
                backgroundColor: "#000",
                border: "1px solid #000",
                color: "#fff",
                paddingX: "20px",
                fontSize: "14px",
                minWidth: "127px",
              }}
            >
              {createLoading && (
                <CircularProgress size={15} sx={{ color: "#fff" }} />
              )}

              {!createLoading && <span>Create Task</span>}
            </Button>
          </div>
        </form>
      </div>
    </Box>
  );

  return (
    <>
      <div>
        <Drawer
          onClose={toggleDrawer(false)}
          PaperProps={{
            sx: {
              width: 750,
              borderRadius: "8px 0 0 8px",
              overflow: "visible",
            },
          }}
          anchor="right"
          open={open}
        >
          <div
            onClick={toggleDrawer(false)}
            className="bg-white h-14 w-1.5 rounded-lg absolute top-1/2 -translate-y-1/2   -left-5 -translate-x-1/2  z-99999999 cursor-grab"
          ></div>
          {form()}
        </Drawer>
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

export default CreateNewTaskForm;
