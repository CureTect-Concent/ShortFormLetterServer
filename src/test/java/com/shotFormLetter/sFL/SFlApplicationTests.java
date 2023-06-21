package com.shotFormLetter.sFL;

import com.shotFormLetter.sFL.domain.post.controller.PostController;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.assertj.core.api.Assertions.assertThat;


import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class SFlApplicationTests {

	@Autowired
	private PostController postController;
	@Autowired
	private MockMvc mockMvc;


	@Test
	public void testSignupSuccess() throws Exception {
		String requestBody = "{\"username\": \"usr1\", \"user_nickname\": \"deumi\", \"password\": \"aaa\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/user/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().string("User created successfully"));
	}

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
