package com.shotFormLetter.sFL;

import com.shotFormLetter.sFL.domain.post.controller.PostController;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.InstanceOfAssertFactories.atomicIntegerFieldUpdater;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.assertj.core.api.Assertions.assertThat;


import java.util.Collections;
import java.util.jar.JarException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class SFlApplicationTests {


    @Test
    public void newJson(){
        String data="[{'ref':'a', 'type':'VIDEO'}]";
        String data2="[{'ref':'b', 'type':'IMAGE'}]";
        try {
            // geturls를 JSONArray로 변환
            JSONArray urls = new JSONArray(data);

            // new_media_reference를 JSONArray로 변환
            JSONArray new_urls = new JSONArray(data2);

            // new_urls의 요소를 urls에 추가
            for (int i = 0; i < new_urls.length(); i++) {
                urls.put(new_urls.getJSONObject(i));
            }

            // 결과 출력
            System.out.println(urls.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//	@Autowired
//	private PostController postController;
//	@Autowired
//	private MockMvc mockMvc;


//	@Test
//	public void testJson() {
//		String data= "[{'ref':'a', 'type':'IMAGE'},{'ref':'b', type:'IMAGE'}]";
//		String url="example.com";
//		JSONArray jsonArray=new JSONArray(data);
//		try{
//			for(int i=0; i<jsonArray.length(); i++){
//				JSONObject jsonObject=jsonArray.getJSONObject(i);
//				jsonObject.put("s3url",url);
//			}
//		}catch (JSONException e){
//			e.printStackTrace();
//		}
//		System.out.println(jsonArray);
//
//	}

//	@Test
//	public void testSignupConflict() throws Exception {
//		String requestBody = "{\"username\": \"existingUser\", \"userNickname\": \"deumi\", \"password1\": \"aaa\"}";
//
//		mockMvc.perform(post("/signup")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(requestBody))
//				.andExpect(status().isConflict())
//				.andExpect(content().string("User already exists"));
//	}
//	@Test
//	public void testCreatePost() {
//		// Given
//		String content = "포스트 내용1";
//
//		// When
//		ResponseEntity<Post> response = postController.createPost(Collections.singletonMap("content", content));
//
//		// Then
//		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//		Post createdPost = response.getBody();
//		assertThat(createdPost).isNotNull();
//		assertThat(createdPost.getContent()).isEqualTo(content);
//		// 추가적인 검증 로직을 작성할 수 있습니다.
//	}
//
//	// 다른 테스트 메서드를 작성할 수 있습니다.
//	@Test
//	void contextLoads() {
//	}

}
