import Skeleton from "@mui/material/Skeleton";
import userAvatar from "../../assets/userAvatar.png";
import { Link, useNavigate, useParams } from "react-router-dom";
import IconButton from "@mui/material/IconButton";
import speechIcon from "../../assets/speech.png";
import heartIcon from "../../assets/like.png";
import { DragDropContext } from "@hello-pangea/dnd";
import KanbanColumn from "./KanbanColumn";
import {
  clearTasksProject,
  fetchTasksByProject,
  updateTaskStatus,
} from "../../store/member/taskSlice";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { clearProject, getProject } from "../../store/member/projectSlice";

const iconBtnStyle = {
  width: 36,
  height: 36,
  backgroundColor: "#EFEFEF",
  cursor: "pointer",
  borderRadius: "8px",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  "&:hover": {
    backgroundColor: "#EFEFEF",
  },
};

const KanbanBoard = () => {
  const dispatch = useDispatch();
  const params = useParams();
  const { projectId } = params;

  useEffect(() => {
    if (!projectId) return;

    dispatch(fetchTasksByProject(projectId));
    dispatch(getProject(projectId));

    return () => {
      dispatch(clearTasksProject());
      dispatch(clearProject());
    };
  }, [projectId, dispatch]);

  const { projectTasks, kanbanLoading } = useSelector(
    (state) => state.memberTasks,
  );

  const { getLoading, project } = useSelector((state) => state.memberProjects);

  const todoTasks = projectTasks?.filter((task) => task.status === "TO_DO");
  const doingTasks = projectTasks?.filter(
    (task) => task.status === "IN_PROGRESS",
  );
  const reviewsTasks = projectTasks?.filter((task) => task.status === "REVIEW");
  const doneTasks = projectTasks?.filter((task) => task.status === "DONE");

  const onDragEnd = (result) => {
    const { destination, source, draggableId } = result;

    if (!destination) return;

    if (
      destination.droppableId == source.droppableId &&
      destination.index == source.index
    ) {
      return;
    }

    dispatch(
      updateTaskStatus({ id: draggableId, status: destination.droppableId }),
    );
  };

  const navigate = useNavigate();

  return (
    <>
      <div className="mt-4 mx-8 relative flex flex-col h-full">
        <div className="bg-white shadow flex justify-between items-center rounded-lg px-5 py-2.5">
          <div className="flex gap-2 items-center">
            <div>
              {getLoading ? (
                <Skeleton variant="rounded" width={32} height={32} />
              ) : (
                <img src={project?.logo} alt="Project Logo" className="h-8" />
              )}
            </div>

            <div>
              {getLoading ? (
                <>
                  <Skeleton variant="text" width={120} height={18} />
                  <Skeleton variant="text" width={80} height={14} />
                </>
              ) : (
                <>
                  <h3 className="text-[13px] font-semibold m-0">
                    {project?.title}
                  </h3>
                  <Link
                    to="/projects"
                    className="text-[11px] flex items-center text-[#555454] -mt-1 font-semibold"
                  >
                    Change Project
                  </Link>
                </>
              )}
            </div>
          </div>

          <div className="flex gap-2">
            {getLoading ? (
              <>
                <Skeleton variant="rounded" width={36} height={36} />
                <Skeleton variant="rounded" width={36} height={36} />
              </>
            ) : (
              <>
                <IconButton onClick={() => navigate("/chat")} sx={iconBtnStyle}>
                  <img src={speechIcon} alt="" className="w-4" />
                </IconButton>

                <IconButton sx={iconBtnStyle}>
                  <img className="w-3.5" src={heartIcon} alt="" />
                </IconButton>
              </>
            )}
          </div>
        </div>

        <div className="mt-6 pb-4">
          <div className="flex-1 overflow-x-auto overflow-y-hidden custom-scrollbar">
            <div className="flex gap-4 items-start min-w-max h-full pb-6">
              <DragDropContext onDragEnd={onDragEnd}>
                <KanbanColumn
                  title="To Do"
                  status="TO_DO"
                  tasks={todoTasks}
                  projectId={project?.id}
                  loading={kanbanLoading}
                />

                <KanbanColumn
                  title="In Progress"
                  status="IN_PROGRESS"
                  tasks={doingTasks}
                  projectId={project?.id}
                  loading={kanbanLoading}
                />

                <KanbanColumn
                  title="Reviews"
                  status="REVIEW"
                  projectId={project?.id}
                  tasks={reviewsTasks}
                  loading={kanbanLoading}
                />

                <KanbanColumn
                  title="Done"
                  status="DONE"
                  projectId={project?.id}
                  tasks={doneTasks}
                  loading={kanbanLoading}
                />
              </DragDropContext>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default KanbanBoard;
