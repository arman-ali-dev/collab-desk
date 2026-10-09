import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import plusIcon from "../../assets/plus.png";
import {
  Select,
  MenuItem,
  Button,
  Snackbar,
  Alert,
  CircularProgress,
} from "@mui/material";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { createMemberSchema } from "../../validations/userSchema";
import { createUser } from "../../store/admin/userSlice";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";

const inputClass =
  "w-full outline-0 px-4 py-2 text-[15px] mt-1 border rounded-sm";

const selectSx = {
  "& .MuiOutlinedInput-notchedOutline": { border: "none" },
  "& .MuiSelect-select": {
    paddingLeft: "16px",
    display: "flex",
    alignItems: "center",
  },
};

export default function AddMemberForm({ open, toggleDrawer }) {
  const dispatch = useDispatch();
  const { createLoading } = useSelector((state) => state.adminUsers);

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
    resolver: yupResolver(createMemberSchema),
    mode: "onBlur",
    defaultValues: {
      fullName: "",
      email: "",
      designation: "",
      role: "",
    },
  });

  const onSubmit = async (data) => {
    try {
      await dispatch(createUser(data)).unwrap();

      setSnackType("success");
      setSnackMessage("User created");
      setOpenSnack(true);
      toggleDrawer(false)();
      reset();
    } catch (err) {
      setSnackType("error");
      setSnackMessage(err);
      setOpenSnack(true);
    }
  };

  const form = () => (
    <Box sx={{ width: 750 }} role="presentation">
      <div className="px-8 py-10">
        <h2 className="font-semibold flex gap-3 items-center">
          <img src={plusIcon} className="w-4" alt="" /> Add New Member
        </h2>

        <form onSubmit={handleSubmit(onSubmit)} className="mt-5 space-y-4">
          <div className="flex gap-4">
            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Full Name</label>
              <input
                type="text"
                {...register("fullName")}
                id="fullName"
                className={`${inputClass} ${
                  errors.fullName ? "border-red-500" : "border-[#BCBCBC]"
                }`}
              />
              {errors.fullName && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.fullName.message}
                </p>
              )}
            </div>

            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Email</label>
              <input
                type="text"
                {...register("email")}
                id="email"
                className={`${inputClass} ${
                  errors.email ? "border-red-500" : "border-[#BCBCBC]"
                }`}
              />

              {errors.email && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.email.message}
                </p>
              )}
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
                className={`border ${errors.role ? "border-red-500" : "border-[#BCBCBC]"} w-full text-[15px] mt-1 rounded-sm h-10.5`}
                sx={selectSx}
                {...register("role")}
              >
                <MenuItem value="">
                  <em>Select Role</em>
                </MenuItem>
                <MenuItem value="MEMBER">Member</MenuItem>
                <MenuItem value="ADMIN">Admin</MenuItem>
              </Select>
              {errors.role && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.role.message}
                </p>
              )}
            </div>

            <div className="flex-1">
              <label className="text-[#616161] text-[14px]">Designation</label>
              <input
                type="text"
                {...register("designation")}
                id="designation"
                className={`${inputClass} ${
                  errors.designation ? "border-red-500" : "border-[#BCBCBC]"
                }`}
              />
              {errors.designation && (
                <p className="text-red-500 text-[12px] mt-1">
                  {errors.designation.message}
                </p>
              )}
            </div>
          </div>

          <div className="flex gap-2 mt-10">
            <Button
              onClick={() => reset()}
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
              type="submit"
              disabled={createLoading}
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
              {createLoading && (
                <CircularProgress size={15} sx={{ color: "#fff" }} />
              )}

              {!createLoading && <span>Add Member</span>}
            </Button>
          </div>
        </form>
      </div>
    </Box>
  );

  return (
    <>
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
}
