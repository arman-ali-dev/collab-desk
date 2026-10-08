import searchIcon from "../../assets/search2.png";
import bellIcon from "../../assets/bell.png";
import tagIcon from "../../assets/tag.png";
import userAvatar from "../../assets/userAvatar.png";
import { IconButton } from "@mui/material";

const ChatHeader = ({ selectedChatRoom }) => {
  return (
    <div className="px-4 flex justify-between items-center pb-1.5 pt-2 border-b border-[rgba(200,200,200,.5)]">
      <div className="flex items-center gap-2.5">
        {true ? (
          <img
            src={selectedChatRoom?.project.logo}
            alt={"User"}
            className="w-6 h-6 rounded-full object-cover shrink-0"
          />
        ) : (
          <img src={tagIcon} className="w-3.5 h-3.5" alt="" />
        )}

        <div>
          <p className="text-[13px] font-medium leading-tight">
            {selectedChatRoom?.project.title}
          </p>
          <p className="text-[11px] opacity-55">
            {selectedChatRoom?.project.members.length} Member
            {selectedChatRoom?.project.members.length > 1 && "s"}
          </p>
        </div>
      </div>

      <div className="flex items-center gap-3">
        <div className="relative">
          <IconButton size="small">
            <img src={searchIcon} className="w-3.5 h-3.5" alt="" />
            <div
              className={`dropdown ${false && "show"} bg-[#EFEFEF] z-30 max-w-73.5 w-65 absolute`}
            >
              <div className="relative">
                <input
                  type="text"
                  placeholder="Search..."
                  className="shadow placeholder:text-[#464545] border-0 outline-0 text-[14px] pr-4 pl-8 py-1.5 w-full"
                />
                <img
                  src={searchIcon}
                  className="w-3.5 h-3.5 left-3 absolute top-1/2 -translate-y-1/2"
                  alt=""
                />
              </div>
            </div>
          </IconButton>
        </div>
        <IconButton size="small">
          <img src={bellIcon} className="w-4 h-4" alt="" />
        </IconButton>
      </div>
    </div>
  );
};

export default ChatHeader;
