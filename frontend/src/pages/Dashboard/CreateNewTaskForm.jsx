import Box from "@mui/material/Box";

import { Select, MenuItem, Button, Drawer, Snackbar } from "@mui/material";
import removeIcon from "../../assets/remove.png";
import uploadIcon from "../../assets/upload.png";
import userAvatar from "../../assets/userAvatar.png";

const CreateNewTaskForm = ({ toggleDrawer, open }) => {
  const form = () => (
    <Box sx={{ width: 750 }} className="overflow-y-scroll" role="presentation">
      <div className="px-8 py-10">
        <h2 className="font-semibold ">Create New Task</h2>

        <form className="mt-5 space-y-4">
          <div className="flex gap-4 ">
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Task Title</label>
              <input
                name="title"
                type="text"
                className="border-[#BCBCBC] w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              />
            </div>
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Category</label>
              <Select
                fullWidth
                defaultValue=""
                displayEmpty
                name="category"
                className="border border-[#BCBCBC] w-full outline-none mt-1 rounded-sm h-10.5 box-border"
                sx={{
                  "& .MuiOutlinedInput-notchedOutline": { border: "none" },
                  "& .MuiSelect-select": {
                    paddingLeft: "16px",
                    display: "flex",
                    alignItems: "center",
                    fontSize: "13px",
                    fontWeight: "400",
                    color: "#000",
                  },
                }}
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
            </div>
          </div>

          <div className="flex gap-4 ">
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Description</label>

              <textarea
                name="description"
                rows="4"
                className="border-[#BCBCBC] resize-none w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              ></textarea>
            </div>
          </div>

          <div className="flex gap-4 ">
            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Project</label>

              <Select
                name="project"
                fullWidth
                defaultValue=""
                displayEmpty
                className="border border-[#BCBCBC] w-full outline-none mt-1 rounded-sm h-10.5 box-border"
                sx={{
                  "& .MuiOutlinedInput-notchedOutline": { border: "none" },
                  "& .MuiSelect-select": {
                    paddingLeft: "16px",
                    display: "flex",
                    alignItems: "center",
                    fontSize: "13px",
                    fontWeight: "400",
                    color: "#000",
                  },
                }}
              >
                <MenuItem
                  defaultChecked
                  value=""
                  sx={{ fontSize: "13px", fontWeight: "600" }}
                >
                  Select Project
                </MenuItem>
                <MenuItem sx={{ fontSize: "13px", fontWeight: "600" }}>
                  project 1
                </MenuItem>
              </Select>
            </div>

            <div className="flex-1">
              <label className="text-[#616161]  text-[14px]">Priority</label>

              <Select
                name="priority"
                fullWidth
                defaultValue=""
                displayEmpty
                className="border border-[#BCBCBC] w-full outline-none mt-1 rounded-sm h-10.5 box-border"
                sx={{
                  "& .MuiOutlinedInput-notchedOutline": { border: "none" },
                  "& .MuiSelect-select": {
                    paddingLeft: "16px",
                    display: "flex",
                    alignItems: "center",
                    fontSize: "13px",
                    fontWeight: "400",
                    color: "#000",
                  },
                }}
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
            </div>
          </div>

          <div className="flex gap-4">
            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">
                Estimated Time (hours)
              </label>
              <input
                name="estimatedTime"
                type="number"
                min="0"
                step="0.5"
                placeholder="e.g. 2.5"
                className="border-[#BCBCBC] w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              />
              <p className="text-[11px] text-[#9E9E9E] mt-1">
                Approx time required to complete the task
              </p>
            </div>

            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Due Date</label>
              <input
                name="dueDate"
                type="date"
                className="border-[#BCBCBC] w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              />
            </div>
          </div>
          <div>
            <label className="text-[#616161] text-[14px]">Assigned To</label>

            <div className="relative">
              <input
                type="text"
                placeholder="Search user..."
                className="border-[#BCBCBC] w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              />
            </div>

            <div className="flex gap-2.5 mt-4">
              <div className="relative">
                <img
                  className="w-8.5 h-8.5 rounded-full object-cover"
                  src={userAvatar}
                />
                <img
                  className="w-3.5 cursor-pointer absolute top-0 -right-0.5"
                  src={removeIcon}
                />
              </div>
            </div>
          </div>

          <div className="mt-5">
            <label className="text-[#616161] text-[14px]">
              Upload Document
            </label>
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
              <span>Create Task</span>
            </Button>
          </div>
        </form>
      </div>
    </Box>
  );

  return (
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
  );
};

export default CreateNewTaskForm;
