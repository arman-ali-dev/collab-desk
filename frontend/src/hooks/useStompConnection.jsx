import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { stompClient } from "../util/stompClient";
import { setConnected } from "../store/chatSlice";

const useStompConnection = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    if (!localStorage.getItem("token")) return;

    stompClient.onConnect = () => dispatch(setConnected(true));
    stompClient.onWebSocketClose = () => dispatch(setConnected(false));
    stompClient.onStompError = (frame) =>
      console.error("STOMP error:", frame.headers["message"]);

    stompClient.activate();

    return () => {
      stompClient.deactivate();
      dispatch(setConnected(false));
    };
  }, [dispatch]);
};

export default useStompConnection;
