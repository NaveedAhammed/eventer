import type { User } from "@/types/types";
import { createContext, type Dispatch, type SetStateAction } from "react";

export interface AuthState {
	isAuthenticated: boolean;
	accessToken: string | null;
	user: User | null;
	setAccessToken: Dispatch<SetStateAction<string | null>>;
	setUser: Dispatch<SetStateAction<User | null>>;
}

const AuthContext = createContext<AuthState | null>(null);

export default AuthContext;
