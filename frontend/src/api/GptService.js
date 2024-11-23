import axios from 'axios';

//const API_URL = `http://${process.env.VUE_APP_API_URL}/api/gpt`;
 const API_URL = `http://localhost:8080/api/gpt`;

// 키워드 추출
export const ExtractKeyword = async (product) => {
    const token = sessionStorage.getItem('token');
    try {
        const response = await axios.post(`${API_URL}/keyword`, {
                product: product
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`, // Authorization 헤더에 토큰 추가
                },
            });
        console.log(response.data);
        return response.data;
    } catch (error) {
        console.log(error);
        throw error;
    }
};


//==================== 문구 ======================
// 광고 문구 출력
export const GenerateAdText = async (product, brand, tone, brand_model, features, keywords) => {
    const token = sessionStorage.getItem('token');
    return await axios.post(`${API_URL}/adtext`, {
        product: product,
        brand: brand,
        features: features,
        tone: tone,
        brand_model: brand_model,
        keywords: keywords
    }, 
    {
        headers: {
            Authorization: `Bearer ${token}`, // Authorization 헤더에 토큰 추가
        }
    });
}

//==================== 이미지 =======================
// image 생성 (> s3 버킷에 저장 > 이미지 url 반환)
export const GenerateImageUrl = async (prompt, userId, campaignId) => {
    const token = sessionStorage.getItem('token');
    const response = await axios.post(`${API_URL}/image`, null, {
        params: { 
            prompt: prompt,
            userId: userId,
            campaignId: campaignId
        },
        headers: {
            Authorization: `Bearer ${token}`,
        }
    });
    return response.data;
}

export const onlyImage = async (prompt) => {
    const token = sessionStorage.getItem('token');
    try {
        const response = await axios.post(`${API_URL}/onlyImage`, null, {
            params: { prompt: prompt },
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        console.log(response.data);
        return response.data;
    } catch (error) {
        console.error('이미지 받아오는 중 오류:', error);
        throw error;
    }
};

