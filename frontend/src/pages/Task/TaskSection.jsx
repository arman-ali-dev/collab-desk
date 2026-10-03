import { Draggable, Droppable } from "@hello-pangea/dnd";
import { useState } from "react";
import ReactDOM from "react-dom";
import Task from "./Task";
import { useSelector } from "react-redux";

const TaskSection = ({ sectionId, label, color, bg, tasks, sectionIndex }) => {
  const [isDragOver, setIsDragOver] = useState(false);
  const { profile } = useSelector((state) => state.profile);

  return (
    <>
      <div
        className="mt-4 rounded-xl px-5 py-4"
        style={{
          backgroundColor: isDragOver ? bg : "#fff",
          boxShadow: isDragOver
            ? `0 0 0 2px ${color}50, 0 8px 24px rgba(0,0,0,0.08)`
            : "0 2px 8px rgba(0,0,0,0.06)",
          transition: [
            `opacity 0.45s ease ${sectionIndex * 100 + 150}ms`,
            `transform 0.45s cubic-bezier(0.22,1,0.36,1) ${sectionIndex * 100 + 150}ms`,
            "box-shadow 0.2s ease",
            "background-color 0.2s ease",
          ].join(", "),
        }}
      >
        <div className="flex gap-2 items-center mb-3">
          <span
            style={{
              width: 10,
              height: 10,
              borderRadius: "50%",
              backgroundColor: color,
              display: "inline-block",
              transition: "transform 0.2s",
              transform: isDragOver ? "scale(1.4)" : "scale(1)",
            }}
          />
          <p className="text-[15px] font-semibold">{label}</p>
          <span
            className="text-[12px] font-semibold px-1.5 py-0.5 rounded-md"
            style={{
              backgroundColor: `${color}18`,
              color,
              minWidth: 22,
              textAlign: "center",
            }}
          >
            {tasks?.length}
          </span>
        </div>

        <Droppable droppableId={sectionId}>
          {(provided, snapshot) => {
            if (snapshot.isDraggingOver !== isDragOver)
              setTimeout(() => setIsDragOver(snapshot.isDraggingOver), 0);

            return (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                style={{
                  minHeight: 48,
                  display: "flex",
                  flexDirection: "column",
                  gap: 8,
                  borderRadius: 8,
                  padding: snapshot.isDraggingOver ? "4px" : 0,
                  background: snapshot.isDraggingOver
                    ? `${color}0A`
                    : "transparent",
                  transition: "background 0.15s ease, padding 0.15s ease",
                }}
              >
                {tasks?.map((task, idx) => (
                  <Draggable
                    key={task.id}
                    draggableId={String(task.id)}
                    index={idx}
                  >
                    {(provided, snapshot) => {
                      const child = (
                        <div
                          ref={provided.innerRef}
                          {...provided.draggableProps}
                          {...provided.dragHandleProps}
                          style={{
                            ...provided.draggableProps.style,
                            borderRadius: 12,
                          }}
                        >
                          <Task
                            task={task}
                            isDragging={snapshot.isDragging}
                            currentUserId={profile?.id}
                            userRole={profile?.role}
                          />
                        </div>
                      );

                      if (snapshot.isDragging) {
                        return ReactDOM.createPortal(
                          <div
                            ref={provided.innerRef}
                            {...provided.draggableProps}
                            {...provided.dragHandleProps}
                            style={{
                              ...provided.draggableProps.style,
                              borderRadius: 12,
                              filter:
                                "drop-shadow(0 16px 32px rgba(0,0,0,0.22))",
                            }}
                          >
                            <Task
                              task={task}
                              isDragging={true}
                              currentUserId={profile?.id}
                              userRole={profile?.role}
                            />
                          </div>,
                          document.body,
                        );
                      }

                      return child;
                    }}
                  </Draggable>
                ))}

                {provided.placeholder}

                {tasks?.length === 0 && (
                  <p
                    className="text-[13px] text-center py-3"
                    style={{
                      color: snapshot.isDraggingOver ? color : "#bbb",
                      border: `1.5px dashed ${snapshot.isDraggingOver ? color + "80" : color + "40"}`,
                      borderRadius: 10,
                      fontWeight: snapshot.isDraggingOver ? 600 : 400,
                      transition: "all 0.15s ease",
                    }}
                  >
                    {snapshot.isDraggingOver ? "Drop here" : "No tasks"}
                  </p>
                )}
              </div>
            );
          }}
        </Droppable>
      </div>
    </>
  );
};

export default TaskSection;
