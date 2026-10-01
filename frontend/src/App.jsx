import { Route, Routes, useLocation } from "react-router-dom";
import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard/Dashboard";
import Projects from "./pages/Project/Projects";
import Calendar from "./pages/Calender/Calender";
import Users from "./pages/Users/Users";
import MyTasks from "./pages/Task/MyTasks";
import Signin from "./pages/Auth/Signin";

function App() {
  const location = useLocation();
  const isAuthPage = location.pathname === "/signin";

  const hideLayout = isAuthPage;
  return (
    <>
      <div className="flex h-screen overflow-hidden">
        {!hideLayout && <Sidebar />}
        <div
          className={`flex-1 flex flex-col ${!hideLayout ? "ml-76.25" : ""} overflow-hidden`}
        >
          {!hideLayout && <Navbar />}

          <div className="flex-1 overflow-y-auto">
            <Routes>
              <Route element={<Dashboard />} path="/dashboard" />
              <Route element={<Projects />} path="/projects" />
              <Route element={<Calendar />} path="/calendar" />
              <Route element={<Users />} path="/users" />
              <Route element={<MyTasks />} path="/my-tasks" />
              <Route element={<Signin />} path="/signin" />
            </Routes>
          </div>
        </div>
      </div>
    </>
  );
}

export default App;
