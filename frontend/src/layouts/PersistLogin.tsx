import useAuth from "@/hooks/useAuth";
import useAxiosPrivate from "@/hooks/useAxiosPrivate";
import { useEffect, useState } from "react";
import { Outlet } from "react-router-dom";

const USERS_SERVICE_BASE_URL = import.meta.env.VITE_USERS_SERVICE_BASE_URL;

const PersistLogin = () => {
	const axiosPrivate = useAxiosPrivate();
	const [loading, setLoading] = useState(true);
	const { setUser } = useAuth();

	useEffect(() => {
		const fetchMe = async () => {
			try {
				setLoading(true);
				const res = await axiosPrivate.get(
					`${USERS_SERVICE_BASE_URL}/fetch-profile`
				);
				setUser(res.data);
			} catch (err) {
				console.log("No valid session found", err);
			} finally {
				setLoading(false);
			}
		};

		fetchMe();
	}, [axiosPrivate, setUser]);

	if (loading) return <p>Loading session...</p>;

	return <Outlet />;
};

export default PersistLogin;
