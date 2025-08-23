import { z } from "zod";

export const registerSchema = z.object({
	username: z
		.string()
		.nonempty({ message: "Name is required" })
		.min(3, { message: "Username should be at least 3 characters" })
		.max(20, { message: "Username cannot be more than 20 characters" })
		.regex(/^[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*$/, {
			message:
				"Username may only contain alphanumeric characters or single hyphens, and cannot begin or end with a hyphen.",
		}),
	email: z
		.string()
		.nonempty({ message: "Email is required" })
		.email({ message: "Invalid email address" }),
	password: z
		.string()
		.nonempty({ message: "Password is required" })
		.regex(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$/, {
			message:
				"Password must be at least 8 characters long, contain one digit, one lowercase, one uppercase, and one special character",
		}),
	role: z.string(),
});

export type RegisterSchema = z.infer<typeof registerSchema>;
