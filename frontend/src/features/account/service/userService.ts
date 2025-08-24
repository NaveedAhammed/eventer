import type { UserResponse } from "../types/user";
import type { AxiosInstance } from "axios";

const USERS_SERVICE_BASE_URL = import.meta.env.VITE_USERS_SERVICE_BASE_URL;

const userService = (axiosPrivate: AxiosInstance) => ({
	async fetchProfile(): Promise<UserResponse> {
		const response = await axiosPrivate.get(
			`${USERS_SERVICE_BASE_URL}/fetch-profile`
		);
		return response.data;
	},
});

export default userService;
