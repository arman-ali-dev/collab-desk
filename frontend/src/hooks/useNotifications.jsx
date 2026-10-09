import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { stompClient } from "../util/stompClient";
import {
  addNotification,
  fetchNotifications,
} from "../store/notificationSlice";

const useNotifications = () => {
  const dispatch = useDispatch();
  const { connected } = useSelector((state) => state.chat);

  useEffect(() => {
    if (!connected) return;

    const sub = stompClient.subscribe("/user/queue/notifications", (frame) => {
      console.log(frame.body);

      dispatch(addNotification(JSON.parse(frame.body)));
    });

    dispatch(fetchNotifications());

    return () => {
      try {
        sub.unsubscribe();
      } catch {}
    };
  }, [connected, dispatch]);
};

export default useNotifications;
