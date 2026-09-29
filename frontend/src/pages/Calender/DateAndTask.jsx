const DateAndTask = () => {
  return (
    <>
      <div
        className={`w-[14.28%] border border-[rgba(200,200,200,0.8)] rounded-lg px-4 py-4 ${
          false
            ? "bg-[rgba(217,217,217,0.1)] cursor-pointer hover:bg-[rgba(217,217,217,0.2)]"
            : ""
        }  transition-all`}
      >
        <h2
          className={`text-[20px] font-semibold ${
            false ? "text-[#969696]" : true ? "text-black" : "text-[#969696]"
          }`}
        >
          1
        </h2>
        <p
          className={`text-[15px] mt-3 font-semibold ${
            true ? "text-[#2D69FF]" : "text-[#969696]"
          }`}
        >
          1 task{1 !== 1 ? "s" : ""}
        </p>
      </div>
    </>
  );
};

export default DateAndTask;
