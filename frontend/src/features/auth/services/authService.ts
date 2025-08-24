import { axiosWithCredentials } from "@/lib/axios";
import type { LoginRequest, RegisterRequest } from "../types/types";
import type { User } from "@/types/types";

const AUTH_SERVICE_BASE_URL = import.meta.env.VITE_AUTH_SERVICE_BASE_URL;
const USERS_SERVICE_BASE_URL = import.meta.env.VITE_USERS_SERVICE_BASE_URL;

const authService = {
	async login(data: LoginRequest): Promise<string> {
		const response = await axiosWithCredentials.post(
			`${AUTH_SERVICE_BASE_URL}/login`,
			data
		);
		return response.data.access_token;
	},

	async register(data: RegisterRequest): Promise<string> {
		const response = await axiosWithCredentials.post(
			`${AUTH_SERVICE_BASE_URL}/register`,
			data
		);
		return response.data;
	},

	async fetchProfile(access_token: string): Promise<User> {
		const response = await axiosWithCredentials.get(
			`${USERS_SERVICE_BASE_URL}/fetch-profile`,
			{
				headers: {
					Authorization: `Bearer ${access_token}`,
				},
			}
		);
		return response.data;
	},
};

export default authService;
