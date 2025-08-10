import useAuth from "@/hooks/useAuth";
import useAxiosPrivate from "@/hooks/useAxiosPrivate";
import { useEffect, useState } from "react";
import { Outlet } from "react-router-dom";

const PersistLogin = () => {
	const { user, setUser } = useAuth();
	const axiosPrivate = useAxiosPrivate();
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const fetchMe = async () => {
			try {
				setLoading(true);
				const res = await axiosPrivate.get("/fetch/me");
				setUser(res.data);
			} catch (err) {
				console.log("No valid session found", err);
			} finally {
				setLoading(false);
			}
		};

		if (!user) {
			fetchMe();
		}
	}, [user, axiosPrivate, setUser]);

	if (loading) return <p>Loading session...</p>;

	return <Outlet />;
};

export default PersistLogin;
