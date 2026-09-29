import React, { useEffect, useRef, useState } from "react";
import externalIcon from "../../assets/external.png";
import { Link, useNavigate } from "react-router-dom";
import clockIcon from "../../assets/clock.png";
import { CircularProgress, IconButton, Skeleton } from "@mui/material";
import editIcon from "../../assets/edit.png";
import deleteIcon from "../../assets/delete.png";
import userAvatar from "../../assets/userAvatar.png";
import projectLogo from "../../assets/ahitlogo.webp";

const ProjectCard = () => {
  const progressColor =
    11.2 > 50 ? "#18A322" : 11.2 === 50 ? "#157FD7" : "#FA2626";
  return (
    <>
      <div>
        <div
          className="bg-white group relative cursor-pointer rounded-xl px-6 py-5"
          style={{
            transition:
              "opacity 0.45s ease 100ms, transform 0.45s cubic-bezier(0.34,1.2,0.64,1) 100ms, box-shadow 0.25s ease",
            boxShadow: "0 2px 8px rgba(0,0,0,0.07)",
          }}
        >
          {"MEMBER" === "ADMIN" && (
            <div
              className="absolute top-3 right-3 flex gap-1 z-50"
              style={{
                transition: "opacity 0.2s ease, transform 0.2s ease",
              }}
            >
              <IconButton
                size="small"
                sx={{
                  transition:
                    "background 0.15s ease, transform 0.15s ease !important",
                  "&:hover": {
                    backgroundColor: "#f0f0f0 !important",
                    transform: "scale(1.1) !important",
                  },
                }}
              >
                <img className="w-4.5" src={editIcon} alt="Edit" />
              </IconButton>

              <IconButton
                size="small"
                sx={{
                  transition:
                    "background 0.15s ease, transform 0.15s ease !important",
                  "&:hover": {
                    backgroundColor: "#fff0f0 !important",
                    transform: "scale(1.1) !important",
                  },
                }}
              >
                <img className="w-4" src={deleteIcon} alt="Delete" />
              </IconButton>
            </div>
          )}

          <img
            className="w-16 h-16 object-contain"
            src={projectLogo}
            alt=""
            style={{
              transition: "transform 0.3s cubic-bezier(0.34,1.56,0.64,1)",
            }}
          />

          <h3
            className="font-medium text-[14px] mt-2.5"
            style={{ opacity: 0.8 }}
          >
            All hind info
          </h3>

          <Link
            to={"https://ahit.com"}
            onClick={(e) => e.stopPropagation()}
            className="font-medium -mt-1 text-[12px] flex gap-1 items-center"
            target="_blank"
            rel="noopener noreferrer"
            style={{
              color: "#747373",
              transition: "color 0.15s ease",
            }}
            onMouseEnter={(e) => (e.currentTarget.style.color = "#157FD7")}
            onMouseLeave={(e) => (e.currentTarget.style.color = "#747373")}
          >
            <img className="w-2.5" src={externalIcon} alt="" />
            <span>https://ahit.com</span>
          </Link>

          <p className="text-[13.5px] font-medium mt-4">
            Lorem ipsum, dolor sit amet consectetur pisicing elit. Repellendus,
            accusamus!
          </p>

          <div className="mt-5">
            <p
              className="text-[13px] text-right font-medium"
              style={{
                transition: "color 0.3s ease",
                color: "inherit",
              }}
            >
              11.2%
            </p>
            <div className="h-1 w-full bg-[#D4D9D4] rounded-full overflow-hidden">
              <div
                style={{
                  width: "11.2%",
                  backgroundColor: progressColor,
                  height: "100%",
                  borderRadius: "inherit",
                  transition: "width 0.9s cubic-bezier(0.22,1,0.36,1)",
                }}
              />
            </div>
          </div>

          <div className="mt-6 flex justify-between items-center">
            <div
              className="bg-[#ebebeb] rounded-md flex gap-2 items-center py-1.5 px-3 text-[#605D5D] text-[11px]"
              style={{
                transition: "background 0.2s ease, transform 0.2s ease",
              }}
            >
              <img className="w-3" src={clockIcon} alt="" />
              <span className="font-medium">2 days left</span>
            </div>

            <div className="flex">
              <div
                className="min-w-8 min-h-8 w-8 h-8 rounded-full object-cover flex items-center justify-center text-white text-[13px] font-semibold  z-50 border-white border-2"
                style={{
                  backgroundColor: "#9c9b9b",
                  transition: `transform 0.2s ease ${1 * 35}ms`,
                }}
              >
                <img className="rounded-full" src={userAvatar} alt="" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ProjectCard;
