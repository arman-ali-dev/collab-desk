import { ChartsContainer } from "@mui/x-charts";
import sendIcon from "../../assets/send.png";
import attachFileIcon from "../../assets/attach.png";
import lockIcon from "../../assets/lock.png";
import ChatHeader from "./ChatHeader";
import ChatArea from "./ChatArea";
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useRef, useState } from "react";
import { clearSelectedChatRoom } from "../../store/chatRoomSlice";
import useStompConnection from "../../hooks/UseStompConnection";
import { sendMessage } from "../../util/stompClient";
import useRoomSubscription from "../../hooks/useRoomSubscription";
import { clearMessages, fetchMessages } from "../../store/chatSlice";
import { uploadToCloudinary } from "../../util/uploadToCloudinary";
import { Alert, CircularProgress, Snackbar } from "@mui/material";
import InsertDriveFileIcon from "@mui/icons-material/InsertDriveFile";

const ChatContainer = () => {
  const dispatch = useDispatch();
  const { selectedChatRoom } = useSelector((state) => state.chatRoom);
  useRoomSubscription(selectedChatRoom?.id);

  useEffect(() => {
    if (selectedChatRoom) {
      dispatch(fetchMessages(selectedChatRoom.id));
    }
    return () => {
      dispatch(clearMessages());
    };
  }, [selectedChatRoom]);

  // Send Message

  const [text, setText] = useState("");
  const { connected } = useSelector((state) => state.chat);

  const handleSend = (e) => {
    e.preventDefault();
    const content = text.trim();

    if (!content || !selectedChatRoom?.id || !connected) return;

    const ok = sendMessage({
      roomId: selectedChatRoom.id,
      type: "TEXT",
      content,
    });

    if (ok) setText("");
  };

  // Media send as a message

  const [openSnack, setOpenSnack] = useState(false);
  const [snackMessage, setSnackMessage] = useState("");
  const [snackType, setSnackType] = useState("");

  const fileInputRef = useRef(null);
  const [uploading, setUploading] = useState(false);

  const [filename, setFilename] = useState("");
  const [caption, setCaption] = useState("");
  const [type, setType] = useState("");

  const [previewFile, setPreviewFile] = useState();
  const [previewUrl, setPreviewUrl] = useState("");

  const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

  const handleChangeImage = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.size > MAX_FILE_SIZE) {
      setSnackType("error");
      setSnackMessage("Image size must be less than 10 MB");
      setOpenSnack(true);
      return;
    }
    setPreviewFile(file);

    try {
      setUploading(true);
      const url = await uploadToCloudinary(file);

      const type = file.type.startsWith("image/")
        ? "IMAGE"
        : file.type.startsWith("video/")
          ? "VIDEO"
          : "FILE";

      setPreviewUrl(URL.createObjectURL(file));
      setText(url);
      setFilename(file.name);
      setType(type);
    } catch (err) {
      setSnackType("error");
      setSnackMessage(err.message || "Image upload failed");
      setOpenSnack(true);
    } finally {
      setUploading(false);
    }
  };

  const handleSendMedia = async (e) => {
    e.preventDefault();

    const content = text;

    if (!content || !selectedChatRoom?.id || !connected) return;

    const ok = sendMessage({
      roomId: selectedChatRoom.id,
      type,
      content,
      caption,
      filename,
    });

    if (ok) {
      setText("");
      setCaption("");
      setFilename("");
      setPreviewFile("");
      setPreviewUrl("");
    }
  };

  const closePreview = () => {
    setPreviewFile(null);
    setPreviewUrl("");
    setCaption("");
  };

  // Scroll Down
  const { messages } = useSelector((state) => state.chat);
  const scrollRef = useRef(null);

  useEffect(() => {
    requestAnimationFrame(() => {
      const el = scrollRef.current;

      if (el) {
        el.scrollTop = el.scrollHeight;
      }
    });
  }, [messages, selectedChatRoom]);

  // Restriction
  const { profile } = useSelector((state) => state.profile);

  const canView =
    profile?.role === "ADMIN" ||
    selectedChatRoom?.project?.members.some((u) => u.id === profile?.id);
  return (
    <>
      <div className="h-full w-full flex flex-col relative min-h-0">
        <ChatHeader selectedChatRoom={selectedChatRoom} />

        {!canView ? (
          <div className="flex-1 flex flex-col relative">
            <div className="absolute inset-0 overflow-hidden pointer-events-none">
              <div className="px-4 opacity-30 blur-sm">
                <ChatArea />
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
            <div
              ref={scrollRef}
              className="flex-1 overflow-y-auto px-4 chat-scroll min-h-0"
            >
              <ChatArea />
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
                  ref={fileInputRef}
                  onChange={handleChangeImage}
                />
                <img
                  alt="Attach"
                  onClick={() => !uploading && fileInputRef.current?.click()}
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
              {uploading ? (
                <CircularProgress size={30} sx={{ color: "#fff" }} />
              ) : (
                <>
                  {type === "IMAGE" && (
                    <img
                      src={previewUrl}
                      alt="preview"
                      className="max-h-full max-w-full rounded-lg object-contain"
                    />
                  )}
                  {type === "VIDEO" && (
                    <video
                      src={previewUrl}
                      controls
                      className="max-h-full max-w-full rounded-lg"
                    />
                  )}
                  {type === "FILE" && (
                    <div className="bg-white/10 rounded-xl px-8 py-10 flex flex-col items-center gap-3">
                      <InsertDriveFileIcon />
                      <p className="text-white text-sm text-center break-all">
                        {previewFile.name}
                      </p>
                    </div>
                  )}
                </>
              )}
            </div>
            <div className="px-4 py-3 flex items-center gap-3">
              <input
                value={caption}
                onChange={(e) => setCaption(e.target.value)}
                placeholder="Add a caption..."
                className="flex-1 bg-white/10 text-white placeholder-white/50 text-[13px] px-4 py-2.5 rounded-full outline-none border border-white/20"
              />
              <button
                onClick={handleSendMedia}
                disabled={uploading}
                className="bg-black cursor-pointer disabled:opacity-50 p-3 rounded-full transition-colors"
              >
                <img
                  src={sendIcon}
                  alt="Send"
                  className="w-5 h-5 filter invert"
                />
              </button>
            </div>
          </div>
        )}
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

export default ChatContainer;
