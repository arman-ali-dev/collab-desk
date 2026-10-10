import chronometerIcon from "../../assets/chronometer.png";
import teamIcon from "../../assets/team.png";
import messageIcon from "../../assets/mes.png";
import { IconButton, Tooltip } from "@mui/material";
import plusIcon from "../../assets/plus.png";
import { Draggable } from "@hello-pangea/dnd";
import userAvatar from "../../assets/userAvatar.png";
import { useSelector } from "react-redux";
import UpdateMembersModal from "./UpdateMembersModal";
import { useState } from "react";

const KanbanCard = ({ task, idx }) => {
  const { profile } = useSelector((state) => state.profile);
  const canDrag =
    profile?.role === "ADMIN" ||
    task?.assignedTo.find((elem) => elem.id == profile?.id);

  const [addMemberOpen, setAddMemberOpen] = useState(false);

  return (
    <>
      <Draggable
        isDragDisabled={!canDrag}
        draggableId={String(task?.id)}
        index={idx}
      >
        {(provided) => (
          <div
            ref={provided.innerRef}
            {...provided.draggableProps}
            {...provided.dragHandleProps}
          >
            <div className="border-[rgba(221,221,221,.7)] relative border px-5 py-4 rounded-md">
              <h3 className="text-[14px] font-medium">
                {idx + 1}. {task?.title}
              </h3>

              <p className="text-[12px] text-[#969696] mt-0.5 flex gap-3">
                <span>
                  {new Date(task?.dueDate.split("T")[0]).toLocaleDateString(
                    "en-US",
                    { month: "short", day: "numeric", year: "numeric" },
                  )}
                </span>
                <span> | </span>
                <span className="flex items-center gap-1">
                  <img
                    src={chronometerIcon}
                    alt="chronometer icon"
                    className="w-3 h-3 mr-1 inline-block"
                  />
                  {task.estimatedTime} Hours
                </span>
              </p>

              <div className="space-x-2 mt-4">
                <div
                  style={{
                    color:
                      task.category === "DESIGN"
                        ? "#497AF5"
                        : task.category === "DEVELOPMENT"
                          ? "rgba(250,38,38,.7)"
                          : "#09C015",
                    backgroundColor:
                      task.category === "DESIGN"
                        ? "rgba(73,122,245,0.2)"
                        : task.category === "DEVELOPMENT"
                          ? "rgba(222,23,23,.2)"
                          : "rgba(1,255,18,.3)",
                  }}
                  className="text-[#497AF5] bg-[rgba(73,122,245,0.1)] px-3 py-2 rounded-md text-[12px] inline-block"
                >
                  {task.category}
                </div>
              </div>

              <p className="text-[13px] font-medium mt-2.5">
                {task.description.split(" ").slice(0, 10).join(" ")}
                {task.description.split(" ").length > 10 && "..."}
              </p>

              <div className="mt-4 flex items-center justify-between">
                <div className="flex gap-3 items-center">
                  <div className="flex gap-1.5 items-center">
                    <img
                      src={teamIcon}
                      alt="team icon"
                      className="w-4.5 h-4.5"
                    />
                    <span className="text-[#969696] text-[11px]">
                      {task.assignedTo.length}
                    </span>
                  </div>

                  <Tooltip title="View comments">
                    <button
                      onClick={() => setCommentOpen(true)}
                      className="flex gap-1.5 items-center hover:opacity-70 transition-opacity"
                    >
                      <img
                        src={messageIcon}
                        alt="message icon"
                        className="w-3 h-3"
                      />
                      <span className="text-[#969696] text-[11px]">
                        {task.assignedTo.length}
                      </span>
                    </button>
                  </Tooltip>
                </div>

                <div className="flex">
                  {profile?.role == "ADMIN" && (
                    <IconButton
                      onClick={() => setAddMemberOpen(true)}
                      sx={{
                        width: 33,
                        height: 33,
                        backgroundColor: "#EFEFEF",
                        cursor: "pointer",
                        borderRadius: "50px",
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                        marginRight: "10px",
                        "&:hover": { backgroundColor: "#EFEFEF" },
                      }}
                    >
                      <img className="w-3" src={plusIcon} alt="" />
                    </IconButton>
                  )}

                  {task.assignedTo.map((m, idx) => (
                    <Tooltip key={m.id} title={m.fullName}>
                      <div
                        key={idx}
                        className={`min-w-8 min-h-8 w-8 h-8 rounded-full object-cover flex items-center justify-center text-white text-[13px] font-semibold ${idx !== task.assignedTo?.length - 1 ? "-mr-3 z-50 border-white border" : ""}`}
                        style={{
                          backgroundColor: "#9c9b9b",
                        }}
                      >
                        <img
                          className="rounded-full w-full h-full object-cover"
                          src={m.profileImage || userAvatar}
                          alt=""
                        />
                      </div>
                    </Tooltip>
                  ))}
                </div>
              </div>

              <div className="h-10 w-0.5 rounded-md bg-[#497AF5] lineShadow absolute left-0 top-4" />
            </div>
          </div>
        )}
      </Draggable>

      <UpdateMembersModal
        taskId={task.id}
        alreadyAssignedMembers={task.assignedTo}
        open={addMemberOpen}
        handleClose={() => setAddMemberOpen(false)}
      />
    </>
  );
};

export default KanbanCard;
