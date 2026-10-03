import { Divider, IconButton, Menu, MenuItem, Skeleton } from "@mui/material";
import searchIcon from "../../assets/search.png";
import filterIcon from "../../assets/filter.png";
import plusIcon from "../../assets/plus.png";
import ProjectCard from "./ProjectCard";
import { useDispatch, useSelector } from "react-redux";
import {
  clearSearchResults,
  filterProjects,
  getAllProjects,
  searchProjects,
} from "../../store/member/projectSlice";
import { useEffect, useState } from "react";
import ProjectCardSkeleton from "./ProjectCardSkeleton";
import AddProjectForm from "./AddProjectForm";

const Projects = () => {
  const dispatch = useDispatch();

  // fetch projects

  const { projects, loading, error } = useSelector(
    (state) => state.memberProjects,
  );

  useEffect(() => {
    dispatch(getAllProjects());
  }, [dispatch]);

  // add project

  const [open, setOpen] = useState(false);
  const toggleDrawer = (value) => (event) => {
    setOpen(value);
  };

  // search projects

  const [search, setSearch] = useState("");
  const { searchResults } = useSelector((state) => state.memberProjects);

  const handleSearch = (e) => {
    const value = e.target.value;
    setSearch(value);

    if (value.trim().length < 2) {
      dispatch(clearSearchResults());
      return;
    }

    dispatch(searchProjects(value.trim()));
  };

  const [filterAnchorEl, setFilterAnchorEl] = useState(null);
  const openFilterDropDown = Boolean(filterAnchorEl);

  // filter projects

  const [status, setStatus] = useState(null);
  const [priority, setPriority] = useState(null);

  const filterOptions = [
    {
      type: "item",
      label: "All Projects",
      action: () => {
        setStatus(null);
        setPriority(null);
      },
    },
    { type: "divider" },
    {
      type: "item",
      label: "Active Projects",
      action: () => {
        setPriority(null);
        setStatus("ACTIVE");
      },
    },
    {
      type: "item",
      label: "Completed Projects",
      action: () => {
        setPriority(null);
        setStatus("COMPLETED");
      },
    },
    {
      type: "item",
      label: "On Hold",
      action: () => {
        setPriority(null);
        setStatus("ON_HOLD");
      },
    },
    { type: "divider" },
    {
      type: "item",
      label: "High Priority",
      action: () => {
        setStatus(null);
        setPriority("HIGH");
      },
    },
    {
      type: "item",
      label: "Medium Priority",
      action: () => {
        setStatus(null);
        setPriority("MEDIUM");
      },
    },
    {
      type: "item",
      label: "Low Priority",
      action: () => {
        setStatus(null);
        setPriority("LOW");
      },
    },
  ];

  useEffect(() => {
    dispatch(filterProjects({ status, priority }));
  }, [status, priority, dispatch]);
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
              value={search}
              onChange={handleSearch}
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
              onClick={(e) => setFilterAnchorEl(e.currentTarget)}
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

            <Menu
              anchorEl={filterAnchorEl}
              open={openFilterDropDown}
              onClose={() => setFilterAnchorEl(null)}
              PaperProps={{
                sx: {
                  width: 180,
                  borderRadius: "10px",
                  boxShadow: "0 8px 24px rgba(0,0,0,0.12)",
                  mt: 0.5,
                },
              }}
              transformOrigin={{ horizontal: "right", vertical: "top" }}
              anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
            >
              {filterOptions.map((opt, i) =>
                opt.type === "divider" ? (
                  <Divider key={i} />
                ) : (
                  <MenuItem
                    key={i}
                    onClick={() => {
                      setFilterAnchorEl(null);
                      opt.action();
                    }}
                    sx={{
                      fontSize: "13px",
                      fontWeight: 700,
                      transition: "background 0.15s ease",
                    }}
                  >
                    {opt.label}
                  </MenuItem>
                ),
              )}
            </Menu>
          </div>
        </div>

        {!loading && projects.length === 0 && (
          <p className="text-center mt-5">No projects</p>
        )}
        <div className="grid grid-cols-3 gap-5 mt-6">
          {loading
            ? [1, 2, 3].map((elem) => <ProjectCardSkeleton key={elem} />)
            : searchResults.length !== 0
              ? searchResults?.map((project) => (
                  <ProjectCard key={project.id} project={project} />
                ))
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
