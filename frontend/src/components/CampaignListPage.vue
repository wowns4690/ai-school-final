<template>
  <div class="page-container">
    <!-- 왼쪽 섹션: 슬라이드 및 생성하기 버튼 -->
    <div class="carousel-container">
      <h5><b><font size="6">CAMPAIGN LIST</font></b></h5>
      <h5><font size="2" style="color:gray;">캠페인 목록</font></h5>
      <div class="carousel-wrapper">
        <div class="carousel" :style="{ transform: `translateX(-${currentIndex * 100}%)`, transition: 'transform 0.5s ease-in-out' }">
          <div
              v-for="(slide, slideIndex) in paginatedItems"
              :key="slideIndex"
              class="carousel-item"
          >
            <div v-for="(item, itemIndex) in slide"
                 :key="itemIndex"
                 class="carousel-content"
                 :class="{ 'selected': selectedItem && selectedItem.campaignId === item.campaignId }"
                 @click="navigateToDetail(item.campaignId)">
              <p><b>Product</b></p>
              <h1>{{ item.product }}</h1>
              <p><b>Keywords</b></p>
              <br>
              <p v-for="(keyword, index) in item.keywords.split(',')" :key="index">{{ keyword }}</p>
              <br>
              <p><b>About Product</b></p>
              <br>
              <p>{{item.features}}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="carousel-buttons">
        <button class="carousel-button prev" @click="prevSlide">〈</button>
        <button class="carousel-button next" @click="nextSlide">〉</button>
      </div>
      <button class="create-button" @click="createItem">새 캠페인 생성</button>
    </div>

    <!-- DETAILS 제목 -->

    <!-- 오른쪽 섹션: 상세 정보 -->
    <div class="detail-view" v-if="selectedItem">

      <template v-if="isEditing">
        <!-- 수정 모드 -->
        <h1>{{ editedItem.product }}</h1>
        <div class="detail-row">
          <div class="detail-column">
            <p><b>Keywords</b></p>
            <p v-for="(keyword, index) in editedItem.keywords.split(',')" :key="index">{{ keyword }}</p>
          </div>
          <div class="detail-column">
            <p><b>About Product</b></p>
            <p>{{ editedItem.features }}</p>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-column">
            <p><strong>Brand <br> </strong>{{editedItem.brand}}</p>
          </div>
          <div class="detail-column">
            <p><strong>Tone <br> </strong>{{editedItem.tone}}</p>
          </div>
        </div>
        <br>
        <p><strong>Brand Model <br> </strong>{{ editedItem.brand_model }}</p>
        <br>
        <br>
        <div class="detail-row">
          <div class="detail-column">
          <p class="detail-column-center"><strong>광고문구 </strong></p>
            <ol>
                <textarea v-model="editedItem.ad_text" class="large-textarea"/>
            </ol>
          </div>
          <div class="detail-column">
            <p class="detail-column-center"><strong>이미지 </strong></p>
            <div class="image-container">
              <!-- 로딩 중이 아닐 때만 이미지 표시 -->
              <img v-if="!isLoading" :src="editedItem.image_url" :key="imageKey" alt="이미지 없음" class="editable-image"/>
              <p><textarea class="small-textarea" v-model="image_prompt" placeholder="본인이 원하는 이미지를 자세하게 입력하세요."/></p>
              <!-- 로딩 중일 때 프로그래스바와 진행도 표시 -->
              <div v-if="isLoading" class="loading-wrapper">
                <div class="loading-bar"
                     :style="{ background: `linear-gradient(to right, #42b983 ${progress}%, white ${progress}%)` }"></div>
                <span class="loading-text">{{ progress }}%</span>
              </div>
              <button @click="updateImage" class="image-update-button" :disabled="isLoading">
                {{ isLoading ? "이미지 생성 중..." : "이미지 수정" }}
              </button>
            </div>
          </div>
        </div>
        <div class="button-container">
          <button class="save-button" @click="saveEdit">저장</button>
          <button class="cancel-button" @click="cancelEdit">취소</button>
        </div>
      </template>

      <template v-else>
        <!-- 보기 모드 -->
        <h1>{{ selectedItem.product }}</h1>
        <div class="detail-row">
          <div class="detail-column">
            <p><b>Keywords</b></p>
            <p v-for="(keyword, index) in selectedItem.keywords.split(',')" :key="index">{{ keyword }}</p>
          </div>
          <div class="detail-column">
            <p><b>About Product</b></p>
            <p>{{ selectedItem.features }}</p>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-column">
            <p><strong>Brand <br> </strong>{{selectedItem.brand}}</p>
          </div>
          <div class="detail-column">
            <p><strong>Tone <br> </strong>{{selectedItem.tone}}</p>
          </div>
        </div>
        <br>
        <p><strong>Brand Model <br> </strong>{{ selectedItem.brand_model }}</p>
        <br>
        <br>
        <div class="detail-row">
          <div class="detail-column">
            <p class="detail-column-center"><strong>광고문구 </strong></p>
            <ol>
              <li v-for="(textPart, index) in splitAdTextFiltered" :key="index"><br>{{ textPart }}</li>
            </ol>
          </div>
          <div class="detail-column">
            <p class="detail-column-center"><strong>이미지 </strong> <img :src="selectedItem.image_url" :key="imageKey" alt="이미지 없음" class="editable-image"/></p>
          </div>
        </div>
        <div class="button-container">
          <button class="edit-button" @click="editItem">수정</button>
          <button class="delete-button" @click="deleteItem(selectedItem.userId, selectedItem.campaignId)">삭제</button>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import {DeleteCampaign, GetCampaignList, UpdateCampaign} from "@/api/CampaignService";
import {onlyImage} from "@/api/GptService";

export default {
  data() {
    return {
      items: [], // 카드 아이템의 데이터
      currentIndex: 0,
      selectedItem: null, // 선택된 아이템을 저장할 변수
      editedItem : null,
      isEditing: false,
      image_prompt: "",
      isLoading: false,
      progress: 0,
      imageKey : new Date().getTime(),
    };
  },
  computed: {
    paginatedItems() {
      // 3개씩 아이템을 슬라이드로 나눔
      const chunkSize = 2;
      const slides = [];
      for (let i = 0; i < this.items.length; i += chunkSize) {
        slides.push(this.items.slice(i, i + chunkSize));
      }
      return slides;
    },
    splitAdText() {
      return this.selectedItem && this.selectedItem.ad_text ? this.selectedItem.ad_text.split(/\d\./) : [];
    },
    splitAdTextFiltered() {
      return this.splitAdText.filter(textPart => textPart.trim() !== "").map((textPart, index) => `${index + 1}.${textPart.trim()}`);
    },
  },
  methods: {
    // 캠페인 리스트 가져오기
    async GetCampaignList() {
      try {
        const response = await GetCampaignList();
        this.items = response;
      } catch (error) {
        console.error("캠페인 목록을 가져오는 중 오류:", error);
      }
    },
    nextSlide() {
      if (this.currentIndex < this.paginatedItems.length - 1) {
        this.currentIndex++;
      } else {
        this.currentIndex = 0; // 마지막 슬라이드이면 처음으로 돌아감
      }
    },
    prevSlide() {
      if (this.currentIndex > 0) {
        this.currentIndex--;
      } else {
        this.currentIndex = this.paginatedItems.length - 1; // 첫 번째 슬라이드이면 마지막으로 돌아감
      }
    },
    createItem() {
      this.$router.push("/CampaignPage");
    },
    editItem() {
      this.isEditing = true;

      const adTextParts = this.selectedItem.ad_text.split(/\d\./).filter(textPart => textPart.trim() !== "");
      this.editedItem.ad_text = adTextParts.map((textPart, index) => `${index + 1}.${textPart.trim()}`).join('\n\n');
    },
    async updateImage() {
      this.isLoading = true;
      this.progress = 0;
      const interval = setInterval(() => {
        if (this.progress < 100) {
          this.progress += 1;
        }
      }, 200);
      try {
        this.editedItem.image_url = await onlyImage(this.image_prompt);
        this.imageKey = new Date().getTime();
      } catch (error) {
        console.error("이미지 업데이트 실패 : ", error);
      } finally {
        clearInterval(interval);
        this.progress = 100;
        setTimeout(() => {
          this.isLoading = false;
          this.progress = 0;
        }, 500);

      }
    },
    async saveEdit() {
      if (window.confirm("캠페인을 정말로 수정하겠습니까?")) {
        const adTextParts = this.editedItem.ad_text.split(/\d\./).filter(textPart => textPart.trim() !== "");
        this.editedItem.ad_text = adTextParts.map((textPart, index) => `${index + 1}.${textPart.trim()}`).join('');
        try{
          await UpdateCampaign(this.editedItem);
          this.selectedItem = JSON.parse(JSON.stringify(this.editedItem));
          const index = this.items.findIndex(item => item.campaignId === this.editedItem.campaignId);
          if(index !== -1){
            this.$set(this.items, index, JSON.parse(JSON.stringify(this.editedItem)));
          }
          this.imageKey = new Date().getTime();
          this.isEditing = false;
        } catch (error){
          console.log("업데이트 중 오류 : ",error);
        }
      }
    },
    cancelEdit() {
      this.editedItem = JSON.parse(JSON.stringify(this.selectedItem));
      this.image_prompt = "";
      this.isEditing = false;
    },
    async deleteItem(userId, campaignId) {
      if (window.confirm("캠페인을 정말로 삭제하시겠습니까?")) {
        try {
          const response = await DeleteCampaign(userId, campaignId);
          console.log("Response 객체:", response);

          // 응답이 204라면 items 배열 업데이트
          if (response && response.status === 204) {

            // 새로운 배열 생성 후 Vue.set으로 items 갱신
            const updatedItems = this.items.filter(item => item.campaignId !== campaignId);
            this.$set(this, 'items', updatedItems);

            // 선택된 항목이 삭제된 항목이라면, 선택된 항목 초기화
            if (this.selectedItem && this.selectedItem.campaignId === campaignId) {
              this.selectedItem = null;
            }
            console.log("삭제 후 items:", this.items);
          } else {
            console.log("삭제 요청 응답이 예상과 다릅니다:", response);
          }
        } catch (error) {
          console.error("삭제 중 오류 발생:", error);
          alert("캠페인 삭제에 실패했습니다.");
        }
      }
    },
    navigateToDetail(campaignId) {
      // 아이템의 상세 정보 출력하기
      if(this.selectedItem == null){
        this.selectedItem = this.items.find(item => item.campaignId === campaignId);
      }else if(this.selectedItem && this.selectedItem.campaignId !== campaignId) {
        this.selectedItem = this.items.find(item => item.campaignId === campaignId);
      } else if(this.selectedItem && this.selectedItem.campaignId === campaignId) {
        this.selectedItem = null;
      }
      this.editedItem = JSON.parse(JSON.stringify(this.selectedItem));
      this.isEditing = false;

    },
  },
  created() {
    this.GetCampaignList();
  },
};
</script>

<style scoped>
.page-container {
  display: flex; /* 가로 정렬 */
  width: 100%;
  height: 100vh; /* 화면 전체 높이 */
  background-color: #fff;
}

.carousel-container {
  flex: 1; /* 왼쪽 섹션 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 20px;
  overflow : hidden;
}

.carousel-wrapper {
  width: 100%;
  overflow: hidden;
  margin-bottom: 10px; /* 슬라이드와 버튼 사이의 간격 */
}

.carousel {
  display: flex;
  transition: transform 0.5s ease-in-out;
  width: 100%;
}

.carousel-item {
  flex: 0 0 100%;
  display: flex;
  justify-content: space-around;
  padding: 20px;
}

.carousel-content {
  flex: 0 0 45%;
  height: 450px;
  border-radius: 15px;
  background-color: #e3e1e1;
  box-shadow: 5px 5px 20px rgba(0, 0, 0, 0.4);
  padding: 20px;
  overflow: auto;
  cursor: pointer;
}

.carousel-buttons {
  display: flex;
  justify-content: space-between;
  width: 100px; /* 버튼의 간격 조정 */
  margin-bottom: 20px; /* create-button과의 간격 */
}

.carousel-content h1 {
  font-size: 40px;
  font-weight: bold;
  margin: 0 0 30px;
}

.carousel-content h2 {
  font-size: 20px;
  margin: 0 0 30px;
}

.carousel-button {
  border: none;
  background: none;
  font-size: 2rem;
  cursor: pointer;
}


.create-button {
  background-color: #000000;
  color: #f6f6f6;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  font-family: 'MyFont-Bold', sans-serif;
}


.button-container {
  display: flex;
  justify-content: flex-end; /* 오른쪽 정렬 */
  gap: 10px; /* 버튼 사이의 간격 */
  margin-top: auto; /* 하단에 위치 */
}

.save-button,
.cancel-button,
.edit-button,
.delete-button {
  background-color: #36996e; /* 기본 수정 버튼 색상 */
  color: #ffffff;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-family: 'MyFont-Bold', sans-serif;
  width: 120px;
}

.save-button,
.delete-button {
  background-color: #ff0000; /* 삭제 버튼 색상 */
}

.cancel-button,
.edit-button {
  background-color: #000000; /* 수정 버튼 색상 */
}

.create-button:hover,
.cancel-button:hover,
.edit-button:hover,
.delete-button:hover,
.save-button:hover,
.image-update-button:hover {
  box-shadow: 0 1px 10px rgb(0, 0, 0);
}

.detail-view {
  flex: 1.5;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 20px;
  box-shadow: 5px 5px 20px rgba(0, 0, 0, 0.4);
  border-radius: 15px;
  background-color: #f1efef;
  margin: auto 30px;
  height: 700px;
  overflow-y: auto; /* 세로 스크롤 활성화 */
}

.detail-view h1 {
  font-size: 40px;
  font-weight: bold;
  margin: 0 0 30px;
}

.detail-view h5 {
  text-align: center;
}

input, .ad-text-input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 16px;
  line-height: 1.5;
  color: #333;
  background-color: #f9f9f9;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input:focus, .ad-text-input:focus {
  border-color: #42b983;
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.5);
  outline: none;
}

.image-container {
  display: flex;
  flex-direction: column; /* 세로로 정렬 */
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 10px; /* 이미지와 아래 요소 간 간격 추가 */
}

.editable-image {
  max-width: 100%;
  width: 512px;
  height: 512px;
  border: 1px solid #ccc;
  border-radius: 5px;
}


.image-update-button {
  width: 120px; /* 원하는 버튼 너비 고정 */
  padding: 10px; /* 버튼의 내부 여백 */
  background-color: #000000;
  color: #ffffff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-align: center;
  font-size: 14px;
  margin-top: 10px; /* textarea와 버튼 간 간격 추가 */
  font-family: 'MyFont-Bold', sans-serif;
}

/* 로딩 바와 진행도 텍스트 스타일 */
.loading-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
  margin-top: 10px;
}

.loading-bar {
  height: 5px;
  background: linear-gradient(to right, #42b983 0%, white 0%);
  transition: width 0.2s ease;
  flex-grow: 1; /* 남은 공간을 채우도록 설정 */
}

.loading-text {
  margin-left: 10px;
  font-size: 14px;
  color: #333;
}

.detail-row {
  display: flex;
  align-items: flex-start; /* 아이템의 세로 정렬 */
  gap: 20px; /* Keywords와 About Product 사이 간격 */
  margin-bottom: 20px; /* 전체 행 간의 간격 */
}

.detail-column {
  flex: 1; /* 두 컬럼이 동일한 너비를 가짐 */
}

.detail-column-center {
  flex: 1; /* 두 컬럼이 동일한 너비를 가짐 */
  text-align: center;
}

.detail-column p {
  margin: 5px 0; /* 텍스트 간격 조정 */
}

.large-textarea {
  width: 100%; /* 가로 전체 크기 */
  height: 512px; /* 세로 크기 */
  padding: 10px; /* 내부 여백 */
  border: 1px solid #ccc; /* 테두리 색상 */
  border-radius: 5px; /* 둥근 테두리 */
  font-size: 16px; /* 글자 크기 */
  line-height: 1.5; /* 줄 간격 */
  resize: none; /* 크기 조절 비활성화 (원하면 활성화 가능) */
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1); /* 안쪽 그림자 */
  background-color: #f9f9f9; /* 배경색 */
}

.large-textarea:focus {
  border-color: #42b983; /* 포커스 시 테두리 색 변경 */
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.5); /* 포커스 시 그림자 */
  outline: none; /* 기본 포커스 효과 제거 */
}

.small-textarea {
  margin-top: 10px; /* 이미지와 textarea 사이 간격 추가 */
  margin-bottom: 10px; /* textarea와 이미지 수정 버튼 사이 간격 추가 */
  width: 508px; /* 가로 전체 크기 */
  height: 128px; /* 세로 크기 */
  padding: 10px; /* 내부 여백 */
  border: 1px solid #ccc; /* 테두리 색상 */
  border-radius: 5px; /* 둥근 테두리 */
  font-size: 16px; /* 글자 크기 */
  line-height: 1.5; /* 줄 간격 */
  resize: none; /* 크기 조절 비활성화 */
  background-color: #f9f9f9; /* 배경색 */
}

.small-textarea:focus {
  border-color: #42b983; /* 포커스 시 테두리 색 변경 */
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.5); /* 포커스 시 그림자 */
  outline: none; /* 기본 포커스 효과 제거 */
}

.carousel-content:hover {
  box-shadow: 0 1px 20px rgb(0, 0, 0);
}

.carousel-content.selected {
  border: 2px solid #000000; /* 선택된 아이템에 초록색 테두리 추가 */
  box-shadow: 0 1px 20px rgb(0, 0, 0); /* 강조 효과 */
}

</style>