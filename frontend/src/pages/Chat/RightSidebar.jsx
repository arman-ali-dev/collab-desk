import React from "react";
import mediaIcon from "../../assets/media.png";
import filesIcon from "../../assets/files.png";
import tagIcon from "../../assets/tag.png";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFilePdf } from "@fortawesome/free-solid-svg-icons";
import { useSelector } from "react-redux";

const RightSidebar = () => {
  const { selectedChatRoom } = useSelector((state) => state.chatRoom);
  return (
    <div className="h-full w-full px-4 py-6 border-[rgba(200,200,200,.5)] border-l overflow-y-auto relative">
      <div className="border-b border-[rgba(200,200,200,.5)] pb-4 mb-3">
        <div className="bg-[#EFEFEF] w-16 h-16 mx-auto rounded-full flex justify-center items-center">
          <img src={tagIcon} alt="tag icon" className="w-5" />
        </div>
        <h3 className="text-center mt-2 font-medium text-[13px]">
          {selectedChatRoom?.project.title}
        </h3>
        <p className="text-center font-medium text-[11px] opacity-65">
          {selectedChatRoom?.project.members.length} Member
          {selectedChatRoom?.project.members.length > 1 && "s"}
        </p>
      </div>

      <div className="border-b border-[rgba(200,200,200,.5)] pb-5 mb-4">
        <h3 className="font-medium text-[13px]">Description</h3>
        <p className="text-[12px]">
          {selectedChatRoom?.project.description
            .split(" ")
            .slice(0, 13)
            .join(" ")}
          {selectedChatRoom?.project.description.split(" ").length > 13 &&
            "..."}
        </p>
      </div>

      <div className="border-b border-[rgba(200,200,200,.5)] pb-5 mb-4">
        <div className="flex justify-between items-center mb-4">
          <h3 className="flex gap-2 items-center font-medium">
            <img className="w-4" src={mediaIcon} alt="media icon" />
            <span className="text-[13px] mt-0.5">
              Media
              <span className="ml-1 opacity-50">(7)</span>
            </span>
          </h3>
          <button className="text-[11px] opacity-50 hover:opacity-100 transition-opacity">
            See all
          </button>
        </div>

        <div className="grid grid-cols-2 gap-2">
          <div className="relative cursor-pointer rounded-lg overflow-hidden h-17 bg-gray-100 group">
            <img
              src="https://picsum.photos/seed/a1/300/200"
              alt="media"
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
            />
          </div>

          <div className="relative cursor-pointer rounded-lg overflow-hidden h-17 bg-gray-100 group">
            <img
              src="https://picsum.photos/seed/a2/300/200"
              alt="media"
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
            />
          </div>

          <div className="relative cursor-pointer rounded-lg overflow-hidden h-17 bg-gray-100 group">
            <div className="w-full h-full flex items-center justify-center bg-gray-800 relative">
              <video
                src="https://www.w3schools.com/html/mov_bbb.mp4"
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 flex items-center justify-center bg-black/30">
                <span className="text-white text-xl">▶</span>
              </div>
            </div>
          </div>

          <div className="relative cursor-pointer rounded-lg overflow-hidden h-17 bg-gray-100 group">
            <img
              src="https://picsum.photos/seed/a4/300/200"
              alt="media"
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
            />
            <div className="absolute inset-0 bg-black/60 flex items-center justify-center rounded-lg">
              <span className="text-white text-lg font-semibold">+4</span>
            </div>
          </div>
        </div>
      </div>

      <div>
        <div className="flex justify-between items-center mb-4">
          <h3 className="flex gap-2 items-center font-medium">
            <img className="w-4" src={filesIcon} alt="files icon" />
            <span className="text-[13px] mt-0.5">
              Files
              <span className="ml-1 opacity-50">(3)</span>
            </span>
          </h3>
          <button className="text-[11px] opacity-50 hover:opacity-100 transition-opacity">
            See all
          </button>
        </div>

        <div className="space-y-2">
          <a
            href="#"
            className="flex items-center gap-3 p-2.5 rounded-lg bg-[#F5F5F5] hover:bg-[#EBEBEB] transition-colors group"
          >
            <div className="w-8 h-8 rounded-md bg-white flex items-center justify-center text-lg shadow-sm shrink-0">
              <FontAwesomeIcon icon={faFilePdf} />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-[12px] font-medium truncate">
                brand-guidelines.pdf
              </p>
              <p className="text-[11px] opacity-40">Priya Verma • 3/10/2025</p>
            </div>
            <span className="text-[11px] opacity-0 group-hover:opacity-40 transition-opacity">
              ⬇
            </span>
          </a>
        </div>
      </div>
    </div>
  );
};

export default RightSidebar;
