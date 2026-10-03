import { Avatar, Button, IconButton } from "@mui/material";
import editIcon from "../../assets/edit2.png";
import userAvatar from "../../assets/userAvatar.png";
import { useSelector } from "react-redux";

const Profile = () => {
  const { profile } = useSelector((state) => state.profile);

  return (
    <div className="mt-4 mx-8 relative">
      <div className="bg-white h-[85vh] shadow rounded-lg px-10 py-10">
        <div className="flex gap-4 items-center">
          <div className="relative">
            <Avatar
              src={profile?.profileImage || userAvatar}
              alt="User Profile"
              sx={{ width: 110, height: 110 }}
            />

            <div className="absolute -bottom-0.5 right-3">
              <IconButton
                sx={{
                  backgroundColor: "#000",
                  "&:hover": { backgroundColor: "#000" },
                }}
              >
                <img className="w-3 h-3" src={editIcon} alt="" />
              </IconButton>
            </div>
          </div>

          <div>
            <h3 className="text-[17px] font-medium">{profile?.fullName}</h3>
            <p className="font-medium text-[#222222] text-[14px]">
              {profile?.designation}
            </p>
          </div>
        </div>

        <form className="mt-10">
          <div className="flex gap-5">
            <div className="flex-1">
              <label className="text-[13px] font-medium">Full Name</label>
              <input
                name="fullName"
                defaultValue={profile?.fullName}
                className="bg-[#EFEFEF] outline-0 mt-0.5 w-full text-[14px] py-2 px-4 rounded-lg"
              />
            </div>

            <div className="flex-1">
              <label className="text-[13px] font-medium">Email</label>
              <input
                name="email"
                defaultValue={profile?.email}
                className="bg-[#EFEFEF] outline-0 mt-0.5 w-full text-[14px] py-2 px-4 rounded-lg"
              />
            </div>
          </div>

          <div className="mt-5">
            <label className="text-[13px] font-medium">Designation</label>
            <input
              name="designation"
              defaultValue={profile?.designation}
              className="bg-[#EFEFEF] outline-0 mt-0.5 w-full text-[14px] py-2 px-4 rounded-lg"
            />
          </div>

          <div className="flex gap-2 mt-16 justify-end">
            <Button
              type="button"
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
              type="button"
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
              <span className="font-medium">Save Changes</span>
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Profile;
