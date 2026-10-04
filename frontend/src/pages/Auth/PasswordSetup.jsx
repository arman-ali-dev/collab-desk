import {
  Alert,
  Button,
  CircularProgress,
  IconButton,
  Snackbar,
} from "@mui/material";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import mailIcon from "../../assets/mail.png";
import lockIcon from "../../assets/lock.png";
import viewIcon from "../../assets/view.png";
import backIcon from "../../assets/back.png";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { passwordSetupSchema } from "../../validations/userSchema";
import { setPassword } from "../../store/authSlice";

const PasswordSetup = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const dispatch = useDispatch();
  const { loading } = useSelector((state) => state.auth);

  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [snackType, setSnackType] = useState("success");

  const [searchParams] = useSearchParams();
  const TOKEN = searchParams.get("token");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(passwordSetupSchema),
    mode: "onBlur",
    defaultValues: {
      token: TOKEN || "",
      password: "",
    },
  });

  useEffect(() => {
    if (TOKEN) {
      reset({
        token: TOKEN,
        password: "",
      });
    }
  }, [TOKEN, reset]);

  const onSubmit = async (data) => {
    try {
      await dispatch(setPassword(data)).unwrap();
      navigate("/signin");
    } catch (e) {
      setOpenSnack(true);
      setSnackType("error");
      setSnackMessage(e);
    }
  };

  return (
    <>
      <div className="flex justify-center">
        <form onSubmit={handleSubmit(onSubmit)} className="w-100 pt-20">
          <IconButton onClick={() => navigate(-1)} className="pt-10">
            <img src={backIcon} className="w-4 h-4" alt="" />
          </IconButton>

          <div className="text-center">
            <h3 className="text-[#272626] text-[18px] font-semibold">
              Set Your Password
            </h3>
            <p className="text-[#727272] text-[14px]">
              Please enter your details to get started
            </p>
          </div>
          <div className="mt-8">
            <label className="text-[13px] font-medium text-[#363636]">
              Password*
            </label>

            <div className="relative">
              <input
                name="password"
                type={showPassword ? "text" : "password"}
                placeholder="• • • • • • •"
                {...register("password")}
                id="password"
                className={`${errors.password ? "border-red-500" : "border-[#B7B7B7]"}  placeholder:text-[18px] pr-4 pl-8.5 w-full outline-0 mt-0.5 border text-[14px] py-2 rounded-md`}
              />

              <img
                src={lockIcon}
                className="w-4 h-4 opacity-50 absolute top-1/2 -translate-y-1/2 left-3"
                alt=""
              />

              <div className="absolute top-1/2 -translate-y-1/2 right-3">
                <IconButton onClick={() => setShowPassword(!showPassword)}>
                  <img
                    src={viewIcon}
                    className="w-4.5 h-4.5 opacity-50"
                    alt=""
                  />
                </IconButton>
              </div>
            </div>

            {errors.password && (
              <p className="text-red-500 text-[12px] mt-1">
                {errors.password.message}
              </p>
            )}
          </div>

          {/* Password */}
          <div className="mt-3">
            <label className="text-[13px] font-medium text-[#363636]">
              Comfirm Password*
            </label>

            <div className="relative">
              <input
                name="confirmPassword"
                type={showPassword ? "text" : "password"}
                placeholder="• • • • • • •"
                {...register("confirmPassword")}
                id="confirmPassword"
                className={`${errors.confirmPassword ? "border-red-500" : "border-[#B7B7B7]"}  placeholder:text-[18px] pr-4 pl-8.5 w-full outline-0 mt-0.5 border text-[14px] py-2 rounded-md`}
              />

              <img
                src={lockIcon}
                className="w-4 h-4 opacity-50 absolute top-1/2 -translate-y-1/2 left-3"
                alt=""
              />

              <div className="absolute top-1/2 -translate-y-1/2 right-3">
                <IconButton onClick={() => setShowPassword(!showPassword)}>
                  <img
                    src={viewIcon}
                    className="w-4.5 h-4.5 opacity-50"
                    alt=""
                  />
                </IconButton>
              </div>
            </div>

            {errors.confirmPassword && (
              <p className="text-red-500 text-[12px] mt-1">
                {errors.confirmPassword.message}
              </p>
            )}
          </div>

          <div className="mt-7">
            <Button
              type="submit"
              fullWidth
              disabled={loading}
              sx={{
                height: "38px",
                textTransform: "capitalize",
                backgroundColor: "#000",
                border: "1px solid #000",
                color: "#fff",
                paddingX: "30px",
                fontSize: "13px",
                borderRadius: "4px",
                position: "relative",
                "&:hover": {
                  backgroundColor: "#111",
                },
              }}
            >
              {loading && <CircularProgress size={15} sx={{ color: "#fff" }} />}

              {!loading && <span className="font-medium">Set Password</span>}
            </Button>
          </div>
        </form>
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

export default PasswordSetup;
