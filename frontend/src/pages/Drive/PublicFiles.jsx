import { IconButton } from "@mui/material";
import filterIcon from "../../assets/filter.png";
import plusIcon from "../../assets/plus.png";
import FileTable from "./FileTable";

const publicFiles = [
  { id: 1, fileName: "brand-guidelines.pdf", fileType: "PDF", fileUrl: "#" },
  { id: 2, fileName: "hero-reference.png", fileType: "PNG", fileUrl: "#" },
  { id: 3, fileName: "requirements.docx", fileType: "DOCX", fileUrl: "#" },
  { id: 4, fileName: "wireframe.jpg", fileType: "JPG", fileUrl: "#" },
  { id: 5, fileName: "meeting-notes.txt", fileType: "TXT", fileUrl: "#" },
];

const PublicFiles = () => {
  return (
    <div className="bg-white shadow mt-4 rounded-lg px-5 py-4">
      <div className="flex justify-between items-center">
        <div className="flex gap-2 items-center">
          <h3 className="text-[14px] font-semibold">Public Files</h3>
          <span className="py-1 px-3 bg-[rgba(217,217,217,.6)] text-[11px] font-medium text-[rgba(0,0,0,.5)] rounded-full">
            {publicFiles.length} Totals
          </span>
        </div>

        <div className="flex gap-2">
          <IconButton
            sx={{
              width: 36,
              height: 36,
              backgroundColor: "#EFEFEF",
              cursor: "pointer",
              borderRadius: "50px",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              "&:hover": { backgroundColor: "#EFEFEF" },
            }}
          >
            <img src={filterIcon} alt="" className="w-3.5" />
          </IconButton>

          <IconButton
            sx={{
              width: 36,
              height: 36,
              backgroundColor: "#EFEFEF",
              cursor: "pointer",
              borderRadius: "50px",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              "&:hover": { backgroundColor: "#EFEFEF" },
            }}
          >
            <img className="w-3" src={plusIcon} alt="" />
          </IconButton>
        </div>
      </div>

      <div className="mt-5">
        <FileTable files={publicFiles} />
      </div>
    </div>
  );
};

export default PublicFiles;
