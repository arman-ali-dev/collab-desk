import { Route, Routes, useLocation } from "react-router-dom";
import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard/Dashboard";
import Projects from "./pages/Project/Projects";
import Calendar from "./pages/Calendar/Calendar";
import Users from "./pages/Users/Users";
import MyTasks from "./pages/Task/MyTasks";
import Signin from "./pages/Auth/Signin";
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { fetchProfile } from "./store/profileSlice";
import Profile from "./pages/Account/Profile";
import KanbanBoard from "./pages/Kanban/KanbanBoard";
import Drive from "./pages/Drive/Drive";
import PasswordSetup from "./pages/Auth/PasswordSetup";
import Chat from "./pages/Chat/Chat";
import useStompConnection from "./hooks/UseStompConnection";
import useNotifications from "./hooks/useNotifications";
import { fetchReminders } from "./store/member/taskSlice";
import ProtectedRoute from "./components/ProtectedRoute";
import AdminProtectedRoute from "./components/AdminProtectedRoute";
import NotFound from "./pages/404/NotFound";

function App() {
  useStompConnection();
  useNotifications();

  const dispatch = useDispatch();
  const { isAuthenticated } = useSelector((state) => state.auth);

  useEffect(() => {
    if (isAuthenticated) {
      dispatch(fetchProfile());
      dispatch(fetchReminders());
    }
  }, [isAuthenticated, dispatch]);

  const location = useLocation();
  const isAuthPage =
    location.pathname === "/signin" || location.pathname === "/set-password";

  const validPaths = [
    "/dashboard",
    "/projects",
    "/my-tasks",
    "/calendar",
    "/chat",
    "/drive",
    "/users",
    "/profile",
  ];

  const isNotFoundPage =
    !validPaths.includes(location.pathname) &&
    !location.pathname.startsWith("/projects/") &&
    location.pathname !== "/signin" &&
    !location.pathname.startsWith("/set-password");

  const hideLayout = isAuthPage || isNotFoundPage;

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
              <Route
                element={
                  <ProtectedRoute>
                    <AdminProtectedRoute>
                      <Dashboard />
                    </AdminProtectedRoute>
                  </ProtectedRoute>
                }
                path="/dashboard"
              />
              <Route
                element={
                  <ProtectedRoute>
                    <Projects />
                  </ProtectedRoute>
                }
                path="/projects"
              />
              <Route
                element={
                  <ProtectedRoute>
                    <Calendar />
                  </ProtectedRoute>
                }
                path="/calendar"
              />
              <Route
                element={
                  <ProtectedRoute>
                    <AdminProtectedRoute>
                      <Users />
                    </AdminProtectedRoute>
                  </ProtectedRoute>
                }
                path="/users"
              />
              <Route
                element={
                  <ProtectedRoute>
                    <MyTasks />
                  </ProtectedRoute>
                }
                path="/my-tasks"
              />
              <Route
                element={
                  <ProtectedRoute>
                    <Profile />
                  </ProtectedRoute>
                }
                path="/profile"
              />
              <Route
                element={
                  <ProtectedRoute>
                    <Drive />
                  </ProtectedRoute>
                }
                path="/drive"
              />
              <Route
                element={
                  <ProtectedRoute>
                    <Chat />
                  </ProtectedRoute>
                }
                path="/chat"
              />
              <Route
                path="/projects/:projectId/kanban"
                element={
                  <ProtectedRoute>
                    <KanbanBoard />
                  </ProtectedRoute>
                }
              />
              <Route path="/set-password" element={<PasswordSetup />} />
              <Route element={<Signin />} path="/signin" />
              <Route element={<NotFound />} path="*" />
            </Routes>
          </div>
        </div>
      </div>
    </>
  );
}

export default App;
