import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function OAuthFailure() {
	const [message, setMessage] = useState(
		"Something went wrong, Please try again!"
	);
	const navigate = useNavigate();

	useEffect(() => {
		const params = new URLSearchParams(window.location.search);
		const errorMessage = params.get("error");

		if (errorMessage) {
			setMessage(errorMessage);
		}

		setTimeout(() => navigate("/login"), 3000);
	}, [navigate]);

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
}

export default OAuthFailure;
