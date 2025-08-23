import AuthContext from "@/context/AuthContext";
import type { User } from "@/types/types";
import { useState, type ReactNode } from "react";

const AuthProvider = ({ children }: { children: ReactNode }) => {
	const [user, setUser] = useState<User | null>(null);
	const [accessToken, setAccessToken] = useState<string | null>(null);

	return (
		<AuthContext.Provider
			value={{
				isAuthenticated: accessToken ? true : false,
				accessToken: accessToken || null,
				user: user || null,
				setAccessToken,
				setUser,
			}}
		>
			{children}
		</AuthContext.Provider>
	);
};

export default AuthProvider;
