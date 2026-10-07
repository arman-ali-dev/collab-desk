import React from "react";
import userAvatar from "../../assets/userAvatar.png";

const currentUserId = 1;

const messages = [
  {
    id: 1,
    type: "TEXT",
    content: "Hey team, landing page ka design ready hai?",
    sentAt: "2025-03-10T10:30:00Z",
    sender: { id: 2, fullName: "Priya Verma", profileImage: "" },
  },
  {
    id: 2,
    type: "TEXT",
    content: "Haan, Figma mein final kar diya hai. Abhi share karta hoon.",
    sentAt: "2025-03-10T10:32:00Z",
    sender: { id: 1, fullName: "Rahul Sharma", profileImage: "" },
  },
  {
    id: 3,
    type: "IMAGE",
    content: "https://picsum.photos/400/300",
    caption: "Hero section ka preview",
    sentAt: "2025-03-10T10:33:00Z",
    sender: { id: 1, fullName: "Rahul Sharma", profileImage: "" },
  },
  {
    id: 4,
    type: "VIDEO",
    content: "https://www.w3schools.com/html/mov_bbb.mp4",
    caption: "Animation demo",
    sentAt: "2025-03-10T10:35:00Z",
    sender: { id: 2, fullName: "Priya Verma", profileImage: "" },
  },
  {
    id: 5,
    type: "FILE",
    content: "#",
    fileName: "brand-guidelines.pdf",
    sentAt: "2025-03-10T10:36:00Z",
    sender: { id: 2, fullName: "Priya Verma", profileImage: "" },
  },
  {
    id: 6,
    type: "TEXT",
    content: "Perfect, thanks! Review karke batata hoon.",
    sentAt: "2025-03-10T10:40:00Z",
    sender: { id: 1, fullName: "Rahul Sharma", profileImage: "" },
  },
];

const MessageContent = ({ msg }) => {
  switch (msg.type) {
    case "IMAGE":
      return (
        <div>
          <img
            src={msg.content}
            alt="media"
            className="max-w-55 rounded-lg cursor-pointer object-cover"
          />
          {msg.caption && (
            <p className="text-[12px] mt-1 text-gray-700">{msg.caption}</p>
          )}
        </div>
      );

    case "VIDEO":
      return (
        <div>
          <video controls className="max-w-55 rounded-lg">
            <source src={msg.content} />
          </video>
          {msg.caption && (
            <p className="text-[12px] mt-1 text-gray-700">{msg.caption}</p>
          )}
        </div>
      );

    case "FILE":
      return (
        <a
          href={msg.content}
          className="flex items-center gap-2 text-blue-600 underline text-[12px]"
        >
          <span>📎</span>
          <span>{msg.fileName || "Download File"}</span>
        </a>
      );

    default: // TEXT
      return <p className="text-[12px]">{msg.content}</p>;
  }
};

const ChatArea = () => {
  return (
    <div className="py-4 space-y-4">
      {messages.map((msg) => {
        const isMine = msg.sender?.id === currentUserId;
        const isMedia = ["IMAGE", "VIDEO", "FILE"].includes(msg.type);

        return (
          <div
            key={msg.id}
            className={`flex gap-3 items-start ${isMine ? "justify-end" : ""}`}
          >
            {!isMine && (
              <img
                src={msg.sender?.profileImage || userAvatar}
                alt="Profile"
                className="w-7.5 h-7.5 mt-1 rounded-full object-cover"
              />
            )}

            <div>
              <div
                className={`flex gap-2 items-center ${isMine ? "justify-end" : ""}`}
              >
                {!isMine && (
                  <>
                    <p className="text-[13px]">
                      {msg.sender?.fullName || "User"}
                    </p>
                    <span className="h-1 w-1 bg-black rounded-full"></span>
                  </>
                )}
                <p className="opacity-30 text-[12px]">
                  {msg.sentAt
                    ? new Date(msg.sentAt).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })
                    : ""}
                </p>
                {isMine && (
                  <>
                    <span className="h-1 w-1 bg-black rounded-full"></span>
                    <p className="text-[13px]">You</p>
                  </>
                )}
              </div>

              <div
                className={`mt-0.5 ${isMedia ? "" : "bg-[#EAEAEA] px-3 py-1.5 rounded-bl-lg rounded-tr-lg"}`}
              >
                <MessageContent msg={msg} />
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default ChatArea;
