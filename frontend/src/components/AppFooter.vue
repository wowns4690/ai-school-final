<template>
  <div class="footer">
    <div class="content-wrapper">
      <p class="footer-contact" style="color:gray;" >©Hooniping, 김지훈, 강어진, 김지현, 이수민, 한재준</p>
      <p class="footer-info" style="color:gray;" >문의: hooniping@gmail.com</p>

    </div>
  </div>
</template>

<script>
export default {
  name: 'AppFooter',
};
</script>

<style scoped>
body{
  font-family: 'MyFont', sans-serif;
}
.footer {
  display: flex;
  flex-direction: column;
  justify-content: left; 
  align-items: left;    
  padding: 10px 20px;
  background-color: #f4f4f4;
  border-top: 1px solid #ccc;
}

.content-wrapper {
  text-align: left;
  font-family: 'MyFont', sans-serif;
}
.footer-info {
  text-align: left;
}

.footer-contact {
  font-size: 16px; 
  color: #333;
  margin: 5px 0;
}
</style>
