import * as React from "react";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import plusIcon from "../../assets/plus.png";
import { Select, MenuItem, Button } from "@mui/material";

export default function AddMemberForm({ open, toggleDrawer }) {
  const form = () => (
    <Box sx={{ width: 750 }} role="presentation">
      <div className="px-8 py-10">
        <h2 className="font-semibold flex gap-3 items-center">
          <img src={plusIcon} className="w-4" alt="" /> Add New Member
        </h2>

        <form className="mt-5 space-y-4">
          <div className="flex gap-4">
            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Full Name</label>
              <input
                type="text"
                name="fullName"
                className="border-[#BCBCBC] w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              />
            </div>

            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Email</label>
              <input
                type="text"
                name="email"
                className="border-[#BCBCBC] w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              />
            </div>
          </div>

          <div className="flex gap-4">
            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Role</label>

              <Select
                fullWidth
                displayEmpty
                name="role"
                defaultValue=""
                className="border border-[#BCBCBC] w-full text-[15px] mt-1 rounded-sm h-10.5"
                sx={{
                  "& .MuiOutlinedInput-notchedOutline": { border: "none" },
                  "& .MuiSelect-select": {
                    paddingLeft: "16px",
                    display: "flex",
                    alignItems: "center",
                  },
                }}
              >
                <MenuItem value="">
                  <em>Select Role</em>
                </MenuItem>
                <MenuItem value="MEMBER">Member</MenuItem>
                <MenuItem value="ADMIN">Admin</MenuItem>
              </Select>
            </div>

            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Designation</label>
              <input
                type="text"
                name="designation"
                className="border-[#BCBCBC] w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm"
              />
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
              <span className="font-medium">Reset Data</span>
            </Button>

            <Button
              type="button"
              sx={{
                textTransform: "capitalize",
                backgroundColor: "#000",
                border: "1px solid #000",
                color: "#fff",
                paddingX: "20px",
                fontSize: "14px",
                minWidth: "134px",
              }}
            >
              <span>Add Member</span>
            </Button>
          </div>
        </form>
      </div>
    </Box>
  );

  return (
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
      <div className="bg-white h-14 w-1.5 rounded-lg absolute top-1/2 -translate-y-1/2 -left-5 z-50 cursor-grab" />
      {form()}
    </Drawer>
  );
}
