import Vue from 'vue';
import Router from 'vue-router';
import CampaignPage from '@/components/CampaignPage.vue';
import LoginPage from '@/components/LoginPage.vue';
import MainPage from '@/components/MainPage.vue';
import SignupPage from '@/components/SignupPage.vue';
import CampaignListPage from '@/components/CampaignListPage.vue';
Vue.use(Router);

const router = new Router({
  mode: 'history', // URL에서 해시(#)를 제거하기 위해 history 모드 사용
  routes: [
    {
      path: '/CampaignPage',
      name: 'CampaignPage',
      component: CampaignPage,
    },
    {
      path: '/',
      name: 'LoginPage',
      component: LoginPage,
    },
    {
      path: '/main',
      name: 'MainPage',
      component: MainPage,
    },
    {
      path: '/signup',
      name: 'SignupPage',
      component: SignupPage,
    },
    {
      path :'/CampaignListPage',
      name : "CampaignListPage",
      component : CampaignListPage,
    },
  ],
});

// 라우터 가드 추가
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!sessionStorage.getItem('token'); // 로그인 상태를 확인

  if (to.path === '/signup') {
    // 회원가입 페이지는 로그인 여부와 무관하게 접근 가능
    next();
  } else if (to.meta.requiresAuth && !isLoggedIn) {
    // 인증이 필요한 페이지인데 로그인되지 않은 경우
    next('/'); // 로그인 페이지로 리디렉션
  } else if (to.path === '/' && isLoggedIn) {
    // 로그인 상태에서 로그인 페이지로 이동하려고 하면
    next('/CampaignListPage'); // 캠페인 페이지로 리디렉션
  } else {
    next(); // 그 외의 경우는 정상적으로 진행
  }
});

export default router;
