import StatusCard from "./StatusCard";
import layerIcon from "../../assets/layers.png";
import topIcon from "../../assets/top.png";
import top2Icon from "../../assets/top2.png";
import folderIcon from "../../assets/folder.png";
import checklistIcon from "../../assets/checklist2.png";
import groupIcon from "../../assets/group2.png";
import { useSelector } from "react-redux";

const StatusCardsSection = () => {
  const { projects } = useSelector((state) => state.memberProjects);
  const { tasks } = useSelector((state) => state.adminTasks);
  const { users } = useSelector((state) => state.adminUsers);
  return (
    <>
      <div className="grid grid-cols-4 gap-4">
        <StatusCard
          icon={layerIcon}
          iconBg="#8127FF"
          statusIcon={topIcon}
          label="Total Projects"
          num={22.34}
          total={0 || projects.length}
          delay={0}
        />
        <StatusCard
          icon={folderIcon}
          iconBg="#F55600"
          statusIcon={topIcon}
          label="Project Files"
          num={10.56}
          delay={80}
          total={12}
        />
        <StatusCard
          icon={checklistIcon}
          iconBg="#157FD7"
          statusIcon={topIcon}
          label="Assigned Tasks"
          num={19.45}
          total={tasks.length}
          delay={160}
        />
        <StatusCard
          icon={groupIcon}
          iconBg="#18A322"
          statusIcon={topIcon}
          label="Team Members"
          num={9.34}
          delay={240}
          total={users.length}
        />
      </div>
    </>
  );
};

export default StatusCardsSection;
