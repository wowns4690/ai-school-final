<template>
  <div class="container"> 
    <div class="header-wrapper">

      <div v-if="!isLoggedIn" class="button-group"> 
        <button class="login-button" @click="goToLogin">로그인</button>
        <button class="signup-button" @click="goToSignup">회원가입</button>
      </div>

      <div v-else class="welcome-wrapper">
        <span class="welcome-message">환영합니다, {{ userId }}님!</span>
        <button class="logout-button" @click="handleLogout">로그아웃</button>
      </div>

    </div>
  </div>
</template>

<script>
import { getLoginStatus, performLogout } from '@/api/authService.js'; // 변경된 이름 사용

export default {
  data() {
    return {
      userId: '', // 사용자 아이디
      isLoggedIn: false // 로그인 상태
    };
  },
  mounted() {
    const { isLoggedIn, userId } = getLoginStatus(); // 변경된 함수 호출
    this.isLoggedIn = isLoggedIn;
    this.userId = userId;
  },
  methods: {
    goToLogin() {
      this.$router.push('/login'); 
    },
    goToSignup() {
      this.$router.push('/signup'); 
    },
    handleLogout() {
      performLogout(); // 변경된 함수 호출
      this.isLoggedIn = false;
      this.userId = '';
      this.$router.push('/');
    }
  }
};
</script>



<style scoped>
/* 전체 컨테이너 */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f3fb;
}

/* 헤더 영역 */
.header-wrapper {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 15px;
  position: absolute;
  top: 20px;
  right: 20px;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  gap: 10px;
}

/* 로그인 및 회원가입 버튼 스타일 */
.login-button, .signup-button {
  background-color: #42b983;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

/* 버튼 호버 시 스타일 */
.login-button:hover, .signup-button:hover {
  background-color: #36996e;
}

/* 환영 메시지 및 로그아웃 버튼 */
.welcome-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 환영 메시지 */
.welcome-message {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

/* 로그아웃 버튼 스타일 */
.logout-button {
  background-color: #f56565;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

/* 로그아웃 버튼 호버 시 스타일 */
.logout-button:hover {
  background-color: #e53e3e;
}
</style>
