import { Skeleton } from "@mui/material";

const ProjectCardSkeleton = () => (
  <div className="bg-white rounded-xl shadow px-6 py-5">
    <Skeleton
      variant="rectangular"
      width={55}
      height={55}
      sx={{ borderRadius: "8px" }}
    />
    <Skeleton height={18} width="60%" sx={{ mt: 2 }} />
    <Skeleton height={14} width="80%" />
    <Skeleton height={14} width="100%" sx={{ mt: 2 }} />
    <Skeleton height={14} width="90%" />
    <Skeleton height={14} width="70%" />
    <Skeleton height={8} width="100%" sx={{ mt: 3, borderRadius: "4px" }} />
    <div className="flex justify-between items-center mt-6">
      <Skeleton height={28} width={100} sx={{ borderRadius: "6px" }} />
      <div className="flex -space-x-3">
        <Skeleton variant="circular" width={32} height={32} />
        <Skeleton variant="circular" width={32} height={32} />
      </div>
    </div>
  </div>
);

export default ProjectCardSkeleton;
