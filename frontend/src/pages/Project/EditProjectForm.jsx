import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import {
  Select,
  MenuItem,
  Button,
  Snackbar,
  Alert,
  CircularProgress,
} from "@mui/material";

import plusIcon from "../../assets/plus.png";
import uploadIcon from "../../assets/upload.png";
import userAvatar from "../../assets/userAvatar.png";
import removeIcon from "../../assets/remove.png";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { projectSchema } from "../../validations/projectSchema";
import { useDispatch, useSelector } from "react-redux";
import { useRef, useState } from "react";
import { clearSearchResults, searchUser } from "../../store/admin/userSlice";
import { uploadToCloudinary } from "../../util/uploadToCloudinary";
import { createProject, updateProject } from "../../store/admin/projectSlice";

const inputClass =
  "w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm transition-all duration-200  focus:shadow-[0_0_0_2px_rgba(0,0,0,0.08)] ";

const labelClass = "text-[#616161] text-[14px]";

const selectSx = {
  "& .MuiOutlinedInput-notchedOutline": { border: "none" },
  "& .MuiSelect-select": {
    paddingLeft: "16px",
    display: "flex",
    alignItems: "center",
  },
  "&:hover": { boxShadow: "0 0 0 1px #bcbcbc" },
};

const selectClass =
  "border border-[#BCBCBC] w-full outline-none text-[15px] mt-1 rounded-sm h-10.5 box-border";

const EditProjectForm = ({ project, toggleDrawer, open }) => {
  const dispatch = useDispatch();
  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [snackType, setSnackType] = useState("success");

  const {
    register,
    handleSubmit,
    reset,
    watch,
    setValue,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(projectSchema),
    mode: "onBlur",
    defaultValues: {
      title: project.title || "",
      description: project.description || "",
      priority: project.priority || "",
      status: project.status || "",
      members: project.members.map((elem) => elem.id) || [],
      logo: project.logo || "",
      organizationName: project.organizationName || "",
      url: project.url || "",
      progress: project.progress || 0.0,
    },
  });

  const { searchResults, searchLoading } = useSelector(
    (state) => state.adminUsers,
  );

  const [search, setSearch] = useState("");
  const [selectedUsers, setSelectedUsers] = useState(project.members || []);
  const memberIds = watch("members");

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
    setValue("members", [...memberIds, user.id], { shouldValidate: true });
    setSearch("");
    dispatch(clearSearchResults());
  };

  const handleRemoveUser = (id) => {
    setSelectedUsers(selectedUsers.filter((u) => u.id !== id));
    setValue(
      "members",
      memberIds.filter((m) => m !== id),
      { shouldValidate: true },
    );
  };

  const [uploading, setUploading] = useState(false);
  const [logo, setLogo] = useState(project.logo || "");
  const logoInputRef = useRef(null);

  const handleLogoChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    if (file.size > 10 * 1024 * 1024) {
      setSnackType("error");
      setSnackMessage("Image size must be less than 10 MB");
      setOpenSnack(true);
      return;
    }
    try {
      setUploading(true);
      const logo = await uploadToCloudinary(file);

      setLogo(logo);
      setValue("logo", logo);
    } catch (err) {
      console.log(err);

      setSnackType("error");
      setSnackMessage(err.message || "Image upload failed");
      setOpenSnack(true);
    } finally {
      setUploading(false);
    }
  };

  const onSubmit = async (data) => {
    try {
      await dispatch(updateProject({ id: project.id, data })).unwrap();
      setSnackType("success");
      setSnackMessage("Project saved");
      setOpenSnack(true);
      toggleDrawer(false)();
    } catch (err) {
      console.log(err);

      setSnackType("error");
      setSnackMessage(err);
      setOpenSnack(true);
    }
  };

  const { updateLoading } = useSelector((state) => state.adminProjects);
  return (
    <>
      <Drawer
        onClose={toggleDrawer(false)}
        PaperProps={{
          sx: { width: 750, borderRadius: "8px 0 0 8px", overflow: "visible" },
        }}
        anchor="right"
        open={open}
      >
        <div
          onClick={toggleDrawer(false)}
          className="bg-white hover:bg-[#e0e0e0] h-14 w-1.5 rounded-lg absolute top-1/2 -translate-y-1/2 -left-5 -translate-x-1/2 z-50 cursor-grab"
        />

        <Box sx={{ width: 750 }} className="h-full overflow-auto">
          <div className="px-8 py-10">
            <h2 className="font-semibold flex gap-3 items-center">
              <img src={plusIcon} className="w-4" alt="" /> Add New Project
            </h2>

            <form onSubmit={handleSubmit(onSubmit)} className="mt-5 space-y-4">
              <div
                onClick={() => logoInputRef.current.click()}
                className={`w-full h-35  border-2 border-dashed border-[#BCBCBC] hover:border-[#888] rounded-xl flex items-center relative justify-center ${uploading ? "cursor-no-drop" : "cursor-pointer"} transition-colors`}
              >
                {logo ? (
                  <img
                    src={logo}
                    alt="Project Logo"
                    className="w-full h-full object-cover"
                    style={{
                      transition: "transform 0.3s ease",
                    }}
                  />
                ) : (
                  <div className="flex flex-col items-center text-center px-3">
                    <img
                      src={uploadIcon}
                      className="w-6 mb-2 opacity-70"
                      alt=""
                    />
                    <p className="text-[12px] text-[#616161] font-medium">
                      Upload Project Logo
                    </p>
                    <span className="text-[11px] text-[#9E9E9E] mt-1">
                      PNG, JPG up to 10MB
                    </span>
                  </div>
                )}

                <input
                  ref={logoInputRef}
                  type="file"
                  hidden
                  accept="image/*"
                  disabled={uploading}
                  onChange={handleLogoChange}
                />
                {uploading && (
                  <div
                    className="absolute inset-0 z-50 bg-black/40 rounded-xl flex items-center justify-center"
                    style={{ animation: "fadeIn 0.2s ease" }}
                  >
                    <div className="w-7 h-7 border-[3px] border-white border-t-transparent rounded-full animate-spin"></div>
                  </div>
                )}
              </div>

              {errors.logo && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.logo.message}
                </p>
              )}

              <div className="flex gap-4">
                <div className="flex-1">
                  <label className={labelClass}>Project Name</label>
                  <input
                    type="text"
                    {...register("title")}
                    id="title"
                    className={`${inputClass} ${
                      errors.title ? "border-red-500" : "border-[#BCBCBC]"
                    }`}
                  />

                  {errors.title && (
                    <p className="text-red-500 text-[12px] mt-1">
                      {errors.title.message}
                    </p>
                  )}
                </div>
                <div className="flex-1">
                  <label className={labelClass}>URL</label>
                  <input
                    type="text"
                    {...register("url")}
                    id="url"
                    className={`${inputClass} ${
                      errors.url ? "border-red-500" : "border-[#BCBCBC]"
                    }`}
                  />
                  {errors.url && (
                    <p className="text-red-500 text-[12px] mt-1">
                      {errors.url.message}
                    </p>
                  )}
                </div>
              </div>

              <div>
                <label className={labelClass}>Description</label>
                <textarea
                  rows="4"
                  {...register("description")}
                  id="description"
                  className={`${inputClass} resize-none ${
                    errors.description ? " border-red-500" : " border-[#B7B7B7]"
                  }`}
                />

                {errors.description && (
                  <p className="text-red-500 text-[12px] mt-1">
                    {errors.description.message}
                  </p>
                )}
              </div>

              <div className="flex gap-4">
                <div className="flex-1">
                  <label className={labelClass}>Organization Name</label>
                  <input
                    type="text"
                    {...register("organizationName")}
                    id="organizationName"
                    className={`${inputClass} ${
                      errors.organizationName
                        ? "border-red-500"
                        : "border-[#BCBCBC]"
                    }`}
                  />

                  {errors.organizationName && (
                    <p className="text-red-500 text-[12px] mt-1">
                      {errors.organizationName.message}
                    </p>
                  )}
                </div>
                <div className="flex-1">
                  <label className={labelClass}>Progress</label>
                  <input
                    type="text"
                    {...register("progress")}
                    id="progress"
                    className={`${inputClass} ${
                      errors.progress ? "border-red-500" : "border-[#BCBCBC]"
                    }`}
                  />

                  {errors.progress && (
                    <p className="text-red-500 text-[12px] mt-1">
                      {errors.progress.message}
                    </p>
                  )}
                </div>
              </div>

              <div>
                <label className={labelClass}>Add Members</label>

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
                        searchResults.map((user) => {
                          const alreadyAdded = memberIds.includes(user.id);
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
                                  backgroundColor: alreadyAdded
                                    ? "#aaa"
                                    : "#000",
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

                {errors.members && (
                  <p className="text-red-500 text-[12px] mt-1">
                    {errors.members.message}
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

              <div className="flex gap-4">
                <div className="flex-1">
                  <label className={labelClass}>Priority</label>
                  <Select
                    defaultValue={project.priority}
                    fullWidth
                    displayEmpty
                    className={selectClass}
                    sx={selectSx}
                    {...register("priority")}
                  >
                    <MenuItem value="">Select Priority</MenuItem>
                    <MenuItem value="HIGH">High</MenuItem>
                    <MenuItem value="MEDIUM">Medium</MenuItem>
                    <MenuItem value="LOW">Low</MenuItem>
                  </Select>

                  {errors.priority && (
                    <p className="text-red-500 text-[12px] mt-1">
                      {errors.priority.message}
                    </p>
                  )}
                </div>

                <div className="flex-1">
                  <label className={labelClass}>Status</label>
                  <Select
                    defaultValue={project.status}
                    fullWidth
                    displayEmpty
                    className={selectClass}
                    sx={selectSx}
                    {...register("status")}
                  >
                    <MenuItem value="">Select Status</MenuItem>
                    <MenuItem value="ACTIVE">Active</MenuItem>
                    <MenuItem value="ON_HOLD">On Hold</MenuItem>
                    <MenuItem value="COMPLETED">Completed</MenuItem>
                  </Select>
                  {errors.status && (
                    <p className="text-red-500 text-[12px] mt-1">
                      {errors.status.message}
                    </p>
                  )}
                </div>
              </div>

              <div className="flex gap-2 mt-10">
                <Button
                  type="button"
                  sx={{
                    textTransform: "capitalize",
                    border: "1px solid #BCBCBC",
                    color: "#000",
                    paddingX: "20px",
                    fontSize: "14px",
                    "&:hover": { backgroundColor: "#f5f5f5" },
                  }}
                >
                  <span className="font-medium">Reset Data</span>
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
                    minWidth: "126px",
                    "&:hover": { backgroundColor: "#222" },
                  }}
                >
                  {updateLoading && (
                    <CircularProgress size={15} sx={{ color: "#fff" }} />
                  )}

                  {!updateLoading && <span>Save Changes</span>}
                </Button>
              </div>
            </form>
          </div>
        </Box>
      </Drawer>

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

export default EditProjectForm;
