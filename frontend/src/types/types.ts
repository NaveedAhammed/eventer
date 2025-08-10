export interface ErrorResponse {
	message: string;
	errorCode: string;
}

export interface User {
	id: string;
	email: string;
	name: string;
	role: string;
	avatar?: string;
}

export interface AuthResponse {
	user: User;
	accessToken: string;
}
