import React from "react";
import File from "./File";
import filter2Icon from "../../assets/filter2.png";

const recentFiles = [
  { id: 1, fileName: "brand-guidelines.pdf", fileType: "PDF", fileUrl: "#" },
  { id: 2, fileName: "hero-reference.png", fileType: "PNG", fileUrl: "#" },
  { id: 3, fileName: "requirements.docx", fileType: "DOCX", fileUrl: "#" },
  { id: 4, fileName: "wireframe.jpg", fileType: "JPG", fileUrl: "#" },
  { id: 5, fileName: "meeting-notes.txt", fileType: "TXT", fileUrl: "#" },
];

const RecentFiles = () => {
  return (
    <div className="bg-white shadow mt-4 rounded-lg px-5 py-4">
      <div className="flex justify-between items-center">
        <h3 className="text-[14px] font-semibold">Recent Files</h3>

        <div className="flex gap-1 items-center cursor-pointer">
          <img src={filter2Icon} alt="Filter Icon" className="w-4 h-4" />
          <p className="text-[12px] font-medium text-[#555454]">Newest First</p>
        </div>
      </div>

      <div className="grid grid-cols-5 gap-4 mt-3">
        {recentFiles.map((file) => (
          <File key={file.id} file={file} />
        ))}
      </div>
    </div>
  );
};

export default RecentFiles;
