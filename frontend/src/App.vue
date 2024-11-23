<template>
  <div id="app">
    <div class="app-container">

      <div v-if="isLoggedIn" class="header-wrapper">
        <div class="left-section">
          <img
            src="@/assets/KakaoTalk_20240926_155821857.jpg"
            alt="Logo"
            class="header-logo"
            @click="goToCampaignListPage"
          />
        </div>
        <div class="right-section">
          <span class="welcome-message">WELCOME, {{ userId }}!</span>
          <div class="time-wrapper">
            <span class="time-icon">⏰</span>
            <span class="time-remaining">{{ timeRemaining }}</span>
            <button class="extend-button" @click="extendLogin">연장</button>
          </div>
          <button class="logout-button" @click="handleLogout">LOGOUT</button>
        </div>
      </div>

      <router-view />

      <AppFooter />
    </div>
  </div>
</template>


<script>
import AppFooter from '@/components/AppFooter.vue';
import { getLoginStatus, performLogout } from '@/api/authService.js';
import {extendLogin} from "@/api/loginService";
import jwt_decode from 'jsonwebtoken';

export default {
  components: {
    AppFooter,
  },
  data() {
    return {
      userId: '',
      isLoggedIn: false,
      timeRemaining : '',
      intervalId: null,
    };
  },
  mounted() {
    this.updateLoginStatus();
    this.startTokenTimer();
  },
  beforeDestroy() {
    clearInterval(this.intervalId);
  },
  methods: {
    async extendLogin(){
      const response = await extendLogin()
      if(response != null){
        sessionStorage.setItem('token', response);
      }
    },
    updateLoginStatus() {
      const { isLoggedIn, userId } = getLoginStatus();
      this.isLoggedIn = isLoggedIn;
      this.userId = userId;

      if(isLoggedIn){
        this.startTokenTimer();
      }else{
        clearInterval(this.intervalId);
        this.timeRemaining = '';
      }
    },
    handleLogout() {
      performLogout();
      this.isLoggedIn = false;
      this.userId = '';
      clearInterval(this.intervalId);
      this.timeRemaining = '';

      // 현재 경로가 '/'이 아닐 때만 이동
      if (this.$route.path !== '/') {
        this.$router.push('/');
      }
    },
    goToCampaignListPage() {
      // 현재 경로가 "/campaignlistpage" 인지 확인
      if (this.$route.path !== '/campaignlistpage') {
        this.$router.push('/campaignlistpage'); // 여기에서 'pust'를 'push'로 수정
      }
    },
    startTokenTimer() {
      // 기존 타이머가 있다면 정리
      if (this.intervalId) {
        clearInterval(this.intervalId);
      }

      // 1초마다 남은 시간을 계산하는 타이머 설정
      this.intervalId = setInterval(() => {
        const token = sessionStorage.getItem('token');
        if (token) {
          const decoded = jwt_decode.decode(token);
          if (decoded && decoded.exp) {
            const currentTime = Math.floor(Date.now() / 1000);
            const timeLeft = decoded.exp - currentTime;
            if (timeLeft > 0) {
              const minutes = Math.floor(timeLeft / 60);
              const seconds = timeLeft % 60;
              this.timeRemaining = `${minutes}분 ${seconds}초`;
            } else {
              this.timeRemaining = '토큰 만료';
              this.handleLogout();
            }
          }
        }
      }, 1000);
    },
  },
  watch: {
    // 라우트 변경을 감지하고 로그인 상태 업데이트
    $route() {
      this.updateLoginStatus();
    },
  },
};
</script>

<style scoped>
body{
  font-family: 'MyFont', sans-serif;
}
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f0f3fb;
  justify-content: space-between;
}

.header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #f4f4f4;
  border-bottom: 1px solid #ccc;
}

.left-section {
  display: flex;
  align-items: center;
}

.header-logo {
  width: 50px;
  height: 50px;
  cursor: pointer;
}

.header-logo:hover {
  box-shadow: 0 1px 20px rgb(0, 0, 0);
}

.right-section {
  display: flex;
  align-items: center;
  gap: 25px;
  font-family: 'MyFont', sans-serif;
}

.welcome-message {
  font-size: 16px;
  color: #1a1919;
}

.time-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-icon {
  font-size: 16px;
}

.time-remaining {
  font-size: 14px;
  color: #999;
}

.extend-button {
  background-color: #999;
  color: white;
  border: none;
  border-radius: 15px;
  padding: 5px 10px;
  font-size: 14px;
  cursor: pointer;
}

.extend-button:hover {
  background-color: #494040;
}

.logout-button {
  background-color: transparent;
  color: #1a1919;
  border: none;
  font-size: 14px;
  cursor: pointer;
}

.logout-button:hover {
  color: #1a1919;
  text-decoration: underline;
}
</style>


