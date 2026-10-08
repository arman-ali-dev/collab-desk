import { Client } from "@stomp/stompjs";

export const stompClient = new Client({
  brokerURL: import.meta.env.VITE_WS_URL,
  reconnectDelay: 5000,
  heartbeatIncoming: 10000,
  heartbeatOutgoing: 10000,
  beforeConnect: () => {
    stompClient.connectHeaders = {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    };
  },
});

export function sendMessage({
  roomId,
  type = "TEXT",
  content,
  caption,
  fileName,
}) {
  if (!stompClient.connected) return false;
  stompClient.publish({
    destination: "/app/chat.send",
    body: JSON.stringify({ roomId, type, content, caption, fileName }),
  });
  return true;
}
