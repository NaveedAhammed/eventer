import { axiosWithCredentials } from "@/lib/axios";
import type { LoginRequest, RegisterRequest } from "../types/types";

const AUTH_SERVICE_BASE_URL = import.meta.env.VITE_AUTH_SERVICE_BASE_URL;

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
};

export default authService;
