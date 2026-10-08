import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { stompClient } from "../util/stompClient";
import { upsertMessage, clearMessages } from "../store/chatSlice";

const useRoomSubscription = (roomId) => {
  const dispatch = useDispatch();
  const { connected } = useSelector((state) => state.chat);

  useEffect(() => {
    dispatch(clearMessages());
  }, [roomId, dispatch]);

  useEffect(() => {
    if (!connected || !roomId) return;

    const sub = stompClient.subscribe(`/topic/room.${roomId}`, (frame) => {
      dispatch(upsertMessage(JSON.parse(frame.body)));
    });

    return () => {
      try {
        sub.unsubscribe();
      } catch {
        // ignore if already Connection is closed
      }
    };
  }, [connected, roomId, dispatch]);
};

export default useRoomSubscription;
