import useAuth from "@/hooks/useAuth";
import { Navigate, Outlet, useLocation } from "react-router-dom";

function RequireAuth() {
	const { accessToken } = useAuth();
	const location = useLocation();

	return accessToken ? (
		<Outlet />
	) : (
		<Navigate to="/login" state={{ from: location }} replace />
	);
}

export default RequireAuth;
