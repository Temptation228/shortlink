import axios from 'axios'

export const serverApi = axios.create({
	baseURL:	process.env.APP_BACKEND,
});