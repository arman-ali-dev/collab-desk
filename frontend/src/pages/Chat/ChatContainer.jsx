import { ChartsContainer } from "@mui/x-charts";
import sendIcon from "../../assets/send.png";
import attachFileIcon from "../../assets/attach.png";
import lockIcon from "../../assets/lock.png";
import ChatHeader from "./ChatHeader";
import ChatArea from "./ChatArea";
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { clearSelectedChatRoom } from "../../store/chatRoomSlice";
import useStompConnection from "../../hooks/UseStompConnection";
import { sendMessage } from "../../util/stompClient";
import useRoomSubscription from "../../hooks/useRoomSubscription";

const ChatContainer = () => {
  const dispatch = useDispatch();
  const { selectedChatRoom } = useSelector((state) => state.chatRoom);
  useStompConnection(selectedChatRoom?.id);
  useRoomSubscription(selectedChatRoom?.id);

  useEffect(() => {
    return () => dispatch(clearSelectedChatRoom());
  }, []);

  // Send Message

  const [text, setText] = useState("");
  const { connected } = useSelector((state) => state.chat);

  const handleSend = (e) => {
    console.log(e);

    e.preventDefault();
    const content = text.trim();
    console.log("Content", content);

    if (!content || !selectedChatRoom?.id || !connected) return;

    const ok = sendMessage({
      roomId: selectedChatRoom.id,
      type: "TEXT",
      content,
    });

    console.log("Ok", ok);

    if (ok) setText("");
  };

  const { profile } = useSelector((state) => state.profile);

  const canView =
    profile?.role === "ADMIN" ||
    selectedChatRoom?.project?.members.some((u) => u.id === profile.id);
  return (
    <>
      <div className="h-full w-full flex flex-col relative min-h-0">
        <ChatHeader selectedChatRoom={selectedChatRoom} />

        {!canView ? (
          <div className="flex-1 flex flex-col relative">
            <div className="absolute inset-0 overflow-hidden pointer-events-none">
              <div className="px-4 opacity-30 blur-sm">
                <ChatArea messages={[]} currentUserId={1} />
              </div>
            </div>
            <div className="flex-1 flex items-center justify-center relative z-10">
              <div className="bg-white rounded-xl px-6 py-5 shadow-md text-center max-w-xs">
                <img
                  className="w-7 h-7 mb-1 mx-auto opacity-80"
                  src={lockIcon}
                  alt=""
                />
                <p className="text-[14px] font-semibold text-gray-800">
                  Restricted Access
                </p>
                <p className="text-[12px] text-gray-500 mt-1">
                  Only project members can view this chat.
                </p>
              </div>
            </div>
            <div className="pb-4 px-4">
              <div className="flex items-center gap-2 border border-gray-200 rounded-md px-4 py-2.5 bg-gray-50 cursor-not-allowed">
                <img className="w-4 h-4 opacity-60" src={lockIcon} alt="" />
                <span className="text-gray-400 text-[13px]">
                  You cannot send messages in this chat.
                </span>
              </div>
            </div>
          </div>
        ) : (
          <div className="flex-1 flex flex-col min-h-0 pb-4">
            <div className="flex-1 overflow-y-auto px-4 chat-scroll min-h-0">
              <ChatArea messages={[]} currentUserId={1} />
              <div />
            </div>

            <div className="pt-3 px-4">
              <form onSubmit={handleSend} className="relative">
                <input
                  value={text}
                  onChange={(e) => setText(e.target.value)}
                  type="text"
                  disabled={!connected}
                  placeholder={
                    connected ? "Type a message..." : "Connecting..."
                  }
                  className="text-[13px] outline-0 border border-gray-300 w-full py-2.5 pl-10 pr-12 rounded-md disabled:opacity-60"
                />
                <input
                  type="file"
                  accept="image/*,video/*,application/pdf,.doc,.docx,.zip"
                  className="hidden"
                />
                <img
                  alt="Attach"
                  src={attachFileIcon}
                  className={`absolute top-1/2 -translate-y-1/2 left-4 w-4 h-4 cursor-pointer ${false ? "opacity-40" : ""}`}
                />
                <button
                  type="submit"
                  className="bg-black px-3 py-2 rounded-md absolute top-1/2 -translate-y-1/2 right-2 disabled:opacity-40"
                >
                  <img
                    src={sendIcon}
                    alt="Send"
                    className="w-4 h-4 filter invert"
                  />
                </button>
              </form>
            </div>
          </div>
        )}
        {/* 
        {previewFile && (
          <div className="absolute inset-0 bg-black/80 z-50 flex flex-col">
            <div className="flex items-center justify-between px-4 py-3">
              <button
                onClick={closePreview}
                className="text-white text-xl font-bold"
              >
                ✕
              </button>
              <p className="text-white text-sm font-medium truncate max-w-[60%]">
                {previewFile.name}
              </p>
              <span className="text-white/50 text-xs">
                {(previewFile.size / 1024).toFixed(1)} KB
              </span>
            </div>
            <div className="flex-1 flex items-center justify-center px-4 overflow-hidden">
              {messageType === "IMAGE" && (
                <img
                  src={previewUrl}
                  alt="preview"
                  className="max-h-full max-w-full rounded-lg object-contain"
                />
              )}
              {messageType === "VIDEO" && (
                <video
                  src={previewUrl}
                  controls
                  className="max-h-full max-w-full rounded-lg"
                />
              )}
              {messageType === "FILE" && (
                <div className="bg-white/10 rounded-xl px-8 py-10 flex flex-col items-center gap-3">
                  <span className="text-5xl">📄</span>
                  <p className="text-white text-sm text-center break-all">
                    {previewFile.name}
                  </p>
                </div>
              )}
            </div>
            <div className="px-4 py-3 flex items-center gap-3">
              <input
                value={previewCaption}
                onChange={(e) => setPreviewCaption(e.target.value)}
                placeholder="Add a caption..."
                className="flex-1 bg-white/10 text-white placeholder-white/50 text-[13px] px-4 py-2.5 rounded-full outline-none border border-white/20"
              />
              <button
                onClick={handleSendMedia}
                disabled={uploading}
                className="bg-green-500 hover:bg-green-600 disabled:opacity-50 p-3 rounded-full transition-colors"
              >
                {uploading ? (
                  <span className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin block" />
                ) : (
                  <img
                    src={sendIcon}
                    alt="Send"
                    className="w-5 h-5 filter invert"
                  />
                )}
              </button>
            </div>
          </div>
        )} */}
      </div>
    </>
  );
};

export default ChatContainer;
