import { axiosWithCredentials } from "@/lib/axios";
import useAuth from "./useAuth";

const USERS_SERVICE_BASE_URL = import.meta.env.VITE_API_USERS_SERVICE_BASE_URL;

const useRefreshToken = () => {
	const { setAccessToken } = useAuth();

	const refresh = async () => {
		const response = await axiosWithCredentials.get(
			`${USERS_SERVICE_BASE_URL}/auth/refresh-access-token`
		);
		setAccessToken(response.data.access_token);
		return response.data.access_token;
	};

	return refresh;
};

export default useRefreshToken;
