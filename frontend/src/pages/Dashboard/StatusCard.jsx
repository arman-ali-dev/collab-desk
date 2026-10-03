const StatusCard = ({ icon, iconBg, statusIcon, label, num, total }) => {
  return (
    <>
      <div
        style={{
          opacity: 1,
          transform: "translateY(0) scale(1)",
          transition: `opacity 0.5s ease 100ms, transform 0.5s cubic-bezier(0.34,1.56,0.64,1) 100ms, box-shadow 0.25s ease`,
          boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
          cursor: "default",
        }}
        className="rounded-2xl px-6 py-5 bg-white"
      >
        <div
          style={{
            backgroundColor: iconBg,
            transform: "scale(1) rotate(0deg)",
            transition: "transform 0.3s cubic-bezier(0.34,1.56,0.64,1)",
          }}
          className="rounded-full h-11 w-11 flex justify-center items-center"
        >
          <img src={icon} className="w-5.5 h-5.5" alt="" />
        </div>

        <p
          className="text-[14px] font-medium mt-3"
          style={{
            opacity: 0.75,
            transition: `opacity 0.4s ease 300ms`,
          }}
        >
          {label}
        </p>

        <div className="mt-2 flex justify-between items-center">
          <p
            className="text-[27px] font-semibold"
            style={{
              opacity: 0.85,
              transition: "color 0.2s ease",
              color: "#111",
            }}
          >
            {total}
          </p>

          <span
            className="flex gap-1 items-center py-0.5 px-3 rounded-full text-[11px] font-medium"
            style={{
              backgroundColor: "rgba(1,255,18,0.15)",
              color: "#1C8F24",
              transform: "scale(1)",
              transition: "transform 0.2s ease",
            }}
          >
            <img src={statusIcon} className="w-3 h-3" alt="" />
            {num}
          </span>
        </div>
      </div>
    </>
  );
};

export default StatusCard;
