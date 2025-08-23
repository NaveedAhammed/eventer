import { z } from "zod";

export const loginSchema = z.object({
	usernameOrEmail: z
		.string()
		.nonempty({ message: "Username or Email is required" }),
	password: z.string().nonempty({ message: "Password is required" }),
});

export type LoginSchema = z.infer<typeof loginSchema>;
