import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

const AdminProtectedRoute = ({ children }) => {
  const { profile } = useSelector((state) => state.profile);

  if (profile?.role !== "ADMIN") {
    return <Navigate to="/not-found" replace />;
  }

  return children;
};

export default AdminProtectedRoute;
