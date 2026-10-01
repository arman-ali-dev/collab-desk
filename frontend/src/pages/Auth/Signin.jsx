import IconButton from "@mui/material/IconButton";
import { useNavigate } from "react-router-dom";
import mailIcon from "../../assets/mail.png";
import lockIcon from "../../assets/lock.png";
import viewIcon from "../../assets/view.png";
import backIcon from "../../assets/back.png";
import Button from "@mui/material/Button";
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from "react-hook-form";
import { signinSchema } from "../../validations/authSchema";
import { useState } from "react";
import { CircularProgress } from "@mui/material";
import { useDispatch } from "react-redux";
import { login } from "../../store/authSlice";

const Signin = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    setError,
    reset,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: yupResolver(signinSchema),
    mode: "onBlur",
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const onSubmit = async (data) => {
    try {
      dispatch(login(data));
      reset();
    } catch (err) {
      setError("root", { message: "Something went wrong, try again" });
    }
  };

  return (
    <>
      <div className="flex justify-center">
        <form onSubmit={handleSubmit(onSubmit)} className="w-100 pt-32">
          <IconButton onClick={() => navigate(-1)} className="pt-10">
            <img src={backIcon} className="w-4 h-4" alt="" />
          </IconButton>

          <div className="text-center">
            <h3 className="text-[#272626] text-[18px] font-semibold">
              Sign In Your Account
            </h3>
            <p className="text-[#727272] text-[14px]">
              Please enter your details to get started
            </p>
          </div>

          <div className="mt-8">
            <label className="text-[13px] font-medium text-[#363636]">
              Email Address*
            </label>
            <div className="relative mt-0.5">
              <input
                {...register("email")}
                type="text"
                id="email"
                placeholder="name@gmail.com"
                aria-invalid={!errors.email}
                className={`${errors.email ? "border-red-500" : "border-[#B7B7B7]"} outline-0 w-full  border text-[14px] pr-4 pl-8.5 py-2 rounded-md`}
              />

              <img
                src={mailIcon}
                className="w-4 h-4 opacity-50 absolute top-1/2 -translate-y-1/2 left-3"
                alt=""
              />
            </div>
            {errors.email && (
              <p className="text-red-500 text-[12px] mt-1">
                {errors.email.message}
              </p>
            )}
          </div>

          {/* Password */}
          <div className="mt-3">
            <label className="text-[13px] font-medium text-[#363636]">
              Password*
            </label>

            <div className="relative">
              <input
                id="password"
                type={showPassword ? "text" : "password"}
                aria-invalid={!errors.password}
                {...register("password")}
                placeholder="• • • • • • •"
                className={`${errors.password ? "border-red-500" : "border-[#B7B7B7]"} placeholder:text-[18px] pr-4 pl-8.5 w-full outline-0 mt-0.5 border text-[14px] py-2 rounded-md`}
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
          </div>

          {errors.root && (
            <p
              role="alert"
              className="text-red-500 text-[13px] mt-4 text-center"
            >
              {errors.root.message}
            </p>
          )}

          <div className="mt-7">
            <Button
              type="submit"
              disabled={isSubmitting}
              fullWidth
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
              {isSubmitting && (
                <CircularProgress size={15} sx={{ color: "#fff" }} />
              )}

              <span className="font-medium">
                {isSubmitting ? "Signing in..." : "Sign In"}
              </span>
            </Button>
          </div>
        </form>
      </div>
    </>
  );
};

export default Signin;
