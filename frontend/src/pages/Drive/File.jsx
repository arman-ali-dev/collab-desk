import React from "react";
import docIcon from "../../assets/doc.png";
import optionIcon from "../../assets/option.png";

const file = { fileName: "brand-guidelines.pdf" };

const File = () => {
  return (
    <div className="w-full cursor-pointer">
      <div className="bg-[rgba(150,150,150,.1)] w-full h-34 border rounded-t-[18px] flex justify-center items-center border-[rgba(200,200,200,.5)] hover:bg-[rgba(150,150,150,.2)] transition-colors">
        <img src={docIcon} alt="Document Icon" className="w-13 h-13" />
      </div>

      <div className="rounded-b-[18px] py-1.5 flex justify-between items-center px-4 border border-[#C8C8C8]">
        <p className="font-medium text-[13px] text-[#626262] truncate max-w-[80%]">
          {file.fileName}
        </p>
        <img
          src={optionIcon}
          alt="Options Icon"
          className="w-4.5 cursor-pointer h-4.5"
        />
      </div>
    </div>
  );
};

export default File;
