import {
  Alert,
  Avatar,
  Button,
  CircularProgress,
  IconButton,
  Snackbar,
} from "@mui/material";
import editIcon from "../../assets/edit2.png";
import userAvatar from "../../assets/userAvatar.png";
import { useDispatch, useSelector } from "react-redux";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { editProfileSchema } from "../../validations/profileSchema";
import { useEffect, useRef, useState } from "react";
import { editProfile } from "../../store/profileSlice";
import { uploadToCloudinary } from "../../util/uploadToCloudinary";

const inputClass =
  "bg-[#EFEFEF] outline-0 mt-0.5 w-full text-[14px] py-2 px-4 rounded-lg";

const Profile = () => {
  const { profile, updateLoading } = useSelector((state) => state.profile);

  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [snackType, setSnackType] = useState("");

  const {
    register,
    handleSubmit,
    setValue,
    getValues,
    formState: { errors },
    reset,
  } = useForm({
    resolver: yupResolver(editProfileSchema),
    mode: "onBlur",
    defaultValues: {
      profileImage: profile?.profileImage || "",
      fullName: profile?.fullName || "",
      email: profile?.email || "",
      designation: profile?.designation || "",
    },
  });

  const handleReset = () => {
    reset({
      profileImage: profile.profileImage || "",
      fullName: profile.fullName || "",
      email: profile.email || "",
      designation: profile.designation || "",
    });
  };

  useEffect(() => {
    if (profile) {
      handleReset();
    }
  }, [profile, reset]);

  const fileInputRef = useRef(null);
  const [uploading, setUploading] = useState(false);

  const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

  const handleChangeImage = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.size > MAX_FILE_SIZE) {
      setSnackType("error");
      setSnackMessage("Image size must be less than 10 MB");
      setOpenSnack(true);
      return;
    }

    try {
      setUploading(true);
      const imageUrl = await uploadToCloudinary(file);
      setValue("profileImage", imageUrl);
    } catch (err) {
      setSnackType("error");
      setSnackMessage(err.message || "Image upload failed");
      setOpenSnack(true);
    } finally {
      setUploading(false);
    }
  };

  const dispatch = useDispatch();

  const onSubmit = async (data) => {
    try {
      await dispatch(editProfile(data)).unwrap();
      setSnackType("success");
      setSnackMessage("Updated");
      setOpenSnack(true);
    } catch (err) {
      setSnackType("error");
      setSnackMessage(err);
      setOpenSnack(true);
    }
  };
  return (
    <>
      <div className="mt-4 mx-8 relative">
        <div className="bg-white h-[85vh] shadow rounded-lg px-10 py-10">
          <div className="flex gap-4 items-center">
            <div className="relative">
              <Avatar
                src={
                  getValues("profileImage") ||
                  profile?.profileImage ||
                  userAvatar
                }
                alt="User Profile"
                sx={{ width: 110, height: 110 }}
              />

              {uploading && (
                <div className="absolute inset-0 bg-black/40 rounded-full flex items-center justify-center">
                  <div className="w-6 h-6 border-[3px] border-white border-t-transparent rounded-full animate-spin"></div>
                </div>
              )}

              <div className="absolute -bottom-0.5 right-3">
                <IconButton
                  onClick={() => fileInputRef.current.click()}
                  sx={{
                    backgroundColor: "#000",
                    "&:hover": { backgroundColor: "#000" },
                  }}
                >
                  <img className="w-3 h-3" src={editIcon} alt="" />
                </IconButton>
              </div>

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                hidden
                onChange={handleChangeImage}
              />
            </div>

            <div>
              <h3 className="text-[17px] font-medium">{profile?.fullName}</h3>
              <p className="font-medium text-[#222222] text-[14px]">
                {profile?.designation}
              </p>
            </div>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="mt-10">
            <div className="flex gap-5">
              <div className="flex-1">
                <label className="text-[13px] font-medium">Full Name</label>
                <input
                  defaultValue={profile?.fullName}
                  {...register("fullName")}
                  type="text"
                  id="fullName"
                  className={`${inputClass} $${errors.fullName ? "border-red-500" : ""}`}
                />
              </div>

              <div className="flex-1">
                <label className="text-[13px] font-medium">Email</label>
                <input
                  defaultValue={profile?.email}
                  {...register("email")}
                  type="text"
                  id="email"
                  className={`${inputClass} $${errors.email ? "border border-red-500" : ""}`}
                />
              </div>
            </div>

            <div className="mt-5">
              <label className="text-[13px] font-medium">Designation</label>
              <input
                defaultValue={profile?.designation}
                {...register("designation")}
                type="text"
                id="designation"
                className={`${inputClass} $${errors.designation ? "border-red-500" : ""}`}
              />
            </div>

            <div className="flex gap-2 mt-16 justify-end">
              <Button
                type="button"
                onClick={handleReset}
                sx={{
                  textTransform: "capitalize",
                  border: "1px solid #BCBCBC",
                  color: "#000",
                  paddingX: "30px",
                  fontSize: "13px",
                  borderRadius: "8px",
                }}
              >
                <span className="font-medium">Discard Changes</span>
              </Button>

              <Button
                type="submit"
                sx={{
                  textTransform: "capitalize",
                  backgroundColor: "#000",
                  border: "1px solid #000",
                  color: "#fff",
                  paddingX: "30px",
                  fontSize: "13px",
                  borderRadius: "8px",
                  minWidth: "158px",
                }}
              >
                {updateLoading && (
                  <CircularProgress size={15} sx={{ color: "#fff" }} />
                )}
                {!updateLoading && (
                  <span className="font-medium">Save Changes</span>
                )}
              </Button>
            </div>
          </form>
        </div>
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

export default Profile;
