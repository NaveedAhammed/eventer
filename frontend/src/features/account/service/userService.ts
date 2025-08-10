import type { UserResponse } from "../types/user";
import type { AxiosInstance } from "axios";

const userService = (axiosPrivate: AxiosInstance) => ({
	async getMe(): Promise<UserResponse> {
		const response = await axiosPrivate.get("/users/me");
		return response.data;
	},
});

export default userService;
