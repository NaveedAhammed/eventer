import axios from "axios";

export default axios.create();

export const axiosWithCredentials = axios.create({
	withCredentials: true,
	headers: {
		"Content-Type": "application/json",
	},
});
