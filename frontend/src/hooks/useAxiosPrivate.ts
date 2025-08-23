import { axiosWithCredentials as axiosPrivate } from "@/lib/axios";
import useRefreshToken from "./useRefreshToken";
import useAuth from "./useAuth";
import { useEffect } from "react";

const useAxiosPrivate = () => {
	const refresh = useRefreshToken();
	const { accessToken } = useAuth();

	useEffect(() => {
		const requestIntercept = axiosPrivate.interceptors.request.use(
			(config) => {
				if (!config.headers["Authorization"]) {
					config.headers["Authorization"] = `Bearer ${accessToken}`;
				}
				return config;
			},
			(err) => {
				Promise.reject(err);
			}
		);

		const responseInercept = axiosPrivate.interceptors.response.use(
			(res) => res,
			async (error) => {
				const prevRequest = error?.config;
				console.log(error?.response?.data?.errorCode);
				if (
					error?.response?.status === 403 &&
					error?.response?.data?.errorCode ===
						"INVALID_ACCESS_TOKEN" &&
					!prevRequest?.sent
				) {
					prevRequest.sent = true;
					const newAccessToken = await refresh();
					prevRequest.headers[
						"Authorization"
					] = `Bearer ${newAccessToken}`;
					return axiosPrivate(prevRequest);
				}
				return Promise.reject(error);
			}
		);

		return () => {
			axiosPrivate.interceptors.response.eject(responseInercept);
			axiosPrivate.interceptors.request.eject(requestIntercept);
		};
	}, [accessToken, refresh]);

	return axiosPrivate;
};

export default useAxiosPrivate;
