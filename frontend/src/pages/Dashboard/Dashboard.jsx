import FilesUploadedCard from "./FilesUploadedCard";
import StatusCardsSection from "./StatusCardsSection";
import TaskStatusCard from "./TaskStatusCard";
import TaskTable from "./TaskTable";

const Dashboard = () => {
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
