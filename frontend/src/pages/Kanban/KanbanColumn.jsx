import { Droppable } from "@hello-pangea/dnd";
import IconButton from "@mui/material/IconButton";
import KanbanCardSkeleton from "./KanbanCardSkeleton";
import KanbanCard from "./KanbanCard";
import plusIcon from "../../assets/plus.png";
import menuIcon from "../../assets/menu.png";
import { useSelector } from "react-redux";
import { useState } from "react";
import CreateNewTaskForm from "./CreateNewTaskForm";

const KanbanColumn = ({ title, tasks, loading, projectId, status }) => {
  const { profile } = useSelector((state) => state.profile);

  const [open, setOpen] = useState(false);

  const toggleDrawer = (value) => (event) => {
    setOpen(value);
  };

  return (
    <>
      <div className="bg-white px-4 py-6  rounded-lg shadow w-85">
        <div className="flex justify-between items-center">
          <div className="flex gap-3 items-center">
            <IconButton
              disabled={profile?.role !== "ADMIN"}
              onClick={(e) => {
                toggleDrawer(true)();
                e.stopPropagation();
              }}
              sx={{
                width: 38,
                height: 38,
                borderRadius: "8px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",

                backgroundColor: "#EFEFEF",

                opacity: 1,
                filter: "none",

                "&.Mui-disabled": {
                  backgroundColor: "#EFEFEF",
                  opacity: 0.9,
                  filter: "blur(1px)",
                  cursor: "not-allowed",
                },

                "&:hover": {
                  backgroundColor: "#EFEFEF",
                },
              }}
            >
              <img className="w-3.5" src={plusIcon} alt="" />
            </IconButton>

            <p className="text-[15px] font-semibold ">{title}</p>
            <p className="text-[13px] font-semibold -mt-2 -ml-2">
              {tasks.length}
            </p>
          </div>

          <div>
            <img src={menuIcon} alt="menu icon" className="w-5 h-5 " />
          </div>
        </div>

        <div className="mt-5 space-y-4">
          <Droppable droppableId={status}>
            {(provided) => (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                className="mt-5 space-y-4 min-h-10"
              >
                {loading
                  ? [1, 2].map((elem) => <KanbanCardSkeleton key={elem} />)
                  : tasks.map((task, idx) => (
                      <KanbanCard task={task} idx={idx} key={task.id} />
                    ))}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </div>
      </div>

      <CreateNewTaskForm
        status={status}
        projectId={projectId}
        toggleDrawer={toggleDrawer}
        open={open}
      />
    </>
  );
};

export default KanbanColumn;
