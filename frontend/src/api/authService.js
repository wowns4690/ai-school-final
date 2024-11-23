// 인증 관련 로직(로그아웃, 로그인 상태 확인)을 담당
export const getLoginStatus = () => {
    const token = sessionStorage.getItem('token');
    const userId = sessionStorage.getItem('userId');
    return token && userId ? { isLoggedIn: true, userId } : { isLoggedIn: false, userId: '' };
};

export const performLogout = () => {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('userId');
};


