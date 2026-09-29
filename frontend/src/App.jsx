import { Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard/Dashboard";
import Projects from "./pages/Project/Projects";
import Calendar from "./pages/Calender/Calender";
import Users from "./pages/Users/Users";

function App() {
  return (
    <>
      <div className="flex h-screen overflow-hidden">
        <Sidebar />
        <div className={"flex-1 flex flex-col ml-76.25 overflow-hidden"}>
          <Navbar />

          <div className="flex-1 overflow-y-auto">
            <Routes>
              <Route element={<Dashboard />} path="/dashboard" />
              <Route element={<Projects />} path="/projects" />
              <Route element={<Calendar />} path="/calendar" />
              <Route element={<Users />} path="/users" />
            </Routes>
          </div>
        </div>
      </div>
    </>
  );
}

export default App;
