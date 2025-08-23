export interface ErrorResponse {
	timestamp: string;
	message: string;
	status: number;
	errorCode: string;
}

export interface User {
	userId: string;
	email: string;
	username: string;
	firstName: string;
	lastName?: string;
	role: string;
	authProvider: string;
	profilePicture?: string;
}
