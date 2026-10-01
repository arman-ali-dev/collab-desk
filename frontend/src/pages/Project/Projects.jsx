import { IconButton, Skeleton } from "@mui/material";
import searchIcon from "../../assets/search.png";
import filterIcon from "../../assets/filter.png";
import plusIcon from "../../assets/plus.png";
import ProjectCard from "./ProjectCard";
import { useDispatch, useSelector } from "react-redux";
import { getAllProjects } from "../../store/member/projectSlice";
import { useEffect, useState } from "react";
import ProjectCardSkeleton from "./ProjectCardSkeleton";
import AddProjectForm from "./AddProjectForm";

const Projects = () => {
  const dispatch = useDispatch();
  const { projects, loading, error } = useSelector(
    (state) => state.memberProjects,
  );

  useEffect(() => {
    dispatch(getAllProjects());
  }, [dispatch]);

  const [open, setOpen] = useState(false);
  const toggleDrawer = (value) => (event) => {
    setOpen(value);
  };

  if (error) {
    return (
      <>
        <div>
          <p className="text-red-500">{error}</p>
          <button onClick={() => dispatch(fetchProjects())}>Retry</button>
        </div>
      </>
    );
  }

  return (
    <>
      <div
        className="mt-4 mx-8 relative"
        style={{
          transition: "opacity 0.3s ease",
        }}
      >
        <div
          className="bg-white flex justify-between items-center rounded-lg px-5 py-2.5"
          style={{
            boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
            transition: "opacity 0.4s ease 0.05s, transform 0.4s ease 0.05s",
          }}
        >
          <div
            className="search-input-wrap flex gap-2 items-center bg-[#EFEFEF] px-3 rounded-lg"
            style={{ minWidth: 200 }}
          >
            <div className="flex justify-center items-center">
              <img
                className="w-3.5"
                src={searchIcon}
                alt=""
                style={{ transition: "opacity 0.2s", opacity: 0.6 }}
              />
            </div>
            <input
              className="border-0 outline-0 py-2.5 text-[13px] placeholder:text-[13px]  bg-transparent w-full"
              style={{
                color: "#000",
                opacity: 0.8,
                transition: "opacity 0.2s",
              }}
              type="text"
              placeholder="Search here..."
            />
          </div>

          <div className="flex gap-2">
            <IconButton
              onClick={toggleDrawer(true)}
              sx={{
                width: 36,
                height: 36,
                backgroundColor: "#EFEFEF",
                borderRadius: "8px",
                transition:
                  "background 0.18s ease, transform 0.15s ease !important",
                "&:hover": {
                  backgroundColor: "#e0e0e0 !important",
                  transform: "scale(1.06) !important",
                },
                "&:active": { transform: "scale(0.93) !important" },
              }}
            >
              <img
                src={plusIcon}
                alt=""
                className="w-3.5"
                style={{
                  transition: "transform 0.2s ease",
                }}
              />
            </IconButton>

            <IconButton
              sx={{
                width: 36,
                height: 36,
                backgroundColor: "#EFEFEF",
                borderRadius: "8px",
                transition:
                  "background 0.18s ease, transform 0.15s ease !important",
                "&:hover": {
                  backgroundColor: "#e0e0e0 !important",
                  transform: "scale(1.06) !important",
                },
                "&:active": { transform: "scale(0.93) !important" },
              }}
            >
              <img
                src={filterIcon}
                alt=""
                className="w-4"
                style={{
                  transition: "transform 0.2s ease",
                }}
              />
            </IconButton>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-5 mt-6">
          {loading
            ? [1, 2, 3].map((elem) => <ProjectCardSkeleton key={elem} />)
            : projects?.map((project) => (
                <ProjectCard key={project.id} project={project} />
              ))}
        </div>
      </div>

      <AddProjectForm toggleDrawer={toggleDrawer} open={open} />
    </>
  );
};

export default Projects;
