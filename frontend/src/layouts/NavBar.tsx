import { Link, NavLink } from "react-router-dom";

import logo from "@/assets/logo.png";
import LinkButton from "@/components/Button/LinkButton";
import useAuth from "@/hooks/useAuth";
import UserMenu from "@/components/UserMenu/UserMenu";
import Button from "@/components/Button/Button";

function NavBar() {
	const { user } = useAuth();

	return (
		<header className="h-16 flex items-center shadow px-4 sticky top-0 bg-white z-50">
			<div className="w-[1200px] mx-auto flex items-center justify-between">
				<Link to="/">
					<div className="h-12 w-12 flex items-center">
						<img
							src={logo}
							alt="Event Booking"
							className="h-full w-full object-cover"
						/>
						<span className="text-2xl font-bold">Eventer</span>
					</div>
				</Link>
				<nav>
					<ul className="flex items-center gap-3 text-sm 2xl:text-base">
						{user != null ? (
							<>
								<li>
									<Button
										variant="ghost"
										className="rounded-full"
									>
										Become an Organizer
									</Button>
								</li>
								<li className="w-9 h-9 rounded-full overflow-hidden">
									<img
										src={user?.profilePictureUrl}
										alt={user.username}
										className="w-full h-full object-cover"
									/>
								</li>
								<li>
									<UserMenu />
								</li>
							</>
						) : (
							<>
								<li>
									<NavLink to="/login?mode=email">
										Login
									</NavLink>
								</li>
								<li>
									<LinkButton to="/register">
										Sign Up
									</LinkButton>
								</li>
							</>
						)}
					</ul>
				</nav>
			</div>
		</header>
	);
}

export default NavBar;
