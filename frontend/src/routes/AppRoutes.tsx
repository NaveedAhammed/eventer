import { createBrowserRouter } from "react-router-dom";

import AppLayout from "@/layouts/AppLayout";
import Register from "@/features/auth/pages/Register";
import Login from "@/features/auth/pages/Login";
import Home from "@/features/home/pages/Home";
import OAuthSuccess from "@/features/auth/pages/OAuthSuccess";
import PersistLogin from "@/layouts/PersistLogin";
import RequireAuth from "@/layouts/RequireAuth";
import OAuthFailure from "@/features/auth/pages/OAuthFailure";
import AuthFallback from "@/layouts/AuthFallback";

export const router = createBrowserRouter([
	{
		element: <PersistLogin />,
		children: [
			{
				element: <AppLayout />,
				children: [
					{
						element: <AuthFallback />,
						children: [
							{
								path: "/register",
								element: <Register />,
							},
							{
								path: "/login",
								element: <Login />,
							},
							{
								path: "/oauth2/success",
								element: <OAuthSuccess />,
							},
							{
								path: "/oauth2/failure",
								element: <OAuthFailure />,
							},
						],
					},
					{
						element: <RequireAuth />,
						children: [
							{
								path: "/",
								element: <Home />,
							},
						],
					},
				],
			},
		],
	},
]);
