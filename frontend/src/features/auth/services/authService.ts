import { axiosWithCredentials } from "@/lib/axios";
import type {
	LoginRequest,
	OtpVerifyRequest,
	RegisterRequest,
} from "../types/auth";
import type { AuthResponse } from "@/types/types";

const authService = {
	async login(data: LoginRequest): Promise<AuthResponse> {
		const response = await axiosWithCredentials.post("/login", data);
		return response.data;
	},

	async register(data: RegisterRequest): Promise<AuthResponse> {
		const response = await axiosWithCredentials.post("/register", data);
		console.log(response.data);
		return response.data;
	},

	async getOtp(mobile: string): Promise<boolean> {
		const response = await axiosWithCredentials.post("/auth/send-otp", {
			mobile,
		});
		return response.status === 200;
	},

	async verifyOtp(data: OtpVerifyRequest): Promise<string> {
		const response = await axiosWithCredentials.post(
			"/auth/verify-otp",
			data
		);
		return response.data.accessToken;
	},
};

export default authService;
