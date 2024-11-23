export const authGuard = (to, from, next) => {
    const isLoggedIn = !!sessionStorage.getItem('token'); // 로그인 상태 확인

    if (to.matched.some((record) => record.meta.requiresAuth) && !isLoggedIn) {
        next('/login'); // 로그인 안 된 경우 로그인 페이지로 이동
    } else {
        next(); // 로그인 상태면 이동 허용
    }
};
