import { axiosWithCredentials } from "@/lib/axios";
import useAuth from "./useAuth";

const useRefreshToken = () => {
	const { setAccessToken } = useAuth();

	const refresh = async () => {
		const response = await axiosWithCredentials.get("/refresh-token");
		setAccessToken(response.data.access_token);
		return response.data.access_token;
	};

	return refresh;
};

export default useRefreshToken;
