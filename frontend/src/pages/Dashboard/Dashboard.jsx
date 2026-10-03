import { useDispatch, useSelector } from "react-redux";
import FilesUploadedCard from "./FilesUploadedCard";
import StatusCardsSection from "./StatusCardsSection";
import TaskStatusCard from "./TaskStatusCard";
import TaskTable from "./TaskTable";
import { useEffect } from "react";
import { fetchTasks } from "../../store/admin/taskSlice";
import { getAllProjects } from "../../store/member/projectSlice";
import { getAllUsers } from "../../store/admin/userSlice";

const Dashboard = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchTasks());
    dispatch(getAllProjects());
    dispatch(getAllUsers());
  }, [dispatch]);

  return (
    <>
      <div
        className="mt-4 mx-8 relative"
        style={{
          opacity: 1,
          transition: "opacity 0.3s ease",
        }}
      >
        <StatusCardsSection />

        <div className="grid grid-cols-6 gap-4 items-stretch mt-4">
          <div
            className="col-span-4 h-full"
            style={{
              transition: "opacity 0.5s ease 0.15s, transform 0.5s ease 0.15s",
            }}
          >
            <FilesUploadedCard />
          </div>

          <div
            className="col-span-2 h-full"
            style={{
              transition: "opacity 0.5s ease 0.25s, transform 0.5s ease 0.25s",
            }}
          >
            <TaskStatusCard />
          </div>
        </div>

        <div
          style={{
            transition: "opacity 0.5s ease 0.35s, transform 0.5s ease 0.35s",
          }}
        >
          <TaskTable />
        </div>
      </div>
    </>
  );
};

export default Dashboard;
