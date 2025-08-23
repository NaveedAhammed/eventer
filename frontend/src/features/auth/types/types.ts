export interface RegisterRequest {
	username: string;
	email: string;
	password: string;
	role: string;
}

export interface LoginRequest {
	usernameOrEmail: string;
	password: string;
}
