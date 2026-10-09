import { DragDropContext } from "@hello-pangea/dnd";
import meIcon from "../../assets/me.png";
import TaskSection from "./TaskSection";
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { fetchMyTasks, updateTaskStatus } from "../../store/member/taskSlice";

const SECTIONS = [
  {
    id: "TO_DO",
    label: "To-Do",
    color: "#157FD7",
    bg: "rgba(21,127,215,0.06)",
  },
  {
    id: "IN_PROGRESS",
    label: "In Progress",
    color: "#F55600",
    bg: "rgba(245,86,0,0.06)",
  },
  {
    id: "DONE",
    label: "Done",
    color: "#18A322",
    bg: "rgba(24,163,34,0.06)",
  },
];

const MyTasks = () => {
  const onDragEnd = ({ destination, source, draggableId }) => {
    if (!destination) return;
    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    )
      dispatch(
        updateTaskStatus({ id: draggableId, status: destination.droppableId }),
      );
    return;
  };

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchMyTasks());
  }, [dispatch]);

  const { tasks } = useSelector((state) => state.memberTasks);

  const tasksBySection = {
    TO_DO: tasks.filter((t) => t.status == "TO_DO"),
    IN_PROGRESS: tasks.filter((t) => t.status == "IN_PROGRESS"),
    DONE: tasks.filter((t) => t.status == "DONE"),
  };

  return (
    <>
      <DragDropContext onDragEnd={onDragEnd}>
        <div
          className="mt-4 mx-8 relative"
          style={{
            transition: "opacity 0.35s ease",
          }}
        >
          <div
            className="bg-white flex items-center gap-2.5 rounded-lg px-5 py-2.5"
            style={{
              boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
              transition: "opacity 0.4s ease, transform 0.4s ease",
            }}
          >
            <img className="w-7" src={meIcon} alt="" />
            <p className="text-[14px] font-semibold">My Tasks</p>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-gray-100 text-gray-500">
              {tasks?.length} total
            </span>
          </div>

          {SECTIONS.map((s, i) => (
            <TaskSection
              key={s.id}
              sectionId={s.id}
              label={s.label}
              color={s.color}
              bg={s.bg}
              tasks={tasksBySection[s.id]}
              sectionIndex={i}
            />
          ))}
        </div>
      </DragDropContext>
    </>
  );
};

export default MyTasks;
