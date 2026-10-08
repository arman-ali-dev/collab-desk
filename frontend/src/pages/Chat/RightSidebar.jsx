import React, { useState } from "react";
import mediaIcon from "../../assets/media.png";
import filesIcon from "../../assets/files.png";
import tagIcon from "../../assets/tag.png";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faFile,
  faFileExcel,
  faFileLines,
  faFilePdf,
  faFilePowerpoint,
  faFileWord,
} from "@fortawesome/free-solid-svg-icons";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import CloseIcon from "@mui/icons-material/Close";

const RightSidebar = () => {
  const { selectedChatRoom } = useSelector((state) => state.chatRoom);
  const { messages } = useSelector((state) => state.chat);

  const media = messages?.filter((m) => m.type !== "TEXT" && m.type !== "FILE");
  const files = messages?.filter((m) => m.type === "FILE");

  const [showAllFiles, setShowAllFiles] = useState(false);
  const [showAllMedia, setShowAllMedia] = useState(false);
  const [mediaIndex, setMediaIndex] = useState(0);
  return (
    <>
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
                <span className="ml-1 opacity-50">({media?.length})</span>
              </span>
            </h3>
            <button
              onClick={() => setShowAllMedia(true)}
              className="cursor-pointer text-[11px] opacity-50 hover:opacity-100 transition-opacity"
            >
              See all
            </button>
          </div>

          {media?.length === 0 ? (
            <p className="text-[12px] opacity-40 text-center mt-4">
              No media shared yet
            </p>
          ) : (
            <div className="grid grid-cols-2 gap-2">
              {media?.slice(0, 4).map((m) => (
                <div
                  key={m.id}
                  className="relative cursor-pointer rounded-lg overflow-hidden h-17 bg-gray-100 group"
                >
                  <img
                    src={m.content}
                    alt="media"
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                  />
                </div>
              ))}
            </div>
          )}
        </div>

        <div>
          <div className="flex justify-between items-center mb-4">
            <h3 className="flex gap-2 items-center font-medium">
              <img className="w-4" src={filesIcon} alt="files icon" />
              <span className="text-[13px] mt-0.5">
                Files
                <span className="ml-1 opacity-50">({files?.length})</span>
              </span>
            </h3>
            <button
              onClick={() => setShowAllFiles(true)}
              className="text-[11px] cursor-pointer opacity-50 hover:opacity-100 transition-opacity"
            >
              See all
            </button>
          </div>

          <div className="space-y-2">
            {files?.length === 0 ? (
              <p className="text-[12px] opacity-40 text-center mt-4">
                No files shared yet
              </p>
            ) : (
              files?.slice(0, 2).map((f) => (
                <Link
                  key={f.id}
                  to={f.content}
                  target="_blank"
                  className="flex items-center gap-3 p-2.5 rounded-lg bg-[#F5F5F5] hover:bg-[#EBEBEB] transition-colors group"
                >
                  <div className="w-8 h-8 rounded-md bg-white flex items-center justify-center text-lg shadow-sm shrink-0">
                    <FontAwesomeIcon icon={faFilePdf} />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-[12px] font-medium truncate">
                      {f.filename}
                    </p>
                    <p className="text-[11px] opacity-40">
                      {f.sender?.fullName} •{" "}
                      {f.sentAt ? new Date(f.sentAt).toLocaleDateString() : ""}
                    </p>
                  </div>
                </Link>
              ))
            )}
          </div>
        </div>
      </div>

      {showAllMedia && (
        <div
          className="fixed inset-0 bg-black/90 z-50 flex flex-col"
          onClick={() => setShowAllMedia(false)}
        >
          <div
            className="flex items-center justify-between px-5 py-4"
            onClick={(e) => e.stopPropagation()}
          >
            <h3 className="text-white font-medium">
              All Media{" "}
              <span className="ml-2 opacity-50 text-sm">({media.length})</span>
            </h3>
            <button
              className="text-white text-2xl font-bold"
              onClick={() => setShowAllMedia(false)}
            >
              <CloseIcon />
            </button>
          </div>
          <div
            className="flex-1 flex items-center justify-center px-4 overflow-hidden"
            onClick={(e) => e.stopPropagation()}
          >
            {media[mediaIndex]?.type === "IMAGE" ? (
              <img
                src={media[mediaIndex].content}
                alt="full view"
                className="max-w-full max-h-full rounded-lg object-contain"
              />
            ) : (
              <video
                src={media[mediaIndex]?.content}
                controls
                autoPlay
                className="max-w-full max-h-full rounded-lg"
              />
            )}
          </div>
          <div
            className="flex gap-2 px-4 py-3 overflow-x-auto"
            onClick={(e) => e.stopPropagation()}
          >
            {media.map((msg, index) => (
              <div
                key={index}
                onClick={() => setMediaIndex(index)}
                className={`shrink-0 w-14 h-14 rounded-lg overflow-hidden cursor-pointer border-2 transition-all ${mediaIndex === index ? "border-white" : "border-transparent opacity-60"}`}
              >
                {msg.type === "IMAGE" ? (
                  <img
                    src={msg.content}
                    alt=""
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full bg-gray-700 flex items-center justify-center">
                    <span className="text-white text-xs">▶</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {showAllFiles && (
        <div
          className="fixed inset-0 bg-black/80 z-50 flex flex-col"
          onClick={() => setShowAllFiles(false)}
        >
          <div
            className="bg-white w-full max-w-md mx-auto mt-auto rounded-t-2xl flex flex-col max-h-[80vh]"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
              <h3 className="font-medium text-[14px]">
                All Files{" "}
                <span className="ml-2 opacity-50 text-[12px]">
                  ({files.length})
                </span>
              </h3>
              <button
                className="text-gray-500 text-xl cursor-pointer font-bold"
                onClick={() => setShowAllFiles(false)}
              >
                <CloseIcon />
              </button>
            </div>
            <div className="overflow-y-auto px-4 py-3 space-y-2">
              {files.map((msg, index) => (
                <Link
                  key={index}
                  to={msg.content}
                  target="_blank"
                  className="flex items-center gap-3 p-2.5 rounded-lg bg-[#F5F5F5] hover:bg-[#EBEBEB] transition-colors group"
                >
                  <div className="w-8 h-8 rounded-md bg-white flex items-center justify-center text-lg shadow-sm shrink-0">
                    {getFileIcon(msg.filename)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-[12px] font-medium truncate">
                      {msg.filename || "File"}
                    </p>
                    <p className="text-[11px] opacity-40">
                      {msg.sender?.fullName || "User"} •{" "}
                      {msg.sentAt
                        ? new Date(msg.sentAt).toLocaleDateString()
                        : ""}
                    </p>
                  </div>
                  <span className="text-[11px] opacity-0 group-hover:opacity-40 transition-opacity">
                    Download
                  </span>
                </Link>
              ))}
            </div>
          </div>
        </div>
      )}
    </>
  );
};

const getFileIcon = (fileName) => {
  const ext = fileName?.split(".").pop()?.toLowerCase();
  const icons = {
    pdf: <FontAwesomeIcon icon={faFilePdf} />,
    doc: <FontAwesomeIcon icon={faFileWord} />,
    docx: <FontAwesomeIcon icon={faFileWord} />,
    xls: <FontAwesomeIcon icon={faFileExcel} />,
    xlsx: <FontAwesomeIcon icon={faFileExcel} />,
    ppt: <FontAwesomeIcon icon={faFilePowerpoint} />,
    pptx: <FontAwesomeIcon icon={faFilePowerpoint} />,
    txt: <FontAwesomeIcon icon={faFileLines} />,
  };
  return icons[ext] || <FontAwesomeIcon icon={faFile} />;
};

export default RightSidebar;
