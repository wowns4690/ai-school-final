import axios from 'axios';

//const API_URL = `http://${process.env.VUE_APP_API_URL}/api/user`;
 const API_URL = `http://localhost:8080/api/user`;
export const Login = async (userId, userPassword) => {
    return await axios.post(API_URL + "/login", {
        userId: userId,
        userPassword : userPassword
    });
};

export const extendLogin = async () => {
    const token = sessionStorage.getItem('token');
    try {
        const response = await axios.post(
            `${API_URL}/extendLogin`,
            { token }, // 요청 본문에 JSON 형식으로 token 포함
            {
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error extending login:", error);
        throw error;
    }
};