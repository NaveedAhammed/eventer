import useAuth from "@/hooks/useAuth";
import { axiosWithCredentials } from "@/lib/axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const USERS_SERVICE_BASE_URL = import.meta.env.VITE_USERS_SERVICE_BASE_URL;

const OAuthSuccess = () => {
	const [message, setMessage] = useState("Logging you in...");
	const navigate = useNavigate();

	const { setAccessToken, setUser } = useAuth();

	useEffect(() => {
		const params = new URLSearchParams(window.location.search);
		const accessToken = params.get("access_token");

		if (!accessToken) {
			setMessage("No token found. Redirecting to login...");
			setTimeout(() => navigate("/login"), 1500);
			return;
		}

		setAccessToken(accessToken);

		axiosWithCredentials
			.get(`${USERS_SERVICE_BASE_URL}/profile`, {
				headers: {
					Authorization: `Bearer ${accessToken}`,
				},
			})
			.then((res) => setUser(res.data))
			.catch((err) => console.log(err));

		setMessage("Login successful! Redirecting...");
		setTimeout(() => navigate("/"), 1500);
	}, [navigate, setAccessToken, setUser]);

	return (
		<div className="flex items-center justify-center h-screen">
			<div className="text-center">
				<h2 className="text-2xl font-semibold mb-2">{message}</h2>
				<p className="text-sm text-gray-500">
					You will be redirected shortly.
				</p>
			</div>
		</div>
	);
};

export default OAuthSuccess;
