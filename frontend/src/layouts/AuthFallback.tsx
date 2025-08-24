import useAuth from "@/hooks/useAuth";
import { useEffect } from "react";
import { Outlet, useNavigate } from "react-router-dom";

function AuthFallback() {
	const { user } = useAuth();
	const navigate = useNavigate();

	useEffect(() => {
		if (user != null) {
			navigate("/", { replace: true });
		}
	}, [user, navigate]);

	return <Outlet />;
}

export default AuthFallback;
