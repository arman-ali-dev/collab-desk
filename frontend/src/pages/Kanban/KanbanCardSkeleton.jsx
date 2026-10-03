import { Skeleton } from "@mui/material";

const KanbanCardSkeleton = () => {
  return (
    <div className="border border-[rgba(221,221,221,.7)] px-5 py-4 rounded-md">
      <Skeleton variant="text" width="80%" height={22} />
      <Skeleton variant="text" width="60%" height={16} />
      <div className="flex gap-2 mt-4">
        <Skeleton variant="rounded" width={70} height={28} />
        <Skeleton variant="rounded" width={40} height={28} />
      </div>
      <Skeleton variant="text" width="100%" height={16} className="mt-3" />
      <Skeleton variant="text" width="90%" height={16} />
      <div className="flex justify-between items-center mt-4">
        <div className="flex gap-3">
          <Skeleton variant="circular" width={24} height={24} />
          <Skeleton variant="circular" width={24} height={24} />
        </div>
        <div className="flex gap-2">
          <Skeleton variant="circular" width={32} height={32} />
          <Skeleton variant="circular" width={32} height={32} />
        </div>
      </div>
    </div>
  );
};

export default KanbanCardSkeleton;
