import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Modal,
  Snackbar,
} from "@mui/material";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import userAvatar from "../../assets/userAvatar.png";
import addMemberIcon from "../../assets/addMember.png";
import { clearSearchResults, searchUser } from "../../store/admin/userSlice";
import { updateMembersInTask } from "../../store/admin/taskSlice";
import { updateStatusInKanban } from "../../store/member/taskSlice";

const UpdateMembersModal = ({
  open,
  handleClose,
  alreadyAssignedMembers,
  taskId,
}) => {
  const dispatch = useDispatch();
  const { searchResults, searchLoading } = useSelector(
    (state) => state.adminUsers,
  );
  const { updateMemberLoading } = useSelector((state) => state.adminTasks);
  const [search, setSearch] = useState("");
  const [selectedMembers, setSelectedMembers] = useState(
    alreadyAssignedMembers || [],
  );
  const [error, setError] = useState("");
  const [openSnack, setOpenSnack] = useState(false);

  const handleAddMember = (user) => {
    if (!selectedMembers.find((member) => member.id === user.id)) {
      setSelectedMembers([...selectedMembers, user]);
      setError("");
    }
    setSearch("");
  };

  const handleRemoveMember = (userId) => {
    setSelectedMembers(
      selectedMembers.filter((member) => member.id !== userId),
    );
  };

  const handleAddMembers = async () => {
    if (selectedMembers.length === 0) {
      setError("Please add at least one member");
      return;
    }

    const updatedTask = await dispatch(
      updateMembersInTask({
        id: taskId,
        assignedTo: selectedMembers.map((u) => u.id),
      }),
    ).unwrap();

    dispatch(updateStatusInKanban(updatedTask));

    handleClose();
    setOpenSnack(true);
  };

  const handleSearch = (e) => {
    const value = e.target.value;
    setSearch(value);

    if (value.trim().length < 2) {
      dispatch(clearSearchResults());
      return;
    }

    dispatch(searchUser(value.trim()));
  };
  return (
    <>
      <Modal open={open} onClose={handleClose}>
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 500,
            bgcolor: "background.paper",
            borderRadius: "12px",
            boxShadow: 24,
            py: 3,
            px: 2.5,
            outline: "none",
          }}
        >
          <div className="flex gap-4 items-center">
            <div className="w-16 h-16 rounded-xl flex justify-center bg-black items-center">
              <img src={addMemberIcon} className="w-8 h-8" alt="" />
            </div>

            <div>
              <h3 className="text-[15px] font-semibold">Add Members</h3>
              <p className="text-[#686565] text-[13px] font-medium">
                Manage the members than can access your document{" "}
              </p>
            </div>
          </div>

          <div className="mt-4 relative">
            <p className="text-[#403F3F] text-[13px] font-medium">
              Add Members
            </p>
            <div className="flex gap-2 mt-0.5">
              <div className="flex-1 relative">
                <input
                  className="text-[13px] outline-0 px-4 py-2 w-full border-[#D3D3D3] border rounded-md"
                  type="text"
                  placeholder="Search for names or emails..."
                  value={search}
                  onChange={handleSearch}
                />
                {search.trim().length >= 2 && (
                  <div className="absolute w-full bg-white border border-[#BCBCBC] mt-1 z-20 max-h-56 overflow-y-auto rounded-sm shadow-lg">
                    {searchLoading && (
                      <p className="px-4 py-3 text-[13px]">Searching...</p>
                    )}

                    {!searchLoading && searchResults.length === 0 && (
                      <p className="px-4 py-3 text-[13px]">No users found</p>
                    )}

                    {!searchLoading &&
                      searchResults?.map((user) => {
                        const alreadyAdded = selectedMembers.includes(user.id);
                        return (
                          <div
                            key={user.id}
                            onClick={() =>
                              !alreadyAdded && handleAddMember(user)
                            }
                            className={`flex items-center gap-2 px-3 py-2 cursor-pointer hover:bg-gray-100 ${alreadyAdded ? "opacity-50 cursor-not-allowed" : ""}`}
                          >
                            <img
                              src={userAvatar}
                              className="w-8 h-8 rounded-full object-cover"
                              alt=""
                            />
                            <div>
                              <p className="text-[13px] font-medium">
                                {user.fullName}
                              </p>
                              <p className="text-[#535353] text-[11px]">
                                {user.email}
                              </p>
                            </div>
                            {alreadyAdded && (
                              <span className="ml-auto text-[11px] text-green-600">
                                Added
                              </span>
                            )}
                          </div>
                        );
                      })}
                  </div>
                )}
                <div className="mt-4">
                  <p className="text-[#403F3F] text-[13px] font-medium mb-1">
                    Selected Members ({selectedMembers.length})
                  </p>

                  {1 === 0 ? (
                    <p className="text-[#9E9E9E] text-[12px] text-center py-4">
                      No members added yet
                    </p>
                  ) : (
                    <div className="mt-3 space-y-3 max-h-60 overflow-y-auto">
                      {selectedMembers.map((member) => (
                        <div
                          key={member.id}
                          className="flex justify-between items-center"
                        >
                          <div className="flex gap-2 items-center">
                            <div>
                              <img
                                src={member.profileImage || userAvatar}
                                className="w-9 h-9 rounded-full object-cover"
                                alt=""
                              />
                            </div>
                            <div className="mt-0.5">
                              <p className="text-[13px] font-medium">
                                {member.fullName}
                              </p>
                              <p className="text-[#535353] text-[12px] -mt-0.5">
                                {member.email}
                              </p>
                            </div>
                          </div>
                          <button
                            onClick={() => handleRemoveMember(member.id)}
                            className="text-red-500 cursor-pointer text-[12px] font-medium hover:text-red-700"
                          >
                            Remove
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
                {error && (
                  <div className="mt-3 bg-red-50 border border-red-200 rounded-md px-3 py-2">
                    <p className="text-red-600 text-[12px] font-medium">
                      {error}
                    </p>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="mt-5 flex gap-2 justify-end">
            <Button
              onClick={handleClose}
              disabled={updateMemberLoading}
              sx={{
                textTransform: "capitalize",
                border: "1px solid #D3D3D3",
                color: "#000",
                paddingX: "20px",
                fontSize: "13px",
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleAddMembers}
              disabled={updateMemberLoading}
              sx={{
                textTransform: "capitalize",
                backgroundColor: "#000",
                border: "1px solid #000",
                color: "#fff",
                paddingX: "20px",
                fontSize: "13px",
                minWidth: "130px",
                "&:hover": {
                  backgroundColor: "#333",
                },
                "&:disabled": {
                  backgroundColor: "#999",
                },
              }}
            >
              {updateMemberLoading ? (
                <CircularProgress size={20} sx={{ color: "#fff" }} />
              ) : (
                "Add Members"
              )}
            </Button>
          </div>
        </Box>
      </Modal>

      <Snackbar
        open={openSnack}
        autoHideDuration={3000}
        onClose={() => setOpenSnack(false)}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={() => setOpenSnack(false)}
          severity={"success"}
          sx={{ width: "100%", fontSize: "13px" }}
        >
          Members updated
        </Alert>
      </Snackbar>
    </>
  );
};

export default UpdateMembersModal;
