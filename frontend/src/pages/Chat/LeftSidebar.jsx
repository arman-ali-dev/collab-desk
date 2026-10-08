import mentionIcon from "../../assets/mention.png";
import { Link } from "react-router-dom";
import draftIcon from "../../assets/draft.png";
import bookmarkIcon from "../../assets/bookmark.png";
import channelIcon from "../../assets/channel.png";
import downArrow from "../../assets/arrowDown.png";
import tagIcon from "../../assets/tag.png";
import groupIcon from "../../assets/group.png";
import userAvatar from "../../assets/userAvatar.png";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchChatRooms, selectChatRoom } from "../../store/chatRoomSlice";

const LeftSidebar = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchChatRooms());
  }, [dispatch]);

  const { chatRooms, selectedChatRoom } = useSelector(
    (state) => state.chatRoom,
  );
  return (
    <>
      <div className="h-full w-full px-4 py-6 border-[rgba(200,200,200,.5)] border-r overflow-y-auto chat-scroll">
        <ul className="space-y-2 border-b border-[rgba(200,200,200,.5)] pb-4 mb-3.5">
          <li className="relative">
            <Link className="text-[13px] opacity-75 flex items-center gap-1.5 hover:opacity-100 transition-all duration-75">
              <img className="w-4 h-4" src={mentionIcon} alt="" />
              <span className="mt-px">Mentions</span>
            </Link>
            <span className="bg-[rgba(250,38,38,.9)] absolute right-0 top-1/2 -translate-y-1/2 opacity-80 flex justify-center items-center text-[10px] text-white h-5.5 w-5.5 rounded-lg">
              2
            </span>
          </li>
          <li>
            <Link className="text-[13px] opacity-75 flex items-center gap-1.5 hover:opacity-100 transition-all duration-75">
              <img className="w-4 h-4" src={draftIcon} alt="" />
              <span className="mt-px">Drafts</span>
            </Link>
          </li>
          <li>
            <Link className="text-[13px] opacity-75 flex items-center gap-1.5 hover:opacity-100 transition-all duration-75">
              <img className="w-4 h-4" src={bookmarkIcon} alt="" />
              <span className="mt-px">Bookmarks</span>
            </Link>
          </li>
        </ul>

        <div className="mb-3.5">
          <div className="flex justify-between items-center">
            <h3 className="flex gap-2 items-center font-medium">
              <img className="w-4 h-4" src={channelIcon} alt="" />
              <span className="text-[13px]">Projects</span>
            </h3>
            <img className="w-2 h-2 cursor-no-drop" src={downArrow} alt="" />
          </div>
          <ul className="space-y-2 border-b border-[rgba(200,200,200,.5)] pb-4 mt-3">
            {chatRooms?.map((r) => (
              <li
                onClick={() => dispatch(selectChatRoom(r))}
                key={r.id}
                className="relative cursor-pointer"
              >
                <span
                  className={`text-[13px] ${selectedChatRoom?.id != r.id ? "opacity-75" : "font-medium"} flex items-center gap-1.5 hover:opacity-100 transition-all duration-75`}
                >
                  <img className="w-3.5 h-3.5" src={tagIcon} alt="" />
                  {r.project.title}
                </span>
              </li>
            ))}
          </ul>
        </div>

        <div className="mb-3.5">
          <div className="flex justify-between items-center mb-3">
            <h3 className="flex gap-2 items-center font-medium">
              <img className="w-4 h-4" src={groupIcon} alt="" />
              <span className="text-[13px] mt-1">Users</span>
            </h3>
            <img
              className="w-2 h-2 mt-1 cursor-no-drop"
              src={downArrow}
              alt=""
            />
          </div>

          <ul className="space-y-2">
            <li
              className={`flex cursor-pointer items-center gap-2.5 px-2 py-1.5 rounded-lg hover:bg-[#F5F5F5] transition-colors ${
                "PRIVATE" === "PRIVATE" && true ? "bg-[#F0F4FF]" : ""
              }`}
            >
              <img
                src={userAvatar}
                alt="user"
                className="w-6 h-6 rounded-full object-cover shrink-0"
              />
              <p className="text-[13px] truncate">User</p>
            </li>
          </ul>
        </div>
      </div>
    </>
  );
};

export default LeftSidebar;
