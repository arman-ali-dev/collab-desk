import { IconButton } from "@mui/material";
import { useState } from "react";
import { useDispatch } from "react-redux";
import mailIcon from "../../assets/mail.png";
import lockIcon from "../../assets/lock.png";
import viewIcon from "../../assets/view.png";
import backIcon from "../../assets/back.png";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";

const PasswordSetup = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch();

  const [searchParams] = useSearchParams();
  const TOKEN = searchParams.get("token");

  return (
    <>
      <div className="flex justify-center">
        <form onSubmit={formik.handleSubmit} className="w-100 pt-20">
          <IconButton onClick={() => navigate(-1)} className="pt-10">
            <img src={backIcon} className="w-4 h-4" alt="" />
          </IconButton>
        </form>
      </div>
    </>
  );
};

export default PasswordSetup;
